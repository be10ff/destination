package ru.tcgeo.application.gilib.specs;

import ru.tcgeo.gilib.gps.GIYandexUtils;
import ru.tcgeo.gilib.planimetry.Edge;
import ru.tcgeo.gilib.specs.GIQuadTreeDouble;
import ru.tcgeo.gilib.specs.GISpeedCamera;
import ru.tcgeo.gilib.specs.GISpeedCamersGeometry;
import ru.tcgeo.gilib.specs.GITreeTile;

import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;

public class GISpeedCar extends GISpeedCamera
{
	public float m_accurancy;
	final public int delta_t = 20;
	public GISpeedCar(Location location)
	{
		m_lon = location.getLongitude();
		m_lat = location.getLatitude();
		m_direction = (int) location.getBearing();
		m_zone = (int)(location.getSpeed()*20);
		m_accurancy = location.getAccuracy();
		init();
		
	}
	public GISpeedCar(double lon, double lat, double speed, int direction)
	{
		m_lon = lon;
		m_lat = lat;
		m_direction = direction;
		m_zone = (int) (speed*20);
		init();
	}
	private void init()
	{
		m_ID = -1;
		m_type = -1;
		m_speed = 0;
		m_direction_type = 1;
		m_name = "car";
		m_isTought = false;
		m_angle = 60;
		Vector();
		//Make();
	}
	public void Make()
	{
		//calculating the geometry
		m_geometry = new GISpeedCamersGeometry();
     
		double[] weights = GIYandexUtils.DegreeWeight(m_lat);
//		//position
//		PointF pos = new PointF((float)m_lon, (float)m_lat);
//		//center of beams end
//		double dLon = m_zone*Math.sin(Math.toRadians(m_direction))/weights[0];
//		double dLat = m_zone*Math.cos(Math.toRadians(m_direction))/weights[1];
//
//		double dLon_left = m_zone*Math.sin(Math.toRadians(m_angle/2))*Math.cos(Math.toRadians(m_angle/2))/weights[0];
//		double dLat_left = m_zone*Math.sin(Math.toRadians(m_angle/2))*Math.sin(Math.toRadians(m_angle/2))/weights[1];
//		double dLon_right = m_zone*Math.sin(Math.toRadians(m_angle/2))*Math.sin(Math.toRadians(m_angle/2))/weights[0];
//		double dLat_right = m_zone*Math.cos(Math.toRadians(m_angle/2))*Math.sin(Math.toRadians(m_angle/2))/weights[1];
//
//		
//		m_geometry.add(new PointF((float)(m_lon), (float)m_lat));
//		m_geometry.add(new PointF((float)(m_lon + dLon - dLon_left), (float)(m_lat + dLat + dLat_left)));
//		m_geometry.add(new PointF((float)(m_lon + dLon), (float)(m_lat + dLat)));
//		m_geometry.add(new PointF((float)(m_lon + dLon + dLon_right), (float)(m_lat + dLat - dLat_right)));
//		m_geometry.add(new PointF((float)(m_lon), (float)m_lat));
		
		//camera
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
	@Override
	public void calculatingMorton()
	{
		//RectF bounds = m_geometry.getBounds();
		Edge edge = getVector();
		m_tile_start = new GITreeTile(MORTON_LVL, edge.m_start.x, edge.m_start.y);
		m_tile_end = new GITreeTile(MORTON_LVL,  edge.m_end.x, edge.m_end.y);
		m_morton_start = GIQuadTreeDouble.MortonCode2D(m_tile_start.m_xtile, m_tile_start.m_ytile);
		m_morton_end = GIQuadTreeDouble.MortonCode2D(m_tile_end.m_xtile, m_tile_end.m_ytile);
	}
	@Override
	public RectF getBounds()
	{
		return m_edge.getBounds();
	}
}
