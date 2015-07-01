package ru.tcgeo.application.gilib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.GIRenderer;
import ru.tcgeo.gilib.GIStyle;
import ru.tcgeo.gilib.GIWMSLayer;

public class GIWMSRenderer extends GIRenderer {

	Canvas m_canvas;
	public GIWMSRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity,
			Bitmap bitmap, double scale) 
	{

		String result = "";
		
		m_canvas = new Canvas(bitmap);
		area = area.Reprojected(layer.projection());
	
		String session = ((GIWMSLayer)layer).getSessionKey();
		
		String bbox_string = String.format(Locale.ENGLISH, "%f,%f,%f,%f", area.m_left, area.m_bottom, area.m_right, area.m_top); 
		//
		HttpClient httpclient = new DefaultHttpClient();
		try
		{
			List<NameValuePair> post_params = new ArrayList<NameValuePair>();
			post_params.add(new BasicNameValuePair("REQUEST", "GetMap"));
			post_params.add(new BasicNameValuePair("SERVICE", "WMS"));
			post_params.add(new BasicNameValuePair("VERSION", "1.1.1"));
			post_params.add(new BasicNameValuePair("MAP", "GIS_EGKO"));
			post_params.add(new BasicNameValuePair("portal_config", "6"));
			post_params.add(new BasicNameValuePair("LAYERS", "FOREST,SPECTERR_POLY,WATER,BOG,FIELD,GLADE,ISLAND,CANAL,ADDRESS,LAND,PAVILION,PLATFORM,LIMIT,ROAD,WAY,BRIDGE,NETWORK,SPECTERR,NAME,XARACTER"));//osm //Img_Sample
			post_params.add(new BasicNameValuePair("FORMAT", "aggpng24"));
			post_params.add(new BasicNameValuePair("BBOX", bbox_string));
			post_params.add(new BasicNameValuePair("WIDTH",String.valueOf(m_canvas.getWidth())));
			post_params.add(new BasicNameValuePair("HEIGHT", String.valueOf(m_canvas.getHeight())));
			post_params.add(new BasicNameValuePair("SRS", "EPSG:3395"));
			post_params.add(new BasicNameValuePair("STYLES", ""));
			post_params.add(new BasicNameValuePair("session_id", session));

			String paramString = "http://geoportal.tc-geo.ru/gis/proxy.php?" + URLEncodedUtils.format(post_params, "utf-8");
			HttpGet httpget = new HttpGet(paramString);

			HttpResponse response = httpclient.execute(httpget);
	        if (response.getStatusLine().getStatusCode() == 200)
	        {
	            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
	            InputStream instream = bufHttpEntity.getContent();
	            Bitmap bit_tile = BitmapFactory.decodeStream(instream);
	            if(bit_tile != null)
	            {
	            	m_canvas.drawBitmap(bit_tile, 0, 0, null);
	            }
	        }
	        else
	        {
	            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
	            InputStream instream = bufHttpEntity.getContent();
				String line; 
				
				BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) ); 
				while ( (line = serverResponse.readLine() ) != null )   
				{  
					result = result + line;  
				} 
	        }

	    } catch (ClientProtocolException e) {} catch (IOException e) {}


	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			float scale_factor, double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AddStyle(GIStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
