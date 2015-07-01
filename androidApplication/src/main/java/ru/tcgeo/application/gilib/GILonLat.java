package ru.tcgeo.application.gilib;

import android.location.Location;

public class GILonLat {

	  private double m_lon;
	  private double m_lat;

	  public double lat() 
	  {
		  return m_lat;
	  }

	  public double lon() 
	  {
		  return m_lon;
	  }

	  public GILonLat(double lon, double lat) 
	  {	
		  m_lon = lon;
		  m_lat = lat;
	  }
	  
	  public static ru.tcgeo.gilib.GILonLat fromLocation(Location location)
	  {	
		  return new ru.tcgeo.gilib.GILonLat(location.getLongitude(), location.getLatitude());

	  }

	public ru.tcgeo.gilib.GILonLat OffsetBy (double lon, double lat)
    {
		return new ru.tcgeo.gilib.GILonLat(m_lon + lon, m_lat + lat);
    }
}