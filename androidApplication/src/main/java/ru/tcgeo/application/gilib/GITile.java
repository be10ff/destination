package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import ru.tcgeo.gilib.*;
import ru.tcgeo.gilib.GIScaleRange;
import ru.tcgeo.gilib.planimetry.Vertex;
import android.graphics.PointF;


public class GITile  
{
	public ArrayList<Vertex> m_points;
	public String m_filename;
	float m_lower;
	float m_upper;
	public ru.tcgeo.gilib.GIScaleRange m_range;

	public GITile(String filename, float lower, float upper)
	{
		m_filename = filename;
		m_lower = lower;
		m_upper = upper;
		m_range = new GIScaleRange(lower, upper);
		m_points = new ArrayList<Vertex>();
	}
	
	public void add(PointF point)
	{
		m_points.add(new Vertex(point));
	}

}
