package ru.tcgeo.application.gilib.specs;

import java.util.ArrayList;

import android.graphics.Rect;

import ru.tcgeo.gilib.specs.GISpeedCamera;
import ru.tcgeo.gilib.specs.GITreeTile;


/**
 * Класс бинарного дерева для разбиения по тайлам OSM
 * @author artem
 *
 */

public class GIQuadTreeDouble 
{
	
	//TODO 15
	public static int m_max_level = 15;
	public static int MORTON_LVL = 17;
	
	private ArrayList<GISpeedCamera> m_cameras;
	public ArrayList<ru.tcgeo.gilib.specs.GIQuadTreeDouble> m_brunches;
	GITreeTile m_tile;

	public GIQuadTreeDouble()
	{
    	m_tile = new GITreeTile(0, 0, 0);
		m_brunches = new ArrayList<ru.tcgeo.gilib.specs.GIQuadTreeDouble>();
		m_cameras = new ArrayList<GISpeedCamera>();
	}
	
	protected GIQuadTreeDouble(GITreeTile tile)
	{
		m_tile = tile;
		m_brunches = new ArrayList<ru.tcgeo.gilib.specs.GIQuadTreeDouble>();
		m_cameras = new ArrayList<GISpeedCamera>();
	}
	
	private boolean IsInclude(GITreeTile tile)
	{
		if(tile.m_zoom < m_tile.m_zoom)
		{
			return false;
		}
		else
		{
			int dZ =  tile.m_zoom - m_tile.m_zoom;
			int xMin = (int) (m_tile.m_xtile*Math.pow(2, dZ));
			int xMax = (int) ((m_tile.m_xtile + 1)*Math.pow(2, dZ)) -1;		
			int yMin = (int) (m_tile.m_ytile*Math.pow(2, dZ));
			int yMax = (int) ((m_tile.m_ytile + 1)*Math.pow(2, dZ)) -1;				
			if((xMin <= tile.m_xtile)&&(tile.m_xtile <= xMax)&&(yMin <= tile.m_ytile)&&(tile.m_ytile <= yMax))
			{
				return true;
			}
		}
		return false;
	}

	
	/**
	 * построение квадродерева заданной вложености
	 * или до последнего содержащего объекты уровня
	 */

	public int Sort()
	{
		int res = 1;
		if(m_tile.m_zoom <= m_max_level)
		{

			ArrayList<ru.tcgeo.gilib.specs.GIQuadTreeDouble> brunches = new ArrayList<ru.tcgeo.gilib.specs.GIQuadTreeDouble>();
			brunches.add(new ru.tcgeo.gilib.specs.GIQuadTreeDouble( new GITreeTile(m_tile.m_zoom + 1, m_tile.m_xtile * 2,  m_tile.m_ytile * 2)));
			brunches.add(new ru.tcgeo.gilib.specs.GIQuadTreeDouble( new GITreeTile(m_tile.m_zoom + 1, m_tile.m_xtile * 2 + 1,  m_tile.m_ytile * 2)));
			brunches.add(new ru.tcgeo.gilib.specs.GIQuadTreeDouble( new GITreeTile(m_tile.m_zoom + 1, m_tile.m_xtile * 2,  m_tile.m_ytile * 2 + 1)));
			brunches.add(new ru.tcgeo.gilib.specs.GIQuadTreeDouble( new GITreeTile(m_tile.m_zoom + 1, m_tile.m_xtile * 2 + 1,  m_tile.m_ytile * 2 + 1)));
			for(int i = 0; i < 4; i++)
			{
				ru.tcgeo.gilib.specs.GIQuadTreeDouble branch = brunches.get(i);
				int shape_counter = 0;
				while(shape_counter < m_cameras.size())
				{
					GISpeedCamera camera = m_cameras.get(shape_counter);
					if(branch.IsInclude((camera.getTile())))
					{
						branch.m_cameras.add(camera);
						m_cameras.remove(camera);
					}
					else
					{
						shape_counter++;
					}
				}
				if((branch.m_cameras.size() > 0))
				{
					m_brunches.add(branch);
				}
				if((branch.m_cameras.size() > 0))
				{
					res = res + branch.Sort();
				}
				if(( m_cameras.size() == 0))
				{
					break;
				}
			}
		}
		return res;
	}
	
	public void setShapes(ArrayList<GISpeedCamera> shapes)
	{
		m_cameras = shapes;
	}
	
/**
 * список всех влияющих объектов для заданного кода
 * @param code заданного кода
 * @return список всех влияющих объектов для заданного кода
 */
	public ArrayList<GISpeedCamera> getDependies(GITreeTile tile)
	{
		ArrayList <GISpeedCamera> result = new ArrayList<GISpeedCamera>();
		if(IsInclude(tile))
		{
			if(m_tile.m_zoom == tile.m_zoom)
			{
				return GetAll(result);
			}
			else
			{
				result.addAll(m_cameras);
				for(ru.tcgeo.gilib.specs.GIQuadTreeDouble child : m_brunches)
				{
					result.addAll(child.getDependies(tile));
				}
			}
		}
		return result;
	}
	public ArrayList<GISpeedCamera> GetAll(ArrayList<GISpeedCamera> result)
	{
		result.addAll(m_cameras);
		for(ru.tcgeo.gilib.specs.GIQuadTreeDouble child : m_brunches)
		{
			child.GetAll(result);
		}
		return result;
	}
	
	public static long MortonCode2D(float x, float y)
	{

		int m_dim =  (int) Math.pow(2, MORTON_LVL);
		int length = m_dim - 1;
		
		Rect m_area = new Rect(0, 0, length, length);
		int nX = (int) Math.round((x/m_area.width())*m_dim);
		int nY = (int) Math.round((y/m_area.height())*m_dim);
		return SeparateBy1(nX)|(SeparateBy1(nY) << 1);
	}
	
	private static long SeparateBy1(long x)
	{
		x &= 0x0000ffff;                  // x = ---- ---- ---- ---- fedc ba98 7654 3210
	    x = (x ^ (x <<  8)) & 0x00ff00ff; // x = ---- ---- fedc ba98 ---- ---- 7654 3210
	    x = (x ^ (x <<  4)) & 0x0f0f0f0f; // x = ---- fedc ---- ba98 ---- 7654 ---- 3210
	    x = (x ^ (x <<  2)) & 0x33333333; // x = --fe --dc --ba --98 --76 --54 --32 --10
	    x = (x ^ (x <<  1)) & 0x55555555; // x = -f-e -d-c -b-a -9-8 -7-6 -5-4 -3-2 -1-0
	    return x;
	}

}
