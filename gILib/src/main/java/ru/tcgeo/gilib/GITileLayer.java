package ru.tcgeo.gilib;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class GITileLayer extends GILayer {

	public ArrayList<GITile> m_tiles;
	
	public GITileLayer(String path) 
	{
		type_ = GILayerType.TILE_LAYER;
		m_renderer = new GITileRenderer();
		m_tiles = new ArrayList<GITile>();
		m_id = initTileLayer(path);
		m_projection = new GIProjection(getTileProjection(m_id), true);
	}
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity, double scale) 
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}

	}
	
	public void add(GITile tile)
	{
		m_tiles.add(tile);
	}
	
	native long initTileLayer(String path);
	native long getTileProjection(long layer_id);
}
