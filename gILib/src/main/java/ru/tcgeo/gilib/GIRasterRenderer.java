package ru.tcgeo.gilib;

import ru.tcgeo.gilib.GIRasterLayer.GIRasterStyle;
import android.graphics.Bitmap;

/**
 * @author p.kitashov
 */
public class GIRasterRenderer extends GIRenderer
{
	public GIRasterStyle m_style;

	@Override
	public void RenderImage (GILayer layer, GIBounds area, int opacity,
	        Bitmap bitmap, double scale)
	{
		//GIBounds layer_area = area.Reprojected(GIProjection.WGS84());
		double[] left_top = reprojectPoint(layer.m_id, area.left(), area.top(), GIProjection.WorldMercator().m_id, layer.projection().m_id);
		double[] right_bottom = reprojectPoint(layer.m_id, area.right(), area.bottom(), GIProjection.WorldMercator().m_id, layer.projection().m_id);
		readMap (layer.m_id, bitmap, left_top[0], left_top[1], right_bottom[0], right_bottom[1]);
		//readMap (layer.m_id, bitmap, layer_area.left(), layer_area.top(), layer_area.right(), layer_area.bottom());
	} 
	
    public GIRasterRenderer(GIRasterStyle style) 
    {
    }

	@Override
    public void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, double scale)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void AddStyle (GIStyle style)
    {
	    // TODO Auto-generated method stub
	    
    }
	


	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor,
			double scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public native int readMap(long layerID,
			Bitmap bitmap,
            double fromX,
            double fromY,
            double toX,
            double toY);
	
	public native double[] reprojectPoint(long layer_id, double X, double Y, long source_id, long dest_id);
}



	  

	