package ru.tcgeo.gilib;

import java.util.ArrayList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;

public class GITextRenderer extends GIRenderer
{
	Canvas m_canvas;
	
	LabelsMap m_labels;
	ArrayList<Rect> m_drawed;
	
	GITextRenderer ()
	{
		m_canvas = new Canvas();
	}
	
	@Override
	public void RenderImage (GILayer layer, GIBounds area, int opacity,
	        Bitmap bitmap, double scale)
	{
		// Does nothing
		return;
	}
	
	private boolean check_label (PointF point, String label)
	{
		// TODO: Paint issues
		Paint paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(14f);
		Typeface tf = Typeface.create("ARIAL", Typeface.BOLD);
		paint.setTypeface(tf);
		
		Rect label_bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), label_bounds);
		label_bounds.offset((int)point.x, (int)point.y + 20);
		for (Rect drawed_rect : m_drawed)
		{
			if (Rect.intersects(drawed_rect,label_bounds))
				return false;
		}		
		m_drawed.add(label_bounds);
		return true;		 
	}

	@Override
	public void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, double scale)
	{
		Paint paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(14f);
		Typeface tf = Typeface.create("ARIAL", Typeface.BOLD);
		paint.setTypeface(tf);
		
		m_canvas.setBitmap(bitmap);
		
		//area = area.Reprojected(GIProjection.WGS84());
		GIVectorLayer vlayer = (GIVectorLayer)layer;
		area = area.Reprojected(vlayer.projection());
		m_labels = new LabelsMap();		
		m_drawed = new ArrayList<Rect>();
		
		getText(layer.m_id,  bitmap.getWidth(), bitmap.getHeight(), area, vlayer.encoding(), m_labels);
		for (Map.Entry<PointF, String> entry : m_labels.m_labels.entrySet())
		{
			// TODO: Paint issues
			if(check_label(entry.getKey(), entry.getValue()))
				m_canvas.drawText(entry.getValue(), entry.getKey().x, entry.getKey().y + 20, paint);
		}

	}

	@Override
	public void AddStyle (GIStyle style)
	{
		// Does nothing
		return;
	}
	
	native int getText (long layerID, 
            		 	int bitmapWidth,
            		 	int bitmapHeight, 
            			GIBounds area,
            			GIEncoding encoding, 
            			LabelsMap labels);

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor,
			double scale) {
		RenderText(layer, area, bitmap, scale);
		
	}

	@Override
	public int getType(GILayer layer) 
	{
		return 0;
	}
}
