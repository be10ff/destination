package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;

import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.GIProjection;
import ru.tcgeo.gilib.GIYandexRenderer;

public class GIYandexLayer extends GILayer {

	String m_site;
	public GIYandexLayer() 
	{
		m_site = "http://vec01.maps.yandex.net/tiles";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIYandexRenderer();
		m_projection = GIProjection.WGS84();
	}
	public GIYandexLayer(String path) 
	{
		m_site = "http://vec01.maps.yandex.net/tiles";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIYandexRenderer();
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
