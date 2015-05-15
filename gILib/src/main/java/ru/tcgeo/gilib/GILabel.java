package ru.tcgeo.gilib;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import ru.tcgeo.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.gilib.parser.ILayersRoot;

public class GILabel extends GIPropertiesLayer implements ILayersRoot
{
	public GILabelStyle m_label_style;
	public GIPropertiesLayer m_layer;
	public long m_vector_layer_id;
	public GIVectorLayer m_vector_layer;
	public String m_semantic;
	/*LabelProperties()
	{
		m_label_style = new LabelStyleProperties();
		//m_layer = new LayerProperties();
		m_semantic = "";
	}*/
	public void addEntry(GIPropertiesLayer layer)
	{
		m_layer = layer;
	}
	
	public String ToString()
	{
		String Res = "Label \n";
		if(m_label_style != null)
		{		
			Res += "m_label_style=" + m_label_style.ToString();
		}
		if(m_layer != null)
		{
			Res +=  " m_layer=" + m_layer.ToString();
		}
		Res +=   " m_semantic=" + m_semantic +"\n";
		return Res;
	}
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Label");
		serializer.attribute("", "semantic", m_semantic);
		
		serializer.startTag("", "LabelStyle");
		serializer.attribute("", "shadow", String.valueOf(m_label_style.m_shadow));
		serializer.attribute("", "fontSize", String.valueOf(m_label_style.m_fontSize));
		serializer.attribute("", "layout", String.valueOf(m_label_style.m_layout));
		serializer = m_label_style.m_Color.Save(serializer);
		serializer.endTag("", "LabelStyle");
		if(m_layer != null)
		{
			serializer = m_layer.Save(serializer);
		}
		serializer.endTag("", "Label");
		return serializer;
	}
}
