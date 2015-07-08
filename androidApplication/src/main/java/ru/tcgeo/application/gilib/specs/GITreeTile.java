//package ru.tcgeo.application.gilib.specs;
//
//import ru.tcgeo.application.gilib.models.GIBounds;
//
//public class GITreeTile
//{
//	//String urlStr = "http://a.tile.openstreetmap.org/" + n + "/" + x + "/" + y + ".png";
//	public int m_zoom;
//	public int m_xtile;
//	public int m_ytile;
//	public boolean m_nesessary;
//	public GeoBounds m_bounds;
//
//	public GITreeTile()
//	{
//
//	}
//	public GITreeTile(int z, double lon, double lat)
//	{
//		m_nesessary = true;
//		m_zoom = z;
//		m_xtile = (int)Math.floor( (lon + 180) / 360 * (1<<z) ) ;
//		m_ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<z) ) ;
//	    if (m_xtile < 0)
//	     m_xtile=0;
//	    if (m_xtile >= (1<<z))
//	     m_xtile=((1<<z)-1);
//	    if (m_ytile < 0)
//	     m_ytile=0;
//	    if (m_ytile >= (1<<z))
//	     m_ytile=((1<<z)-1);
//
//	    getBounds();
//	}
////	public GITreeTile getTile(RectF bounds)
////	{
////		int z = 0;
////		GITreeTile res = null;
////		while(z < 20)
////		{
////			GITreeTile start = new GITreeTile(z, bounds.left, bounds.top);
////			GITreeTile end = new GITreeTile(z, bounds.right, bounds.bottom);
////			if(start.m_xtile == end.m_xtile && start.m_ytile == end.m_ytile)
////			{
////				res = start;
////				z = z + 1;
////			}
////			else
////			{
////				return res;
////			}
////		}
////		return res;
////	}
//	public GITreeTile(GIBounds bounds)
//	{
//
//		int z = 0;
//		GITreeTile res = null;
//		while(z < 20)
//		{
//			GITreeTile start = new GITreeTile(z, bounds.left(), bounds.top());
//			GITreeTile end = new GITreeTile(z, bounds.right(), bounds.bottom());
//			if(start.m_xtile == end.m_xtile && start.m_ytile == end.m_ytile)
//			{
//				res = start;
//				z = z + 1;
//			}
//			else
//			{
////				m_xtile = res.m_xtile;
////				m_ytile = res.m_ytile;
////				m_zoom = res.m_zoom;
//				break;
//			}
//		}
//		m_xtile = res.m_xtile;
//		m_ytile = res.m_ytile;
//		m_zoom = res.m_zoom;
//		m_bounds = new GeoBounds(bounds.left(), bounds.top(), bounds.right(), bounds.bottom());
//	}
//
//	public GITreeTile(int z, int tile_x, int tile_y)
//	{
//		m_nesessary = true;
//		m_zoom = z;
//		m_xtile = tile_x;
//		m_ytile = tile_y;
//
//		getBounds();
//	}
//
//	public GeoBounds getBounds()
//	{
//		double top = tile2lat(m_ytile, m_zoom);
//		double bottom = tile2lat(m_ytile + 1, m_zoom);
//		double left = tile2lon(m_xtile, m_zoom);
//		double right = tile2lon(m_xtile + 1, m_zoom);
//		m_bounds = new GeoBounds(left, top, right, bottom);
//		return m_bounds;
//	}
//
//	public String getURL()
//	{
//		String urlStr = "http://a.tile.openstreetmap.org/" + m_zoom + "/" + m_xtile + "/" + m_ytile  + ".png";
//		return urlStr;
//	}
//
//	public double tile2lon(int x, int z)
//	{
//	   return x / Math.pow(2.0, z) * 360.0 - 180;
//	}
//
//	public double tile2lat(int y, int z)
//	{
//	  double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
//	  return Math.toDegrees(Math.atan(Math.sinh(n)));
//	}
//	@Override
//	public String toString()
//	{
//		return "Z:" + m_zoom + " X:" + m_xtile + "Y:" + m_ytile;
//
//	}
//
//	public int getX()
//	{
//		return m_xtile;
//	}
//
//	public int getY()
//	{
//		return m_ytile;
//	}
//
//	public int getZoom()
//	{
//		return m_zoom;
//	}
//
//}
