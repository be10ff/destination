package ru.tcgeo.gilib;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.tcgeo.gilib.planimetry.GIGeometryLine;
import ru.tcgeo.gilib.planimetry.GIGeometryPolygon;
import ru.tcgeo.gilib.planimetry.GILabeledLayer;
import ru.tcgeo.gilib.planimetry.GILabeledLines;
import ru.tcgeo.gilib.planimetry.GILabeledPolygons;
import ru.tcgeo.gilib.planimetry.GIShape;
import ru.tcgeo.gilib.planimetry.LabelText;
import ru.tcgeo.gilib.planimetry.Vertex;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

class LabelsMap
{
	public Map<PointF, String> m_labels;
	
	LabelsMap()
	{
		m_labels = new HashMap<PointF, String> ();
	}

	public void put (PointF point, String label)
	{
		m_labels.put(point, label);
	}
}

public class GIVectorRenderer extends GIRenderer
{
	Canvas m_canvas;
	//Path   m_path;
	GIVectorStyle m_style;
	ArrayList<GIVectorStyle> m_additional_styles;
	
	LabelsMap m_labels;
	ArrayList<Rect> m_drawed;
	static ArrayList<GIShape> m_used_space;
	
	GIVectorRenderer (GIVectorStyle style)
	{
		//m_path = new Path();
		m_canvas = new Canvas();
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
		
	}
	public static void ResetUsed()
	{
		m_used_space = new ArrayList<GIShape>();
	}
	@Override
	public void RenderImage (GILayer layer, GIBounds area, int opacity,
	        Bitmap bitmap, double scale)
	{
		//
		//long type = getLayerType(layer.m_id);
		//m_canvas.setBitmap(bitmap);
		m_canvas = new Canvas(bitmap);
		area = area.Reprojected(GIProjection.WGS84());
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		float scale_factor = (float) (scale/_scale);
		//TODO
		area = area.Reprojected(layer.projection());
		drawLayer(layer.m_id, bitmap.getWidth(), bitmap.getHeight(), area, scale_factor);
	}
	
	private boolean check_label (PointF point, String label, PointF layout)
	{
		// TODO: Paint issues
		Paint paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(15f);
		Typeface tf = Typeface.create("ARIAL", Typeface.BOLD);
		paint.setTypeface(tf);
		
		Rect label_bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), label_bounds);
		label_bounds.offset((int)(point.x + layout.x), (int)(point.y + layout.y));
		for (Rect drawed_rect : m_drawed)
		{
			if (Rect.intersects(drawed_rect,label_bounds))
				return false;
		}		
		m_drawed.add(label_bounds);
		GIGeometryPolygon poly = new GIGeometryPolygon(label_bounds);
		poly.m_labeltext = label;
		m_used_space.add(poly);
		return true;		 
	}
	private boolean check_label (PointF point, String label, PointF layout, float scale_factor)
	{
		// TODO: Paint issues
		Paint paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(15f*scale_factor);
		Typeface tf = Typeface.create("ARIAL", Typeface.BOLD);
		paint.setTypeface(tf);
		
		Rect label_bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), label_bounds);
		label_bounds.offset((int)(point.x  + layout.x) , (int)(point.y +  layout.y));
		for (Rect drawed_rect : m_drawed)
		{
			if (Rect.intersects(drawed_rect,label_bounds))
				return false;
		}	
		m_drawed.add(label_bounds);
		GIGeometryPolygon poly = new GIGeometryPolygon(label_bounds);
		poly.m_labeltext = label;
		m_used_space.add(poly);
		return true;		 
	}		
	public void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, double scale)
	{
		Paint paint = new Paint();
		paint.setColor(layer.m_label.m_label_style.m_Color.Get());
		paint.setTextSize(layer.m_label.m_label_style.m_fontSize);
        if(layer.m_label.m_label_style.m_shadow)
        {
        	paint.setShadowLayer(layer.m_label.m_label_style.m_fontSize/2, 0, 0, Color.WHITE);
        }
		paint.setAntiAlias(true);
		Typeface tf = Typeface.create("ARIAL", Typeface.BOLD);
		paint.setTypeface(tf);
		PointF layout = new PointF(0, 0);
		if(layer.m_label.m_label_style.m_layout.equalsIgnoreCase("under"))
		{
			layout.x = (float)1.25*layer.m_label.m_label_style.m_fontSize;
			layout.y = (float)1.25*layer.m_label.m_label_style.m_fontSize;
			
		}
		if(layer.m_label.m_label_style.m_layout.equalsIgnoreCase("onlevel"))
		{
			layout.x = (float)1.25*layer.m_label.m_label_style.m_fontSize;
		}
		m_canvas.setBitmap(bitmap);
		//area = area.Reprojected(GIProjection.WGS84());
		GIVectorLayer vlayer = (GIVectorLayer)layer;
		area = area.Reprojected(layer.projection());
		m_labels = new LabelsMap();		
		m_drawed = new ArrayList<Rect>();
		
		getText(layer.m_id,  bitmap.getWidth(), bitmap.getHeight(), area, vlayer.encoding(), m_labels);
		for (Map.Entry<PointF, String> entry : m_labels.m_labels.entrySet())
		{
			// TODO: Paint issues
			if(check_label(entry.getKey(), entry.getValue(), layout))
			{
				m_canvas.drawText(entry.getValue(), entry.getKey().x + layout.x, entry.getKey().y + layout.y, paint);
			}
		}
		RenderLabels(layer, area, bitmap, 1, scale);
	}
	
	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor, double scale)
	{
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		m_canvas.setBitmap(bitmap);
		area = area.Reprojected(GIProjection.WGS84());
		if(scale_factor == 1)
		{
			RenderText (layer, area, bitmap, scale);
		}
		else
		{
			Paint paint = new Paint();
			paint.setColor(layer.m_label.m_label_style.m_Color.Get());
			paint.setTextSize(layer.m_label.m_label_style.m_fontSize*scale_factor);
	        if(layer.m_label.m_label_style.m_shadow)
	        {
	        	paint.setShadowLayer(layer.m_label.m_label_style.m_fontSize/2, 0, 0, Color.WHITE);
	        }
			paint.setAntiAlias(true);
			Typeface tf = Typeface.create("ARIAL", Typeface.NORMAL);
			paint.setTypeface(tf);
			PointF layout = new PointF(0, 0);
			if(layer.m_label.m_label_style.m_layout.equalsIgnoreCase("under"))
			{
				layout.x = (float)1.25*layer.m_label.m_label_style.m_fontSize*scale_factor;
				layout.y = (float)1.25*layer.m_label.m_label_style.m_fontSize*scale_factor;
				
			}
			if(layer.m_label.m_label_style.m_layout.equalsIgnoreCase("onlevel"))
			{
				layout.x = (float)1.25*layer.m_label.m_label_style.m_fontSize*scale_factor;
			}
			m_canvas.setBitmap(bitmap);
			//area = area.Reprojected(GIProjection.WGS84());
			GIVectorLayer vlayer = (GIVectorLayer)layer;
			area = area.Reprojected(layer.projection());
			m_labels = new LabelsMap();		
			m_drawed = new ArrayList<Rect>();
			
			getText(layer.m_id,  bitmap.getWidth(), bitmap.getHeight(), area, vlayer.encoding(), m_labels);
			for (Map.Entry<PointF, String> entry : m_labels.m_labels.entrySet())
			{
				// TODO: Paint issues
				if(check_label(entry.getKey(), entry.getValue(), layout, scale_factor))
				{
					
					m_canvas.drawText(entry.getValue(), entry.getKey().x, entry.getKey().y + 20*scale_factor, paint);
				}
			}
			RenderLabels(layer, area, bitmap, scale_factor, scale); 
		}
		
	}

	//ARAB
	public void RenderLabels(GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor, double scale) 
	{
		Canvas canvas = new Canvas(bitmap);
		RectF bounds = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		//GIBounds _area = area.Reprojected(GIProjection.WGS84());
		//long width = bitmap.getWidth();
		//long height = bitmap.getHeight();
		GIVectorLayer vector_layer = null;
		if(layer.m_label.m_layer != null)
		{
			//подписи дорог и других слоев где геометрия задана отдельным слоем
			vector_layer = layer.m_label.m_vector_layer;
		}
		else
		{
			vector_layer = (GIVectorLayer) layer;
		}
		if(vector_layer == null)
		{
			return;
		}
		//GIBounds _area = area.Reprojected(vector_layer.projection());
		int layer_type = (int)getLayerType(vector_layer.m_id);
		switch(layer_type)
		{
    		case 2:
    		{
				GILabeledPolygons labels_array = new GILabeledPolygons();
				String conditions;
				if(GetFieldIndex(vector_layer.m_id, layer.m_label.m_semantic) != -1)
				{
					conditions = "( " + layer.m_label.m_semantic + " IS NOT NULL )";
					//conditions = "( OBJECTID = 3550 )";
					if(GetFieldIndex(vector_layer.m_id, "Est_Width") != -1)
					{
						int est = (int)Math.round((4/(scale*10000)));
						conditions = conditions + " AND (Est_Width >= " + est + " )";
					}
				}
				else
				{
					return;
				}
				//long features_count = getLabel(vector_layer.m_id, layer.m_label.m_semantic, conditions, width, height, _area,  vector_layer.m_encoding, labels_array);
				if(labels_array.m_shapes.size() > 0)
				{
					//long c = features_count;
					GILabeledLines linear = new GILabeledLines(canvas);
					for(int i = 0; i < labels_array.m_shapes.size(); i++)
					{
						//TODO
						GIGeometryLine current = (GIGeometryLine)labels_array.m_shapes.get(i);
						Charset charset_latin = Charset.forName("CP1252");
						//CharsetDecoder decoder = charset_latin.newDecoder();
						CharsetEncoder encoder_latin = charset_latin.newEncoder();
						CharBuffer uCharBuffer = CharBuffer.wrap(current.m_labeltext);
						String s = "";
						try 
						{
							ByteBuffer bbuf = encoder_latin.encode(uCharBuffer);
							Charset charset_1251 = Charset.forName("CP1251");
							CharsetDecoder decoder_1251 = charset_1251.newDecoder();
							CharBuffer cbuf = decoder_1251.decode(bbuf);
							s = cbuf.toString();
							
						} catch (CharacterCodingException e) {/*e.printStackTrace();*/}
			
						if(s.length() > 0)
						{
							current.m_labeltext = s;
						}

						//correct orientation
						current.m_objectID = i;
						current.generalize(layer.m_label.m_label_style.m_fontSize/4);
						//current.CorrectOrientation();
						linear.m_shapes.add(current);
					}

					Paint paint_text = new Paint();
			        paint_text.setColor(layer.m_label.m_label_style.m_Color.Get());
			        paint_text.setStyle(Style.FILL);
			        paint_text.setAntiAlias(true);
			        paint_text.setTextAlign(Align.CENTER);
			        paint_text.setTextSize(layer.m_label.m_label_style.m_fontSize);
			        if(layer.m_label.m_label_style.m_shadow)
			        {
			        	paint_text.setShadowLayer(layer.m_label.m_label_style.m_fontSize/2, 0, 0, Color.WHITE);
			        }
			        linear.FoundCandidates(bounds, m_used_space, paint_text);
			        linear.Draw(canvas, paint_text);
				}
    			break;
    		}
    		case  3:
			{
				GILabeledPolygons labels_array = new GILabeledPolygons();
				//String conditions;
				if(GetFieldIndex(layer.m_id, layer.m_label.m_semantic) != -1)
				{
					//conditions = "( " + layer.m_label.m_semantic + " IS NOT NULL )";
				}
				else
				{
					return;
				}
				//long features_count = getLabel(layer.m_id, layer.m_label.m_semantic, conditions, width, height, _area,  ((GIVectorLayer)layer).m_encoding, labels_array);
				Paint paint_text = new Paint();
		        paint_text.setColor(layer.m_label.m_label_style.m_Color.Get());
		        paint_text.setStyle(Style.STROKE);
		        paint_text.setAntiAlias(true);
		        paint_text.setTextSize(layer.m_label.m_label_style.m_fontSize);	
		        if(layer.m_label.m_label_style.m_shadow)
		        {
		        	paint_text.setShadowLayer(layer.m_label.m_label_style.m_fontSize/2, 0, 0, Color.WHITE);
		        }
				for(int i = 0; i < labels_array.m_shapes.size(); i++)
				{
					GIGeometryPolygon gg = (GIGeometryPolygon) labels_array.m_shapes.get(i);
					Charset charset_latin = Charset.forName("CP1252");
					//CharsetDecoder decoder = charset_latin.newDecoder();
					CharsetEncoder encoder_latin = charset_latin.newEncoder();
					CharBuffer uCharBuffer = CharBuffer.wrap(gg.m_labeltext);
					String s = "";
					try 
					{
						ByteBuffer bbuf = encoder_latin.encode(uCharBuffer);
						Charset charset_1251 = Charset.forName("CP1251");
						CharsetDecoder decoder_1251 = charset_1251.newDecoder();
						CharBuffer cbuf = decoder_1251.decode(bbuf);
						s = cbuf.toString();
						
					} catch (CharacterCodingException e) { /*e.printStackTrace();*/}
		
					if(s.length() > 0)
					{
						gg.m_labeltext = s;
					}
					if(gg.m_labeltext.length() > 0)
			        {
						LabelText nadpis = new LabelText(gg);
						ArrayList<Vertex> result = nadpis.FoundCandidates(bounds, paint_text);
						if(result != null)
						{
							nadpis.Draw(canvas, paint_text);
						}
			        }
				}
				break;
			}
    		default:  return;
    	}
	}

	
	@Override
	public void AddStyle (GIStyle style)
	{
		m_additional_styles.add((GIVectorStyle) style);
	}
	public int getType(GILayer layer)
	{
		return (int)getLayerType(layer.m_id);
	}

	native int drawLayer (long layerID, 
 						  int bitmapWidth,
						  int bitmapHeight, 
						  GIBounds area,
						  float scale);
	
	native int getText (long layerID, 
            		 	int bitmapWidth,
            		 	int bitmapHeight, 
            			GIBounds area,
            			GIEncoding encoding, 
            			LabelsMap labels);
	
	//native long getStr(long layer_id, String symantic, long bitmapWidth, long bitmapHeight, GIBounds area, GIEncoding encoding, GILabeledLayer labeled_layer);
	native long getLabel(long layer_id, String symantic, String conditions, long bitmapWidth, long bitmapHeight, GIBounds area, GIEncoding encoding, GILabeledLayer labeled_layer);
	native long getLayerType(long layer_id);
	native int GetFieldIndex(long layer_id, String name);

}
