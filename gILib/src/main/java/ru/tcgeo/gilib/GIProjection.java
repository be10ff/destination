package ru.tcgeo.gilib;

public class GIProjection
{
	long m_id;
	
	public GIProjection (String wkt_description)
    {
		m_id = initProjection(wkt_description);
    }
	public GIProjection (long epgs)
    {
		m_id = initProjectionFromEPSG(epgs);
    }
	public GIProjection (long id, boolean bool)
    {
		m_id = id;
    }
	//'code': 6872
	public static GIProjection GCS_Moscow()
	{
		return new GIProjection("PROJCS[\"Pulkovo 1995 / Gauss-Kruger zone 7\",GEOGCS[\"Pulkovo 1995\",DATUM[\"Pulkovo_1995\",SPHEROID[\"Krassowsky 1940\",6378245,298.3,AUTHORITY[\"EPSG\",\"7024\"]],AUTHORITY[\"EPSG\",\"6200\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4200\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",39],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",7500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"20007\"],AXIS[\"Y\",EAST],AXIS[\"X\",NORTH]]");
	}

	public static GIProjection WGS84()
	{
		return new GIProjection(
				"GEOGCS[\"WGS 84\"," +
                         "DATUM[\"WGS_1984\"," +
                          		 "SPHEROID[\"WGS 84\"," +
                        		 		    "6378137," +
                        				    "298.257223563," +
                        				    "AUTHORITY[\"EPSG\",\"7030\"]]," +
                        		 "AUTHORITY[\"EPSG\",\"6326\"]]," +
                         "PRIMEM[\"Greenwich\"," +
                        		  "0," +
                        		  "AUTHORITY[\"EPSG\",\"8901\"]]," +
                         "UNIT[\"degree\"," +
                        	    "0.01745329251994328," +
                        	    "AUTHORITY[\"EPSG\",\"9122\"]]," +
						 "AUTHORITY[\"EPSG\",\"4326\"]]");
						 
	}
	

	public static GIProjection WorldMercator()
	
	{
		return new GIProjection(
				"PROJCS[\"WGS 84 / World Mercator\"," +
						 "GEOGCS[\"WGS 84\"," +
						 		  "DATUM[\"WGS_1984\"," +
						 		  		  "SPHEROID[\"WGS 84\"," +
						 		  		  			 "6378137," +
						 		  		  			 "298.257223563," +
						 		  		  			 "AUTHORITY[\"EPSG\"," +
						 		  		  			           "\"7030\"]]," +
						 		  		  "AUTHORITY[\"EPSG\",\"6326\"]]," +
						 		  "PRIMEM[\"Greenwich\"," +
						 		  		   "0," +
						 		  		   "AUTHORITY[\"EPSG\",\"8901\"]]," +
						 		  "UNIT[\"degree\"," +
						 		  		 "0.01745329251994328," +
						 		  		 "AUTHORITY[\"EPSG\",\"9122\"]]," +
						 		  "AUTHORITY[\"EPSG\",\"4326\"]]," +
						 "UNIT[\"metre\"," +
						 		"1," +
						 		"AUTHORITY[\"EPSG\",\"9001\"]]," +
        				 "PROJECTION[\"Mercator_1SP\"]," +
        				 "PARAMETER[\"central_meridian\",0]," +
        				 "PARAMETER[\"scale_factor\",1]," +
        				 "PARAMETER[\"false_easting\",0]," +
        				 "PARAMETER[\"false_northing\",0]," +
        				 "AUTHORITY[\"EPSG\",\"3395\"]," +
        				 "AXIS[\"Easting\",EAST]," +
    					 "AXIS[\"Northing\",NORTH]]");
	}
	
	public static GILonLat ReprojectLonLat (GILonLat point, GIProjection source, GIProjection dest)
	{
		//android.util.Log.v("DBG", "Okay: " + point + ", " + source + " x " + dest);
		double[] lon_lat = reprojectPoint(point, source.m_id, dest.m_id);
		return new GILonLat(lon_lat[0], lon_lat[1]);
	}
	
	private native long initProjectionFromEPSG (long epgs);
	private native long initProjection (String wkt_description);
	private native static double[] reprojectPoint (GILonLat point, long source_id, long dest_id);
}
