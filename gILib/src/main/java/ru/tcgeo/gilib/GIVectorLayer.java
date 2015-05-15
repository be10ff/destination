package ru.tcgeo.gilib;

import java.util.ArrayList;
import android.graphics.Bitmap;

public class GIVectorLayer extends GILayer {
	GIVectorStyle m_style;
	ArrayList<GIVectorStyle> m_additional_styles;
	GIEncoding m_encoding;
	


	public GIVectorLayer(String path, GIVectorStyle style) {
		type_ = GILayerType.VECTOR_LAYER;
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
		m_renderer = new GIVectorRenderer(m_style);
		m_id = initVectorLayer(path);
		m_projection = new GIProjection(getProjection(m_id), true);
	}

	public GIVectorLayer(String path, GIVectorStyle style, GIEncoding encoding) {
		type_ = GILayerType.VECTOR_LAYER;
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
		m_renderer = new GIVectorRenderer(m_style);
		m_id = initVectorLayer(path);
		m_projection = new GIProjection(getProjection(m_id), true);
		m_encoding = encoding;
	}

	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity, double scale) 
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}
	}

	@Override
	public void RedrawLabels(GIBounds area, Bitmap bitmap, float scale_factor, double scale) 
	{
			
		if(HasLabel())
		{
			synchronized(this)
			{
				m_renderer.RenderText(this, area, bitmap, scale_factor, scale);
			}
		}

	}

	public void AddStyle(GIVectorStyle style) {
		m_renderer.AddStyle(style);
	}

	public GIEncoding encoding() {
		return m_encoding;
	}

	GIDataRequestor RequestDataIn(GIBounds point, GIDataRequestor requestor,
			double scale) {

		
		//GIBounds area = point.Reprojected(GIProjection.WGS84());
		GIBounds area = point.Reprojected(projection());
		// TODO 
		long features_count = GetFeatureCountInArea(m_id, area);
		long fields_count = GetFieldsCount(m_id);
		if(features_count > 0)
		{
			
			requestor.StartLayer(this);
			//ToDo seems this isnt optimal way. but its working
			for(long i = 0; i < features_count; i++)
			{
				if(fields_count > 0)
				{
					requestor.StartObject( new GIGeometry(i) );
					for(long j=0; j<fields_count; j++)
					{
						String field_name = GetFieldName(m_id, j);
						String field_value =  GetFieldAsString(m_id, j, area, i);					
						requestor.ProcessSemantic(field_name, field_value);
					}
					requestor.EndObject(new GIGeometry(i));
				}
			}
			requestor.EndLayer(this);	
		}

		return requestor;
	}
	public boolean Remove()
	{
		return RemoveLayer(m_id);
	}
	
	public int getType()
	{
		return m_renderer.getType(this);
	}
	
	// TODO: get projection, max values
	native long initVectorLayer(String path);

	//native String RequestDataIn(long layer_id, GIBounds area);
	native long GetFeatureCountInArea(long layer_id, GIBounds area);
	native long GetFieldsCount(long layer_id);
	native String GetFieldName(long layer_id, long field_id);
	native String GetFieldAsString(long layer_id, long field_id, GIBounds area, long feature_id);
	//native boolean RequestDataIn ();
	native boolean RemoveLayer(long layer_id);
	native long getLabels(long layer_id, String symantic, long bitmapWidth, long bitmapHeight, GIBounds area, GIEncoding encoding);
	native long getProjection(long layer_id);
	//native long getStr(long layer_id, String symantic, long bitmapWidth, long bitmapHeight, GIBounds area, GIEncoding encoding, GILabeledLayer labeled_layer);
}
