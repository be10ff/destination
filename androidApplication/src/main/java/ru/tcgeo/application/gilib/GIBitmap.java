package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GILonLat;

public class GIBitmap 
{
	public GIBounds m_bounds;
	public Bitmap m_bitmap;
	private int m_width;
	private int m_height;
	
	//private double m_left;
	//private double m_top;
	//private double m_right;
	//private double m_bottom;
	
	//private float m_scale;
	
	//private GIProjection m_projection;
	
	public GIBitmap(GIBounds bounds, int width, int height/*, float scale*/)
	{
		m_width = width;
		m_height = height;
		System.gc();
		m_bitmap =  Bitmap.createBitmap(m_width, m_height, Bitmap.Config.ARGB_8888);
		m_bounds = bounds;
		//m_scale = scale;
	}
	
	public GIBitmap(GIBounds bounds, Bitmap bitmap/*, float scale*/)
	{
		
		m_bitmap =  bitmap;
		m_width = bitmap.getWidth();
		m_height = bitmap.getHeight();
		m_bounds = bounds;
		//m_scale = scale;
	}
	public void Set(GIBounds bounds, Bitmap bitmap/*, float scale*/)
	{
		if(m_bitmap != null)
		{
			m_bitmap.recycle();
			m_bitmap = bitmap;
		}
		m_width = bitmap.getWidth();
		m_height = bitmap.getHeight();
		m_bounds = bounds;
		//m_scale = scale;
	}
	public boolean Draw(Canvas canvas, GIBounds bounds)
	{
		if(m_bitmap == null)
		{
			return true;
		}
		if(m_bitmap.isRecycled())
		{
			return true;
		}
		boolean res = true;
		if(m_bounds.ContainsBounds(bounds))
		{
			res = false;
		}
		if(!m_bounds.Intersects(bounds))
		{
			return true;
		}
		
		int view_width = canvas.getWidth();
		int view_height = canvas.getHeight();
		
		double pixelWidth = bounds.width() / view_width; 
		double pixelHeight = bounds.height() / view_height;
		
		GILonLat LeftTop = m_bounds.TopLeft();
		GILonLat RightBottom = m_bounds.BottomRight();
		
		int left = (int)((LeftTop.lon() - bounds.left())/pixelWidth);
		int top = (int)((bounds.top() - LeftTop.lat())/pixelHeight);
		int right = (int)((RightBottom.lon() - bounds.left())/pixelWidth);
		int bottom = (int)((bounds.top() - RightBottom.lat())/pixelHeight);
		
		// ???
		//TODO re-draw only valid intersection for performance
		/*
 		Rect canvas_rect = new Rect(0, 0, view_width, view_height);
		Rect bitmap_rect = new Rect(left, top, right, bottom);
		//Rect bitmap_rect = new Rect(view_rect);
		//bitmap_rect.offset(left, top);
		Rect intersection_rect_in_canvas = new Rect();
		intersection_rect_in_canvas.setIntersect(canvas_rect, bitmap_rect);
		Rect intersection_rect_in_bitmap = new Rect(intersection_rect_in_canvas);
		intersection_rect_in_bitmap.set(intersection_rect_in_canvas.left - left, intersection_rect_in_canvas.top - top, intersection_rect_in_canvas.right - left + right, intersection_rect_in_canvas.bottom - top + bottom);
		//intersection_rect_in_bitmap.offset(-left, -top);
		//canvas.drawBitmap(m_bitmap, intersection_rect_in_bitmap, intersection_rect_in_canvas, null);
		 */
		
		canvas.drawBitmap(m_bitmap, new Rect(0, 0, m_width, m_height), new Rect(left, top, right, bottom), null);
		return res;
	}

}
