package ru.tcgeo.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.gilib.GILabel;
import ru.tcgeo.gilib.GILabelStyle;

public class GIParserLabel extends GIParser 
{
	GILabel m_root;

	public GIParserLabel(XmlPullParser parent, GILabel root) 
	{
		super(parent);
		section_name = "Label";
		m_root = root;		
	}

	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("semantic"))
			{
				m_root.m_semantic = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("LabelStyle"))
		{
			m_root.m_label_style = new GILabelStyle();
			GIParserLabelStyle parser = new GIParserLabelStyle(m_ParserCurrent, m_root.m_label_style);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("layer"))
		{
			m_root.m_layer = new GIPropertiesLayer();
			GIParserLayer parser = new GIParserLayer(m_ParserCurrent, m_root);
			m_ParserCurrent = parser.ReadSection();
		}
		
	}
	
	@Override
	protected void FinishSection()
	{
		//m_root = m_current;
		return;
	}
	//-----------------------------------------------------------------


}
