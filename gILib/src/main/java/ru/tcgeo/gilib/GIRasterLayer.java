package ru.tcgeo.gilib;

import android.graphics.Bitmap;

public class GIRasterLayer extends GILayer
{
	public enum GIRasterStyle
    {
		GI_RGB
    }
// TODO: Finish raster class 
	
	public String m_dataPath;

	public Integer m_bands;

    GIRasterLayer(String path) 
    {
      type_ = GILayerType.RASTER_LAYER;
  	  m_dataPath = path;
   	  m_renderer = new GIRasterRenderer(GIRasterLayer.GIRasterStyle.GI_RGB);
   	  m_id = initRasterLayer(path);
   	  m_projection = new GIProjection(getProjection(m_id), true);
    }

	@Override
    public void Redraw (GIBounds area, Bitmap bitmap, Integer opacity, double scale)
    {
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, 1);	 
		}  
    }

	native long initRasterLayer (String path);
	native long getProjection(long id);
}
