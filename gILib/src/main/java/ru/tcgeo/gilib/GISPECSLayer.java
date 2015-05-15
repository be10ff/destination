package ru.tcgeo.gilib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import ru.tcgeo.gilib.GIEditableLayer.GIEditableLayerStatus;
import ru.tcgeo.gilib.GILayer.GILayerType;
import ru.tcgeo.gilib.gps.GIYandexUtils;
import ru.tcgeo.gilib.specs.GICameraPList;
import ru.tcgeo.gilib.specs.GIQuadTreeDouble;
import ru.tcgeo.gilib.specs.GISpeedCamera;
import ru.tcgeo.gilib.specs.GISpeedCar;
import ru.tcgeo.gilib.specs.GITreeTile;
import ru.tcgeo.gilib.specs.GeoBounds;
import ru.tcgeo.wkt.GIDBaseField;
import ru.tcgeo.wkt.GIGPSParser;
import ru.tcgeo.wkt.GIWKTParser;
import ru.tcgeo.wkt.GI_WktGeometry;
import ru.tcgeo.wkt.GI_WktPolygon;
import ru.tcgeo.wkt.GI_WktUserTrack;
import ru.tcgeo.wkt.GI_WktGeometry.GIWKTGeometryStatus;
import ru.tcgeo.wkt.GI_WktGeometry.GIWKTGeometryType;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class GISPECSLayer extends GIEditableLayer
{
	GIQuadTreeDouble m_tree;
	public GISPECSLayer(String path) 
	{
		super(path);
		type_ = GILayerType.PLIST;
	}
	public GISPECSLayer(String path, GIVectorStyle style) 
	{
		super(path, style);
		type_ = GILayerType.PLIST;
	}

	public GISPECSLayer(String path, GIVectorStyle style, GIEncoding encoding) 
	{
		super(path, style, encoding);
		type_ = GILayerType.PLIST;
	}


	public void Load()
	{
//		GICameraPList PList = new GICameraPList();
//		
//		PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_cameras.plist"); // "/sdcard/" /cameras.plist
//		for(GISpeedCamera cam : PList.m_list)
//		{
//			cam.Make();
//			cam.calculatingMorton();
//		}
		
//		SQLiteDatabase db; 
//		Cursor c;	
//		try
//		{
//			db = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_cameras.db", null, SQLiteDatabase.OPEN_READWRITE);
//			String sql_string_objects = String.format("SELECT * FROM cameras");
//			c = db.rawQuery(sql_string_objects, null);	
//		    if (c.moveToFirst())
//		    {
//		    	int i_id = c.getColumnIndex("id");
//		    	int i_lon = c.getColumnIndex("lon");
//		    	int i_lat = c.getColumnIndex("lat");
//		    	int i_type = c.getColumnIndex("point_type");
//		    	int i_speed = c.getColumnIndex("speed");
//		    	int i_dir_type = c.getColumnIndex("dir_type");
//		    	int i_dir = c.getColumnIndex("direction");
//		    	int i_zone = c.getColumnIndex("zone");
//		    	int i_angle = c.getColumnIndex("angle");
//		    	int i_name = c.getColumnIndex("name");
//		    	int i_x = c.getColumnIndex("x");
//		    	int i_y = c.getColumnIndex("y");
//		    	int i_z = c.getColumnIndex("z");		
//		    	int i_start = c.getColumnIndex("start");
//				int i_end = c.getColumnIndex("end");
//		    	while ( !c.isAfterLast() )
//		        {
//		    		GISpeedCamera cam = new GISpeedCamera();
//
//		    		
//		    		cam.m_ID =  c.getInt(i_id);
//		    		cam.m_lon =  c.getDouble(i_lon);
//		    		cam.m_lat =  c.getDouble(i_lat);
//		    		cam.m_type =  c.getInt(i_type);
//		    		cam.m_speed =  c.getInt(i_speed);
//		    		cam.m_direction_type =  c.getInt(i_dir_type);
//		    		cam.m_direction =  c.getInt(i_dir);
//		    		cam.m_zone =  c.getInt(i_zone);
//		    		cam.m_angle =  c.getInt(i_angle);
//		    		cam.m_name =  c.getString(i_name);
//		    		int tile_x  =  c.getInt(i_x);
//		    		int tile_y  =  c.getInt(i_y);
//		    		int z  =  c.getInt(i_z);		    		
//		    		cam.m_tile = new GITreeTile(z, tile_x, tile_y);
//		    		cam.m_morton_start =  c.getLong(i_start);
//		    		cam.m_morton_end =  c.getLong(i_end);		    		
//		    		cam.Make();
//		    		cam.calculatingMorton();
//		    		PList.m_list.add(cam);
//		            c.moveToNext();
//		        }
//		    }	
//	        c.close(); 
//	        db.close();
//		}
//		catch(Exception e)
//		{
//			Log.d("LOG_TAG", e.toString());
//		}		
//
		
//		m_tree = new GIQuadTreeDouble();
//		m_tree.setShapes(PList.m_list);
//		m_tree.Sort();
//		
//		Save();
    }

	@Override
	public void DeleteObject(GI_WktGeometry geometry) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void AddGeometry(GI_WktGeometry geometry) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Save() 
	{
		SQLiteDatabase db; 
		try
		{
			db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_cameras.db", null, SQLiteDatabase.OPEN_READONLY);
			//
			ArrayList<GISpeedCamera> result = new ArrayList<GISpeedCamera>();
			m_tree.GetAll(result);
			for(GISpeedCamera cam : result)
			{
				ContentValues values = new ContentValues();
				values.put("id", cam.m_ID);
				values.put("lon", cam.m_lon);
				values.put("lat", cam.m_lat);	
				values.put("point_type", cam.m_type);
				values.put("speed", cam.m_speed);
				values.put("dir_type", cam.m_direction_type);
				values.put("direction", cam.m_direction);
				values.put("zone", cam.m_zone);
				values.put("angle", cam.m_angle);
				values.put("name", cam.m_name);
				values.put("x", cam.m_tile.m_xtile);
				values.put("y", cam.m_tile.m_ytile);
				values.put("z", cam.m_tile.m_zoom);
				values.put("start", cam.m_morton_start);
				values.put("end", cam.m_morton_end);
				db.insert("cameras", null, values);
			}
			db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}

	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity, double scale) 
	{

		GIBounds wgs = area.Reprojected(GIProjection.WGS84());
		
		getShapes(wgs);
		
		

		if(m_Status == GIEditableLayerStatus.UNEDITED)
		{
			synchronized(this)
			{
				m_renderer.RenderImage(this, area, opacity, bitmap, scale);
			}
		}
	}
	
	private void getShapes(GIBounds area)
	{
		m_shapes.clear();
	
		long[] morton_range = GISpeedCamera.getMorton(area);
		//morton_range [2277226146, 2366707539]
		GILonLat center = area.Center();
		//GISpeedCamera me = new GISpeedCamera(center);
		GISpeedCar me = new GISpeedCar(center.lon(), center.lat(), .20, 45);
		Location loc = GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation("mock");
		if(loc != null)
		{
			me = new GISpeedCar(loc);

		}
		GI_WktPolygon me_poly = me.getWKTPolygon();
		//me_poly.m_isTought = true;
		m_shapes.add(me_poly);
		me.calculatingMorton();
	
		SQLiteDatabase db; 
		Cursor c;
		try
		{
			db = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cameras_morton.db", null, SQLiteDatabase.OPEN_READWRITE);
			String sql_string_objects = String.format("SELECT * FROM cameras WHERE end >= %d AND start <= %d", morton_range[0],  morton_range[1]);
			c = db.rawQuery(sql_string_objects, null);	
		    if (c.moveToFirst())
		    {
		    	int i_id = c.getColumnIndex("id");
		    	int i_lon = c.getColumnIndex("lon");
		    	int i_lat = c.getColumnIndex("lat");
		    	int i_type = c.getColumnIndex("point_type");
		    	int i_speed = c.getColumnIndex("speed");
		    	int i_dir_type = c.getColumnIndex("dir_type");
		    	int i_dir = c.getColumnIndex("direction");
		    	int i_zone = c.getColumnIndex("zone");
		    	int i_angle = c.getColumnIndex("angle");
		    	int i_name = c.getColumnIndex("name");
		    	int i_x = c.getColumnIndex("x");
		    	int i_y = c.getColumnIndex("y");
		    	int i_z = c.getColumnIndex("z");		
		    	int i_start = c.getColumnIndex("start");
				int i_end = c.getColumnIndex("end");
		    	while ( !c.isAfterLast() )
		        {
		    		GISpeedCamera cam = new GISpeedCamera();
		    		cam.m_ID =  c.getInt(i_id);
		    		cam.m_lon =  c.getDouble(i_lon);
		    		cam.m_lat =  c.getDouble(i_lat);
		    		cam.m_type =  c.getInt(i_type);
		    		cam.m_speed =  c.getInt(i_speed);
		    		cam.m_direction_type =  c.getInt(i_dir_type);
		    		cam.m_direction =  c.getInt(i_dir);
		    		cam.m_zone =  c.getInt(i_zone);
		    		cam.m_angle =  c.getInt(i_angle);
		    		cam.m_name =  c.getString(i_name);
		    		int tile_x  =  c.getInt(i_x);
		    		int tile_y  =  c.getInt(i_y);
		    		int z  =  c.getInt(i_z);		    		
		    		cam.m_tile = new GITreeTile(z, tile_x, tile_y);
		    		cam.m_morton_start =  c.getLong(i_start);
		    		cam.m_morton_end =  c.getLong(i_end);		    		
		    		cam.Make();
		    		cam.calculatingMorton();
		    		//res.add(cam);
		    		GI_WktPolygon cam_poly = cam.getWKTPolygon();
		    		//TODO
		    		if(cam.m_morton_end >= me.m_morton_start && cam.m_morton_start <= me.m_morton_end)
		    		{
		    			if(me.isIntersectByCamera(cam))
		    			{
		    				//cam_poly.m_isTought = true;
		    			}
		    		}
		    		m_shapes.add(cam_poly);
		            c.moveToNext();
		        }
		    }	
	        c.close(); 
	        db.close();
    		//Log.d("SPECS", "cams amount = " + m_shapes.size());	        
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}		
	}
}
