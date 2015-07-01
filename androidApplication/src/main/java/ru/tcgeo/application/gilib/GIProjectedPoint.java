package ru.tcgeo.application.gilib;

import ru.tcgeo.gilib.*;
import ru.tcgeo.gilib.GIProjection;

public class GIProjectedPoint extends GILonLat {

	ru.tcgeo.gilib.GIProjection m_projection;
	public GIProjectedPoint(ru.tcgeo.gilib.GIProjection projection, double lon, double lat)
	{
		super(lon, lat);
		m_projection = projection;
		// TODO Auto-generated constructor stub
	}
	public GIProjectedPoint(ru.tcgeo.gilib.GIProjection projection, GILonLat lonlat)
	{
		super(lonlat.lon(), lonlat.lat());
		m_projection = projection;
		// TODO Auto-generated constructor stub
	}
	  public GIProjection projection()
	  {
		  return m_projection;
	  }
}
