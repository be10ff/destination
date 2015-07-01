package ru.tcgeo.application.gilib.parser;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import ru.tcgeo.gilib.GIColor;
import ru.tcgeo.gilib.GIEncoding;
import ru.tcgeo.gilib.GIIcon;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.parser.*;
import ru.tcgeo.gilib.parser.GISQLDB;

public class GIPropertiesLayer implements ILayersRoot
{
	public String m_name;
	public GILayer.GILayerType m_type;
	public String m_strType;
	public boolean m_enabled;
	public ArrayList<ru.tcgeo.gilib.parser.GIPropertiesLayer> m_Entries;
	public GIIcon m_icon;
	public GISource m_source;
	public GIEncoding m_encoding;
	public GIPropertiesStyle m_style;
	public GIRange m_range;
	public GISQLDB m_sqldb;
	public GIPropertiesLayer()
	{
		m_Entries = new ArrayList<ru.tcgeo.gilib.parser.GIPropertiesLayer>();
	}
	public void addEntry(ru.tcgeo.gilib.parser.GIPropertiesLayer layer)
	{
		m_Entries.add(layer);
	}
	
	public String ToString()
	{
		String Res = "Layer \n";
		Res += "name=" + m_name + " type=" + m_type + "\n";
		if(m_icon != null)
		{
		Res += m_icon.ToString() + "\n";
		}
		if(m_source != null)
		{
			Res += m_source.ToString() + "\n" ;
		}
		if(m_sqldb != null)
		{
			Res += m_sqldb.ToString() + "\n" ;
		}
		if(m_encoding != null)
		{
		Res += m_encoding.ToString() + "\n" ;
		}
		if(m_style != null)
		{
		Res += m_style.ToString() + "\n" ;
		}
		if(m_range != null)
		{
		Res += m_range.ToString() + "\n";
		}
		for(ru.tcgeo.gilib.parser.GIPropertiesLayer lr : m_Entries)
		{
			Res += lr.ToString() + "\n";
		}
		return Res;
	}
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Layer");
		serializer.attribute("", "name", m_name);
		serializer.attribute("", "type", m_strType);
		serializer.attribute("", "enabled", String.valueOf(m_enabled));
		
		serializer = m_source.Save(serializer);
		if(m_sqldb != null)
		{
			serializer = m_sqldb.Save(serializer);
		}
		if(m_range != null)
		{
			serializer = m_range.Save(serializer);
		}
		if(m_style != null)
		{
			serializer.startTag("", "Style");
			serializer.attribute("", "type", m_style.m_type);
			serializer.attribute("", "lineWidth",String.valueOf(m_style.m_lineWidth));
			serializer.attribute("", "opacity", String.valueOf(m_style.m_opacity));
			for(GIColor color : m_style.m_colors)
			{
				serializer = color.Save(serializer);
			}
			serializer.endTag("", "Style");
		}
		if(m_encoding != null)
		{
			serializer.startTag("", "Encoding");
			serializer.attribute("", "name", m_encoding.m_encoding);
			serializer.endTag("", "Encoding");
		}

		
		if(m_icon != null)
		{
			serializer.startTag("", "Icon");
			if(m_icon.m_source != null )
			{
				serializer =m_icon.m_source.Save(serializer);
			}
			serializer.endTag("", "Icon");
		}

		serializer.endTag("", "Layer");
		return serializer;
	}
}
