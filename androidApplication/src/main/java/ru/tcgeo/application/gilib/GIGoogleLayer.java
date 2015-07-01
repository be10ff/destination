package ru.tcgeo.application.gilib;


import android.graphics.Bitmap;


public class GIGoogleLayer extends GILayer {

	String m_site;
	public GIGoogleLayer()
	{
		m_site = "http://maps.googleapis.com/";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIGoogleRenderer();
		m_projection = GIProjection.WGS84();
	}
	public GIGoogleLayer(String path) 
	{
		m_site = path;
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIGoogleRenderer();
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
