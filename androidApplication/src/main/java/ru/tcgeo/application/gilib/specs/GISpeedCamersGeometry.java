package ru.tcgeo.application.gilib.specs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import ru.tcgeo.application.gilib.GIBounds;
import ru.tcgeo.application.gilib.gps.GIYandexUtils;
import ru.tcgeo.application.gilib.planimetry.GIGeometryPolygon;

public class GISpeedCamersGeometry extends GIGeometryPolygon
{
	Paint m_paint;
	GISpeedCamersGeometry()
	{
		super();
		m_paint = new Paint();
		m_paint.setARGB(128, 255, 64, 64);
		m_paint.setStyle(Style.FILL_AND_STROKE);
		
	}
	@Override
	public RectF getBounds()
	{
		if(m_points == null)
		{
			return null;
		}
		if(m_points.size() == 0)
		{
			return new RectF(0, 0, 0, 0);
		}
		float minX, maxX, minY, maxY;
		minX = m_points.get(0).x;
		maxX = m_points.get(0).x;
		minY = m_points.get(0).y;
		maxY = m_points.get(0).y;
		
		for(int i = 1; i < m_points.size(); i++)
		{
			PointF cur = m_points.get(i);
			if(cur.x < minX)
			{
				minX = cur.x;
			}
			if(cur.x > maxX)
			{
				maxX = cur.x;
			}
			if(cur.y < minY)
			{
				minY = cur.y;
			}
			if(cur.y > maxY)
			{
				maxY = cur.y;
			}
		}
		//TODO
		float minX_b, maxX_b, minY_b, maxY_b;
		return new RectF(minX, minY, maxX, maxY);
	}
	

	public void Draw(Canvas canvas, GIBounds area)
	{
		if(m_points.size() == 0)
		{
			return;
		}
        Path path= new Path();

        PointF screen_point = GIYandexUtils.MapToScreen(m_points.get(0), canvas, area);
        
        path.moveTo(screen_point.x, screen_point.y);
		for(int i = 1; i < m_points.size(); i++)
		{
			screen_point = GIYandexUtils.MapToScreen(m_points.get(i), canvas, area);
			path.lineTo(m_points.get(i).x, m_points.get(i).y);
		}
		canvas.drawPath(path, m_paint);
	}

}
