package ru.tcgeo.gilib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.graphics.Bitmap;

public class GIWMSLayer extends GILayer {

	String m_site;
	private String m_session_key;
	
	public GIWMSLayer() 
	{
		m_site = "http://geoportal.tc-geo.ru/gis/proxy.php?";
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIWMSRenderer();
		m_projection = GIProjection.WorldMercator();
		
		m_session_key = "";		
	}
	public GIWMSLayer(String path) 
	{
		m_site = path;
		type_ = GILayerType.ON_LINE;
		m_renderer = new GIWMSRenderer();
		m_projection = GIProjection.WorldMercator();
		m_session_key = "";
	}
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity,
			double scale) 
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}
	}
	
	private String Login()
	{
		String login = "TCMobileSolution";
		String password = "JvfEulpr2E";
        String result = "";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://geoportal.tc-geo.ru/gis/login.php");
		try
		{
			List<NameValuePair> post_params = new ArrayList<NameValuePair>();
			post_params.add(new BasicNameValuePair("auth", "1"));
			post_params.add(new BasicNameValuePair("auth_login", login));
			post_params.add(new BasicNameValuePair("auth_pass", password));
			
			httppost.setEntity(new UrlEncodedFormEntity(post_params));
			
			HttpResponse response = httpclient.execute(httppost);
	        if (response.getStatusLine().getStatusCode() == 200)
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
		return result;
	}
	
	public String getSessionKey()
	{
		if(m_session_key.equals(""))
		{
			m_session_key = Login();
		}
		return m_session_key;
	}

}
