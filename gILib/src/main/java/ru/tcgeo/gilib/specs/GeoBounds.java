package ru.tcgeo.gilib.specs;

import android.graphics.RectF;
import ru.tcgeo.gilib.GILonLat;


public class GeoBounds 
{
	protected double       m_top;
	protected double       m_left;
	protected double       m_right;
	protected double       m_bottom;
	
	public GeoBounds (double left, double top,  double right, double bottom)
	{
		m_left = left;
		m_top = top;
		m_right = right;
		m_bottom = bottom;
	}
	public GeoBounds (RectF bounds)
	{
		m_left = bounds.left;
		m_top = bounds.top;
		m_right = bounds.right;
		m_bottom = bounds.bottom;
	}
	
	public void set(double left, double top, double right, double bottom)
	{
		m_left = left;
		m_top = top;
		m_right = right;
		m_bottom = bottom;
	}
	
	public double width ()
	{
		return m_right - m_left;
	}

	public double height ()
	{
		return m_top - m_bottom;
	}

	public double top ()
	{
		return m_top;
	}

	public double left ()
	{
		return m_left;
	}

	public double right ()
	{
		return m_right;
	}

	public double bottom ()
	{
		return m_bottom;
	}

	public GILonLat TopLeft ()
	{
		return new GILonLat(m_left, m_top);
	}
	public GILonLat Center ()
	{
		return new GILonLat((m_right+m_left)/2, (m_top + m_bottom)/2);
	}
	
	public GILonLat TopRight ()
	{
		return new GILonLat(m_right, m_top);
	}

	public GILonLat BottomLeft ()
	{
		return new GILonLat(m_left, m_bottom);
	}

	public GILonLat BottomRight ()
	{
		return new GILonLat(m_right, m_bottom);
	}
	public Boolean ContainsBounds (GeoBounds other)
	{
		if(m_left <= other.m_left && m_right >= other.m_right && m_top >= other.m_top && m_bottom <= other.m_bottom)
		{
			return true;
		}
		return false;
		// TODO
	}

	public Boolean ContainsPoint (GILonLat point)
	{
		if(m_left <= point.lon() && m_right >= point.lon() && m_top >= point.lat() && m_bottom <= point.lat())
		{
			return true;
		}
		return false;
	}

	public Boolean Intersects (GeoBounds with)
	{
		if(m_left > with.m_right || m_right < with.m_left || m_top < with.m_bottom || m_bottom > with.m_top)
		{
			return false;
		}
		return true;
		// TODO
	}

	public GeoBounds Intersect (GeoBounds with)
	{
		if(Intersects(with))
		{
			double left = Math.max(m_left, with.m_left);
			double right = Math.min(m_right, with.m_right);
			double top = Math.max(m_top, with.m_top);
			double bottom = Math.min(m_bottom, with.m_bottom);
			return new GeoBounds(left, top, right, bottom);
		}
		return null;
		// TODO
	}
}
