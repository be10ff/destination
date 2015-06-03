package ru.tcgeo.gilib.specs;

import android.graphics.PointF;
import android.graphics.RectF;

import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GILonLat;
import ru.tcgeo.gilib.gps.GIYandexUtils;
import ru.tcgeo.gilib.planimetry.Edge;
import ru.tcgeo.gilib.planimetry.Vertex;
import ru.tcgeo.wkt.GI_WktLinestring;
import ru.tcgeo.wkt.GI_WktPoint;
import ru.tcgeo.wkt.GI_WktPolygon;

public class GISpeedCamera 
{
	public static int MORTON_LVL = 17;
	public int m_ID;
	//GILonLat m_lonlat;
	public double m_lon;
	public double m_lat;
//	double m_lon_m;
//	double m_lat_m;
	public int m_type;
	public int m_speed;
	public int m_direction_type;
	public int m_direction;
	public int m_zone;
	public int m_angle;
	public String m_name;
	//public GILonLat m_lon_lat_geo;
	//public GILonLat m_lon_lat_mercator;
	//public Edge m_edge;
	protected GISpeedCamersGeometry m_geometry;
	private GI_WktPolygon m_wkt_polygon;
	public GITreeTile m_tile;
	public GITreeTile m_tile_start;
	public GITreeTile m_tile_end;	
	public long m_morton_start;
	public long m_morton_end;
	boolean m_isTought;
	
	public Edge m_edge;
	
	
	
	public GISpeedCamera()
	{
		m_isTought = false;
	}

//	public GISpeedCamera(Location location)
//	{
//		m_ID = -1;
//		m_lon = location.getLongitude();
//		m_lat = location.getLatitude();
//		m_type = -1;
//		m_speed = 0;
//		m_direction_type = 1;
//		m_direction = (int) location.getBearing();
//		m_zone = (int)location.getSpeed()*20;
//		m_angle = 30;
//		m_name = "car";
//		m_isTought = false;
//		Make();
//		
//	}
	
	public  GISpeedCamera(GILonLat location)
	{
		m_ID = -1;
		m_lon = location.lon();
		m_lat = location.lat();
		m_type = -1;
		m_speed = 0;
		m_direction_type = 1;
		m_direction = 0;
		m_zone = 300;
		m_angle = 90;
		m_name = "car";
		m_isTought = false;
		//Make();
		Vector();
	}
	
	public GI_WktPolygon getWKTPolygon()
	{
		if(m_wkt_polygon == null)
		{
			m_wkt_polygon = new GI_WktPolygon();
			GI_WktLinestring geom = new GI_WktLinestring();
			
			for(int i = 0; i < m_geometry.m_points.size();  i++)
			{
				Vertex vertex =  m_geometry.m_points.get(i);
				GILonLat lonlat = new GILonLat(vertex.x, vertex.y);
				geom.AddPoint(new GI_WktPoint(lonlat));
			}
			
			m_wkt_polygon.m_rings.add(geom);
		}
		return m_wkt_polygon;
	}
	
	public void Make()
	{
		//Reproject();
		//m_lon_lat_geo = new GILonLat(m_lon, m_lat);
		//calculating the geometry
		m_geometry = new GISpeedCamersGeometry();
		if(m_angle >= 180)
		{
			m_angle = 127;
		}
		double[] weights = GIYandexUtils.DegreeWeight(m_lat);
		//position
		PointF pos = new PointF((float)m_lon, (float)m_lat);
		//center of beams end
		double dLon = m_zone*Math.sin(Math.toRadians(m_direction))/weights[0];
		double dLat = m_zone*Math.cos(Math.toRadians(m_direction))/weights[1];
		//
		double dLon_left  = -m_zone*Math.tan(Math.toRadians(m_angle/2))*Math.cos(Math.toRadians(m_direction))/weights[0];
		double dLat_left  =  m_zone*Math.tan(Math.toRadians(m_angle/2))*Math.sin(Math.toRadians(m_direction))/weights[1];

		
		m_geometry.add(new PointF((float)(m_lon), (float)m_lat));
		m_geometry.add(new PointF((float)(m_lon + dLon + dLon_left), (float)(m_lat + dLat + dLat_left)));
		m_geometry.add(new PointF((float)(m_lon + dLon - dLon_left), (float)(m_lat + dLat - dLat_left)));
		m_geometry.add(new PointF((float)(m_lon), (float)m_lat));
		
		if(m_direction_type == 2)
		{
			m_geometry.add(new PointF((float)(m_lon - dLon - dLon_left), (float)(m_lat - dLat - dLat_left)));
			m_geometry.add(new PointF((float)(m_lon - dLon + dLon_left), (float)(m_lat - dLat + dLat_left)));
			m_geometry.add(new PointF((float)(m_lon), (float)(m_lat)));
		}
		m_geometry.MakeEdgesRing();
		
	}
	/**
	 * ну тоже самое что и Make() но для другого способа поиска попадания
	 * @return
	 */
	protected Edge Vector()
	{
		double[] weights = GIYandexUtils.DegreeWeight(m_lat);
		double dLon = m_zone*Math.sin(Math.toRadians(m_direction))/weights[0];
		double dLat = m_zone*Math.cos(Math.toRadians(m_direction))/weights[1];
		m_edge = new Edge((float)m_lon, (float)m_lat, (float)(m_lon + dLon), (float)(m_lat + dLat));
		return m_edge;
	}
	
	public Edge getVector()
	{
		if(m_edge == null)
		{
			m_edge = Vector();
		}
		return m_edge;
	}
	

//	public GIBounds getBounds()
//	{
//		RectF bounds = m_geometry.getBounds();
//		return new GIBounds(GIProjection.WGS84(), bounds.left, bounds.top, bounds.right, bounds.bottom);
//	}
	
	public RectF getBounds()
	{
		return m_geometry.getBounds();
	}
	
	public GITreeTile getTile()
	{
		if(m_tile == null)
		{
			return calcTile();
		}
		return m_tile;
	}
	
	public GITreeTile calcTile()
	{
		RectF bounds = m_geometry.getBounds();
		int z = 0;
		m_tile = null;
		while(z < 20)
		{
			GITreeTile start = new GITreeTile(z, bounds.left, bounds.top);
			GITreeTile end = new GITreeTile(z, bounds.right, bounds.bottom);
			if(start.m_xtile == end.m_xtile && start.m_ytile == end.m_ytile)
			{
				m_tile = start;
				z = z + 1;
			}
			else
			{
				return m_tile;
			}
		}
		return m_tile;
	}
	public void calculatingMorton()
	{
		RectF bounds = m_geometry.getBounds();
		m_tile_start = new GITreeTile(MORTON_LVL, bounds.left, bounds.bottom);
		m_tile_end = new GITreeTile(MORTON_LVL, bounds.right, bounds.top);
		m_morton_start = GIQuadTreeDouble.MortonCode2D(m_tile_start.m_xtile, m_tile_start.m_ytile);
		m_morton_end = GIQuadTreeDouble.MortonCode2D(m_tile_end.m_xtile, m_tile_end.m_ytile);
	}
	
	public static long[] getMorton(GIBounds area)
	{
		long[] res = new  long[2];
		GITreeTile tile_start = new GITreeTile(MORTON_LVL, area.left(), area.top());
		GITreeTile tile_end = new GITreeTile(MORTON_LVL, area.right(), area.bottom());
		
		res[0] = GIQuadTreeDouble.MortonCode2D(tile_start.m_xtile, tile_start.m_ytile);
		res[1] = GIQuadTreeDouble.MortonCode2D(tile_end.m_xtile, tile_end.m_ytile);
		
		return res;
	}
	
	public boolean isIntersectByCamera(GISpeedCamera camera)
	{
//		ArrayList<Vertex> polygon = new ArrayList<Vertex>();
//		Vertex v0 = new Vertex(10, 10);
//		Vertex v1 = new Vertex(30, 10);
//		Vertex v2 = new Vertex(30, 30);
//		Vertex v3 = new Vertex(10, 10);
//		polygon.add(v0);
//		polygon.add(v1);
//		polygon.add(v2);
//		polygon.add(v3);	
//		
//		Edge edge = new Edge(10, 20, 40, 20);
//		boolean res = edge.ParametricCoordsOfSectionByPolygon_kinda(polygon);
		
		//me POLYGON((37.58128356933594 55.82518768310547, 37.57648849487305 55.82788848876953, 37.586082458496094 55.82788848876953, 37.58128356933594 55.82518768310547))
		//cam POLYGON((37.57268524169922 55.824302673339844, 37.571842193603516 55.82880783081055, 37.57380294799805 55.82878875732422, 37.57268524169922 55.824302673339844))

//		ArrayList<Vertex> me = new ArrayList<Vertex>();
//		me.add(new Vertex(37.5812f, 55.8251f));
//		me.add(new Vertex(37.5764f, 55.8278f));
//		me.add(new Vertex(37.5860f, 55.8278f));		
//		me.add(new Vertex( 37.5812f, 55.8251f));
//		
		
//		ArrayList<Edge> cam = new ArrayList<Edge>();
//		cam.add(new Edge(37.5726f, 55.8260f, 37.5721f, 55.8285f));
//		cam.add(new Edge(37.5721f, 55.8285f, 37.5730f, 55.8285f));
//		cam.add(new Edge(37.5730f, 55.8285f, 37.5726f, 55.8260f));	
//		
//		ArrayList<Vertex> me = new ArrayList<Vertex>();
//		me.add(new Vertex(37.5726f, 55.8243f));
//		me.add(new Vertex(37.5718f, 55.8288f));
//		me.add(new Vertex(37.5738f, 55.8287f));		
//		me.add(new Vertex(37.5726f, 55.8243f));
		
//		ArrayList<Edge> cam = new ArrayList<Edge>();
//		cam.add(new Edge(37.5726f, 55.8243f, 37.5718f, 55.8288f));
//		cam.add(new Edge(37.5718f, 55.8288f, 37.5738f, 55.8287f));
//		cam.add(new Edge(37.5738f, 55.8287f, 37.5726f, 55.8243f));		
//		ArrayList<Vertex> me = new ArrayList<Vertex>();
//		me.add(new Vertex(37.5726f, 55.8260f));
//		me.add(new Vertex(37.5721f, 55.8285f));
//		me.add(new Vertex(37.5730f, 55.8285f));		
//		me.add(new Vertex(37.5726f, 55.8260f));

		
//		for(Edge edge : cam)
//		{
//			if(edge.IsEdgeIntersectedByPolygon(me) )
//			{
//				return true;
//			}
//		}	
		
		
		for(Edge edge : m_geometry.m_edges)
		{
			if(edge.IsEdgeIntersectedByPolygon(camera.m_geometry.m_points) )
			{
				return true;
			}
		}
		if(m_geometry.IsPointInsidePolygon(camera.m_geometry.m_points.get(0)))
		{
			return true;
		}
		if(camera.m_geometry.IsPointInsidePolygon(m_geometry.m_points.get(0)))
		{
			return true;
		}
		
//		camera.m_geometry.MakeEdgesRing();
//		for(Edge edge : camera.m_geometry.m_edges)
//		{
//			if(edge.IsEdgeIntersectedByPolygon(m_geometry.m_points) )
//			{
//				return true;
//			}
//		}
		return false;
	}
	

}
