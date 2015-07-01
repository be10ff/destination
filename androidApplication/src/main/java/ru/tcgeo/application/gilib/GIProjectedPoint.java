package ru.tcgeo.application.gilib;

public class GIProjectedPoint extends GILonLat {

	GIProjection m_projection;
	public GIProjectedPoint(GIProjection projection, double lon, double lat)
	{
		super(lon, lat);
		m_projection = projection;
		// TODO Auto-generated constructor stub
	}
	public GIProjectedPoint(GIProjection projection, GILonLat lonlat)
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
