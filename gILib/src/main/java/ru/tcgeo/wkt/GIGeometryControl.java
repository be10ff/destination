package ru.tcgeo.wkt;

import java.util.ArrayList;
import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GIControl;
import ru.tcgeo.gilib.GIEditLayersKeeper;
import ru.tcgeo.gilib.GIEditableLayer;
import ru.tcgeo.gilib.GIGeometryPointControl;
import ru.tcgeo.gilib.GIMap;
import ru.tcgeo.wkt.GI_WktGeometry.GIWKTGeometryStatus;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

public class GIGeometryControl extends View implements GIControl
{
	Context m_context;
	GIMap m_map;
//	int[] map_location = { 0, 0 };
	public ArrayList<GIGeometryPointControl> m_points;
	public GI_WktGeometry m_geometry;
	public GIEditableLayer m_layer;
	public boolean mShow = true;
	
	public GIGeometryControl(GIEditableLayer layer, GI_WktGeometry geometry) 
	{
		super(GIEditLayersKeeper.Instance().getMap().getContext());
		this.setEnabled(false);
		m_points = new ArrayList<GIGeometryPointControl>();
		m_geometry = geometry;
		m_layer = layer;
		m_context = GIEditLayersKeeper.Instance().getMap().getContext();	
		m_map = GIEditLayersKeeper.Instance().getMap();
//    	m_map.getLocationOnScreen(map_location);
//		this.setX(map_location[0]);
//		this.setY(map_location[1]);
        m_map.registerGIControl(this);
        AddPoints();
		RelativeLayout rl = (RelativeLayout)m_map.getParent();//
    	rl.addView(this);
	}

	public void addPoint(GI_WktPoint point)
	{
		GIGeometryPointControl m_last = new GIGeometryPointControl(m_context, m_map);
		m_last.setWKTPoint(point);
		if((m_geometry.m_status == GIWKTGeometryStatus.GEOMETRY_EDITING) || (m_geometry.m_status == GIWKTGeometryStatus.NEW))
		{
			m_last.setActiveStatus(true);
		}
		m_points.add(m_last);
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.addView(m_last);
	}
	public void AddPoints()
	{
		switch(m_geometry.m_type)
		{
			case POINT:
			{
				GI_WktPoint point = (GI_WktPoint)m_geometry;
				addPoint(point);
				break;
			}
			case LINE:
			{
				GI_WktLinestring line = (GI_WktLinestring)m_geometry;
				for(GI_WktPoint point : line.m_points)
				{
					addPoint(point);
				}
				break;
			}
			case POLYGON:
			{
				GI_WktPolygon polygon = (GI_WktPolygon)m_geometry;
				for(GI_WktLinestring line : polygon.m_rings)
				{
					for(int i = 0; i < line.m_points.size() - 1; i++)
					{
						addPoint( line.m_points.get(i));
					}
				}
				break;
			}
		default:
			break;
		}
		invalidate();
	}

	@Override
    protected void onDraw(Canvas canvas)
	{
		if(mShow) {
			m_geometry.Paint(canvas, m_layer.getPaint(m_geometry.m_status));
		}
	}

	public GIMap Map() 
	{
		return m_map;
	}

	public void setMap(GIMap map) 
	{
		m_map = map;
		map.registerGIControl(this);
	}

	public void onMapMove() {
		// TODO Auto-generated method stub
	}

	public void onViewMove() 
	{
		invalidate();
	}

	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void onMarkerLayerRedraw(Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void afterViewRedraw() {
		// TODO Auto-generated method stub
		
	}


	public void Disable()
	{
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.removeView(this);
    	for(int  i = m_points.size() - 1; i >= 0; i--)
    	{
    		GIGeometryPointControl control = m_points.get(i);
    		rl.removeView(control);
    		m_points.remove(i);
    	}
	}

	public void Show(boolean show){
		mShow = show;
	}


}
