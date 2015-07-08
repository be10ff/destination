//package ru.tcgeo.application.gilib;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Point;
//import android.graphics.Rect;
//
//import ru.tcgeo.application.gilib.models.GILonLat;
//import ru.tcgeo.application.gilib.models.GIStyle;
//
//
//public class GIGoogleRenderer extends GIRenderer {
//
//	Canvas m_canvas;
//	public static int count;
//	public GIGoogleRenderer() {
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public void RenderImage(GILayer layer, GIBounds area, int opacity,
//			Bitmap bitmap, double scale)
//	{
//   	 	String err = "";
//
//		//int tile_width = bitmap.getWidth()/2;
//		//int tile_height = bitmap.getHeight()/2;
//
//		m_canvas = new Canvas(bitmap);
//		area = area.Reprojected(layer.projection());
//		double kf = 360.0f/bitmap.getWidth();
//
//		double width = area.m_right - area.m_left;
//        double dz = Math.log(bitmap.getWidth()*kf/width)/Math.log(2);
//        int z = (int) Math.round(dz);
//
//    	//float koeffX = (float) (bitmap.getWidth() / (area.m_right - area.m_left));
//    	//float koeffY = (float) (bitmap.getHeight() / (area.m_top - area.m_bottom));
//
//    	ArrayList<GITileInfoGoogle> tiles = new ArrayList<GITileInfoGoogle>();
//
//    	GILonLat t_l1 = ScreenToMap(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Point(0, 0));
//    	GILonLat b_r1 = ScreenToMap(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Point(400, 400));
//
//    	GILonLat geo_tl1 = geoToMercator(t_l1);
//    	GILonLat geo_br1 = geoToMercator(b_r1);
//
//    	tiles.add( new GITileInfoGoogle(z+3, geo_tl1, geo_br1, 400, 400));
//
//    	/*GILonLat t_l2 = ScreenToMap(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Point(400, 0));
//    	GILonLat b_r2 = ScreenToMap(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Point(800, 400));
//
//    	GILonLat geo_tl2 = geoToMercator(t_l2);
//    	GILonLat geo_br2 = geoToMercator(b_r2);
//
//    	tiles.add( new GITileInfoGoogle(z+3, t_l2, b_r2, 400, 400));*/
//
//
//
//    	/*tiles.add( new GITileInfoGoogle(z, area.TopLeft(), area.Center(), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, area.Center(), area.BottomRight(), tile_width, tile_height));*/
//
//    	/*tiles.add( new GITileInfoGoogle(z, area.TopLeft(), new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, (area.BottomRight().lat() + area.TopLeft().lat())/2), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, area.TopLeft().lat()),
//    			new GILonLat(area.BottomRight().lon() , (area.BottomRight().lat() + area.TopLeft().lat())/2), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat(area.TopLeft().lon(), (area.BottomRight().lat() + area.TopLeft().lat())/2),
//    			new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2 , area.BottomRight().lat() ), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, (area.BottomRight().lat() + area.TopLeft().lat())/2),
//    			area.BottomRight(), tile_width, tile_height));*/
//    	try
//        {
//
//        	for(int i = 0; i < tiles.size(); i++)
//        	{
//        		GITileInfoGoogle tile = tiles.get(i);
//        		//GITileInfoGoogle tile = new GITileInfoGoogle(z, area.TopLeft(), area.BottomRight(), tile_width, tile_height);
//
//
//    			String urlStr = tile.getURL_spn();
//    			URL url = new URL(urlStr);
//    	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//    	        //
//    	        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
//    	        urlConnection.setRequestProperty("Accept","*/*");
//    	        urlConnection.setDoOutput(false);
//    	        //urlConnection.setDoInput(false);
//    	        urlConnection.setConnectTimeout(30000);
//    	        urlConnection.setDefaultUseCaches(false);
//    	        //
//    	        urlConnection.connect();
//    	        int status = urlConnection.getResponseCode();
//    	        if(status == 200)
//    	        {
//			        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//			        Bitmap bit_tile = BitmapFactory.decodeStream(in);
//
//					//Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getHeight());
//
//					//float left_scr = (float)((tile.m_bounds.TopLeft().lon() - area.m_left) * koeffX);
//					//float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - area.m_bottom) * koeffY);
//					//float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - area.m_left) * koeffX);
//					//float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - area.m_bottom) * koeffY);
//
//					//RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
//					//m_canvas.drawBitmap(bit_tile, src, dst, null);
//
//					//float center_x = (float)((tile.m_center.lon() - area.m_left) * koeffX);
//					//float center_y = (float)(bitmap.getHeight() - (tile.m_center.lat() - area.m_bottom) * koeffY);
//
//					//m_canvas.drawBitmap(bit_tile, src, dst, null);
//					m_canvas.drawBitmap(bit_tile, 0, 0, null);
//
//					bit_tile.recycle();
//	    	    }
//    	        else
//    	        {
//		        	 BufferedReader serverResponse = new BufferedReader( new InputStreamReader( urlConnection.getErrorStream() ) );
//		        	 String line;
//    	 			while ( (line = serverResponse.readLine() ) != null )
//    				{
//    	 				err = err + line;
//    				}
//
//    	        }
//
//    	        urlConnection.disconnect();
//
//        	}
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//        };
//	}
//	public void RenderImage_goggle(GILayer layer, GIBounds area, int opacity,
//			Bitmap bitmap, double scale)
//	{
//   	 	String err = "";
//		m_canvas = new Canvas(bitmap);
//		area = area.Reprojected(layer.projection());
//
//		int tile_width = bitmap.getWidth()/2;
//		int tile_height = bitmap.getHeight()/2;
//		double kf = 360.0f/tile_width;
//
//        double width = area.m_right - area.m_left;
//
//        double dz = Math.log(bitmap.getWidth()*kf/width)/Math.log(2);
//        int z = (int) Math.round(dz) + 2;
//
//
//        //GITileInfoOSM left_top_tile = new GITileInfoOSM(z, area.m_left, area.m_top);
//        //GITileInfoOSM right_bottom_tile = new GITileInfoOSM(z, area.m_right, area.m_bottom);
//
//    	float koeffX = (float) (bitmap.getWidth() / (area.m_right - area.m_left));
//    	float koeffY = (float) (bitmap.getHeight() / (area.m_top - area.m_bottom));
//
//
//    	ArrayList<GITileInfoGoogle> tiles = new ArrayList<GITileInfoGoogle>();
//
//    	tiles.add( new GITileInfoGoogle(z, area.TopLeft(), new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, (area.BottomRight().lat() + area.TopLeft().lat())/2), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, area.TopLeft().lat()),
//    			new GILonLat(area.BottomRight().lon() , (area.BottomRight().lat() + area.TopLeft().lat())/2), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat(area.TopLeft().lon(), (area.BottomRight().lat() + area.TopLeft().lat())/2),
//    			new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2 , area.BottomRight().lat() ), tile_width, tile_height));
//    	tiles.add( new GITileInfoGoogle(z, new GILonLat((area.BottomRight().lon() + area.TopLeft().lon())/2, (area.BottomRight().lat() + area.TopLeft().lat())/2),
//    			area.BottomRight(), tile_width, tile_height));
//		count = count + 4;
//    	try
//        {
//        	for(int i = 0; i < tiles.size(); i++)
//        	{
//        		GITileInfoGoogle tile = tiles.get(i);
//
//    			String urlStr = tile.getURL();
//    			URL url = new URL(urlStr);
//    	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//    	        //
//    	        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
//    	        urlConnection.setRequestProperty("Accept","*/*");
//    	        urlConnection.setDoOutput(false);
//    	        urlConnection.setDoInput(false);
//    	        urlConnection.setConnectTimeout(30000);
//    	        urlConnection.setDefaultUseCaches(false);
//    	        //
//    	        urlConnection.connect();
//    	        int status = urlConnection.getResponseCode();
//    	        if(status == 200)
//    	        {
//			        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//			        Bitmap bit_tile = BitmapFactory.decodeStream(in);
//
//					//Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getHeight());
//
//					//float left_scr = (float)((tile.m_bounds.TopLeft().lon() - area.m_left) * koeffX);
//					//float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - area.m_bottom) * koeffY);
//					//float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - area.m_left) * koeffX);
//					//float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - area.m_bottom) * koeffY);
//
//					//RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
//					//m_canvas.drawBitmap(bit_tile, src, dst, null);
//
//					float center_x = (float)((tile.m_center.lon() - area.m_left) * koeffX);
//					float center_y = (float)(bitmap.getHeight() - (tile.m_center.lat() - area.m_bottom) * koeffY);
//
//					m_canvas.drawBitmap(bit_tile, center_x - tile_width/2, center_y - tile_height/2, null);
//
//					bit_tile.recycle();
//	    	    }
//    	        else
//    	        {
//		        	 BufferedReader serverResponse = new BufferedReader( new InputStreamReader( urlConnection.getErrorStream() ) );
//		        	 //InputStream in = new BufferedInputStream(urlConnection.getErrorStream());
//		        	 String line;
//    	 			while ( (line = serverResponse.readLine() ) != null )
//    				{
//    	 				err = err + line;
//    				}
//
//    	        }
//
//    	        urlConnection.disconnect();
//        	}
//
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//        };
//	}
//
//	@Override
//	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
//			double scale) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
//			float scale_factor, double scale) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void AddStyle(GIStyle style) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public int getType(GILayer layer) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	public GILonLat ScreenToMap(GIBounds m_bounds, Rect m_view_rect, Point point)
//	{
//		double pixelWidth = m_bounds.width() / m_view_rect.width();
//		double pixelHeight = m_bounds.height() / m_view_rect.height();
//		double lon = m_bounds.left() + pixelWidth*point.x;
//		double lat = m_bounds.top() - pixelHeight*point.y;
//		GILonLat lonlat = new GILonLat(lon, lat);
//		//GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
//		return lonlat;
//	}
//
//	 public double[] geoToMercator(double[] g) {
//	        double d = g[0] * Math.PI / 180, m = g[1] * Math.PI / 180, l = 6378137, k = 0.0818191908426, f = k
//	                * Math.sin(m);
//	        double h = Math.tan(Math.PI / 4 + m / 2), j = Math.pow(
//	                Math.tan(Math.PI / 4 + Math.asin(f) / 2), k), i = h / j;
//	        // return new DoublePoint(Math.round(l * d), Math.round(l *
//	        // Math.log(i)));
//	        return new double[] { l * d, l * Math.log(i) };
//	    }
//	 public GILonLat geoToMercator(GILonLat point)
//	 {
//
//		 double[] res = geoToMercator(new double[] { point.lon(),  point.lat() });
//		 return new GILonLat(res[0], res[1]);
//	 }
//
//	    public double[] mercatorToGeo(double[] e) {
//	        double j = Math.PI, f = j / 2, i = 6378137, n = 0.003356551468879694, k = 0.00000657187271079536, h = 1.764564338702e-8, m = 5.328478445e-11;
//	        double g = f - 2 * Math.atan(1 / Math.exp(e[1] / i));
//	        double l = g + n * Math.sin(2 * g) + k * Math.sin(4 * g) + h
//	                * Math.sin(6 * g) + m * Math.sin(8 * g);
//	        double d = e[0] / i;
//	        return new double[] { d * 180 / Math.PI, l * 180 / Math.PI };
//	    }
//		 public GILonLat mercatorToGeo(GILonLat point)
//		 {
//
//			 double[] res = mercatorToGeo(new double[] { point.lon(),  point.lat() });
//			 return new GILonLat(res[0], res[1]);
//		 }
//
//}
