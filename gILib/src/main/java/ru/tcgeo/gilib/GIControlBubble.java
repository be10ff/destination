package ru.tcgeo.gilib;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GIControlBubble  extends LinearLayout implements GIControl, OnClickListener
{

	private GIMap m_map;
	private GILonLat m_PointOriginMap;
	private View m_LayoutView;
	public boolean hasClosed;
	private Context m_context;
	
	private String m_info_text;
	private static GIControlBubble instance;
	/**/
	public static GIControlBubble Instance(Context context, GIMap map)
	{
		if(instance == null)
		{
			instance = new GIControlBubble(context, map);
		}
		instance.hasClosed = true;

		return instance;
	}
	/*public static void SetProjectSettings(GIProjectProperties ps)
	{
		m_project_settings = ps;
	}*/
	public static void Show(GILonLat lonlat, String text)
	{
		if(instance != null)
		{
			instance.hasClosed = false;
			instance.setText(text);
			instance.setLonLat(lonlat);
			instance.onMapMove();
		}
	}

	private GIControlBubble (Context context, GIMap map/*, GILonLat lonlat, String text*/) 
	{
		super(context);
		setMap(map);
		m_context = context;	
		LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.info_control, this);	
		Button btn = (Button)findViewById(R.id.button_close_info);
		btn.setOnClickListener(this);
		hasClosed = false;
		//setText(text);
		//setLonLat(lonlat);
		TextView tv = (TextView)findViewById(R.id.control_info_text);
		tv.setOnClickListener(this);
		//onMapMove();
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.addView(this);
    	
    	 //m_script_parser = new GIScriptParser(m_project_settings.m_search_body);
	}
	
	public GIMap Map ()
	{
		return m_map;
	}
	public void setMap(GIMap map)
	{
		m_map = map;
		map.registerGIControl(this);
	}
	public void onMapMove()
	{
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void onViewMove()
	{
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect)
	{
		Visibility();
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));		
	}
	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect)
	{

	}
	public void onMarkerLayerRedraw(Rect view_rect){}
	public void afterViewRedraw(){}
	private void Visibility()
	{
		if(hasClosed)
		{return;}
        if(m_map.m_view.contains(m_map.MapToScreenTempo(m_PointOriginMap).x, m_map.MapToScreenTempo(m_PointOriginMap).y))
        {
        	this.setVisibility(View.VISIBLE);
        }
        else
        {
        	this.setVisibility(View.GONE);
        }
	}
	private void MoveTo(Point point)
	{
		Visibility();
		if(getVisibility() != View.VISIBLE)
		{return;}
        DisplayMetrics dm = m_context.getResources().getDisplayMetrics();
        	Point show_point = point;
			Point corrected_point = new Point(0, 0);
			if(show_point.x > dm.widthPixels - 483)
			{
				if(show_point.y > dm.heightPixels - 265)
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_right);
					corrected_point.x = show_point.x - 483 +16;
					corrected_point.y = show_point.y - 265 + 28;				
				}
				else
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_right);
					corrected_point.x = show_point.x - 483 +16 ;
					corrected_point.y = show_point.y -28;				
				}
			}
			else
			{
				if(show_point.y > dm.heightPixels - 265)
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_left);
					corrected_point.x = show_point.x - 16;
					corrected_point.y = show_point.y - 265 + 28;				
				}
				else
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_left);
					corrected_point.x = show_point.x - 16;
					corrected_point.y = show_point.y - 28;
				}
			}
			corrected_point.y += 67 + 5;
			corrected_point.x += 5;		
			setX(corrected_point.x);
	        setY(corrected_point.y);
	        invalidate();
	}
	public void setText(String info)
	{
		m_info_text = info;
       	TextView tv = (TextView)findViewById(R.id.control_info_text);
		tv.setText(m_info_text);
	}
	public void setLonLat(GILonLat lonlat)
	{
		m_PointOriginMap = m_map.MetersToDegrees(lonlat);
	}
	public void Close()
	{
		setVisibility(View.GONE);
    	hasClosed = true;
    	m_map.unRegisterGIControl(this);
    	RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.removeView(this);
    	instance = null;
	}
	public void onClick(View v) 
	{
    	if(v.getId() == R.id.button_close_info)
    	{
    		Close();
    	}
    	if(v.getId() == R.id.control_info_text)
    	{
			this.bringToFront();
    	}
    		
	}


}
