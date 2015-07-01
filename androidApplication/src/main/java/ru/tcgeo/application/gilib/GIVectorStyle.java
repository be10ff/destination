package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;
import android.graphics.Paint;


public class GIVectorStyle implements GIStyle
{
	public Paint m_paint_pen;
	public Paint m_paint_brush;
    int m_opacity;
    
	public GIFilter m_filter;
	Bitmap m_image;

    public GIVectorStyle ()
    {
    }

    public GIVectorStyle (Bitmap image)
    {
    	m_image = image;
    	m_filter = GIFilter.All();
    }

    public GIVectorStyle (Bitmap image, GIFilter filter)
    {
    	m_image = image;
    	m_filter = filter;
    }

	public GIVectorStyle (Paint paint_pen, Paint paint_brush, int opacity)
    {
	    m_paint_pen = paint_pen;
	    m_paint_brush = paint_brush;
	    m_opacity = opacity;
	    m_filter = GIFilter.All();
    }

	public GIVectorStyle (Paint paint, int opacity, GIFilter filter)
	{
	    m_paint_pen = paint;
	    m_opacity = opacity;
	    m_filter = filter;
	}

	public GIVectorStyle (GIVectorStyle style, GIFilter filter)
	{
		m_paint_pen = style.m_paint_pen;
	    m_opacity = style.m_opacity;
	    m_image = style.m_image;

	    m_filter = filter;
	}
/*
    public Paint getPaint ()   				{ return m_paint; }
    public void  setPaint (Paint paint)    	{ m_paint = paint; }
    public int   getOpacity ()    			{ return m_opacity; }
    public void  setOpacity (byte opacity) 	{ m_opacity = opacity; }
*/

	public boolean invariant ()
	{
		return m_filter.getClass() == GIFilter.All().getClass();
	}
}
