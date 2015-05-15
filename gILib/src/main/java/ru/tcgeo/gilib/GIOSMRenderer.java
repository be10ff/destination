package ru.tcgeo.gilib;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class GIOSMRenderer extends GIRenderer {

	Canvas m_canvas;
	public GIOSMRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity,
			Bitmap bitmap, double scale) 
	{
		m_canvas = new Canvas(bitmap);
		area = area.Reprojected(layer.projection());
		int Width_px = bitmap.getWidth();
		
		double kf = 360.0f/256.0f;
        
        double left = area.m_left;
		double top= area.m_top;
        double right = area.m_right;
        double bottom = area.m_bottom;

        double width = right - left;
        
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz);

        GITileInfoOSM left_top_tile = new GITileInfoOSM(z, left, top);
        GITileInfoOSM right_bottom_tile = new GITileInfoOSM(z, right, bottom);
        
    	float koeffX = (float) (bitmap.getWidth() / (right - left));
    	float koeffY = (float) (bitmap.getHeight() / (top - bottom));

        try
        {
        	for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
        	{
        		for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
        		{
        			GITileInfoOSM tile = new GITileInfoOSM(z, x, y);
        			String urlStr = tile.getURL();
        			URL url = new URL(urlStr);
        	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        	        urlConnection.connect();
			        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			        Bitmap bit_tile = BitmapFactory.decodeStream(in);
			        
			        //
					Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());

					
					float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
					float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
					float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
					float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);
					
					RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
					m_canvas.drawBitmap(bit_tile, src, dst, null);
					bit_tile.recycle();
        	        urlConnection.disconnect();
        		}
    		}
        }
        catch(Exception e) 
        {
            e.printStackTrace(); 
        }
        finally
        {
        };
	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			float scale_factor, double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AddStyle(GIStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
