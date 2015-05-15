package ru.tcgeo.gilib;

import android.graphics.Bitmap;

public class GIOSMLayer extends GILayer {

	String m_site;
	public GIOSMLayer() 
	{
		m_site = "http://a.tile.openstreetmap.org/";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIOSMRenderer();
		m_projection = GIProjection.WGS84();
	}
	public GIOSMLayer(String path) 
	{
		m_site = "http://a.tile.openstreetmap.org/";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIOSMRenderer();
		m_projection = GIProjection.WGS84();
	}
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity,
			double scale) 
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}
	}

}
