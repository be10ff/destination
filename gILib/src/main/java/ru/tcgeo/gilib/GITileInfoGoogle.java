package ru.tcgeo.gilib;

public class GITileInfoGoogle {


	public GIBounds m_bounds;
	public int m_zoom;
	public int m_tile_width;
	public int m_tile_height;
	public GILonLat m_center;
	
	/*public GITileInfoGoogle(int zoom, GILonLat center, int width, int height) 
	{
		m_center = center;
		m_zoom = zoom;
		m_tile_width = width;
		m_tile_height = height;
	}
	
	public GITileInfoGoogle(int zoom, double lon, double lat, int width, int height)
	{
		m_center = new GILonLat(lon, lat);
		m_zoom = zoom;
		m_tile_width = width;
		m_tile_height = height;
	}*/
	public GITileInfoGoogle(int zoom, GILonLat left_top, GILonLat right_bottom, int width, int height)
	{
		m_tile_width = width;
		m_tile_height = height;
		m_center = new GILonLat((right_bottom.lon() + left_top.lon())/2, (right_bottom.lat() + left_top.lat())/2);
		m_bounds = new GIBounds(GIProjection.WGS84(), left_top.lon(), left_top.lat(), right_bottom.lon(), right_bottom.lat());
		m_zoom = zoom;
		
		//
	
	}
	public GILonLat getCenter()
	{
		return m_center;
	}
	public GIBounds getBounds()
	{
		return m_bounds;
	}
	

	public String getURL()
	{
		String urlStr = "http://static-maps.yandex.ru/1.x/?ll=" + m_center.lon() + "," + m_center.lat() + "&z=" + m_zoom + "&size=" + m_tile_width + "," + m_tile_height + "&l=sat,trf";
		//String urlStr = "http://maps.googleapis.com/maps/api/staticmap?center=" + m_center.lat() + "," + m_center.lon() + "&zoom=" + m_zoom + "&size=" + m_tile_width + "x" + m_tile_height + "&sensor=true_or_false &key=AIzaSyAUi65U_iQh7UocSMzOQXrtCmhpdLrialY";
		return urlStr;
	}
	public String getURL_spn()
	{
		String urlStr = "http://static-maps.yandex.ru/1.x/?ll=" + m_center.lon() + "," + m_center.lat() + "&spn=" + m_bounds.height()/4 + "," +  m_bounds.width()/4 + "&size=" + m_tile_width + "," + m_tile_height + "&l=sat";
		//String urlStr = "http://maps.googleapis.com/maps/api/staticmap?center=" + m_center.lat() + "," + m_center.lon() + "&zoom=" + m_zoom + "&size=" + m_tile_width + "x" + m_tile_height + "&sensor=true_or_false &key=AIzaSyAUi65U_iQh7UocSMzOQXrtCmhpdLrialY";
		return urlStr;
	}
	@Override
	public String toString() {
		return "lon=" + m_center.lon() + " lat=" + m_center.lat();
	}
	
}
