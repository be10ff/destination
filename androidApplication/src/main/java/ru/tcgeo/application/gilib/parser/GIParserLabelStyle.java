package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.gilib.GIColor;
import ru.tcgeo.gilib.GILabelStyle;
import ru.tcgeo.gilib.parser.GIParser;
import ru.tcgeo.gilib.parser.GIParserColor;

public class GIParserLabelStyle extends GIParser
{
	GILabelStyle m_root;

	public GIParserLabelStyle(XmlPullParser parent, GILabelStyle root) 
	{
		super(parent);
		section_name = "LabelStyle";
		m_root = root;		
	}

	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("shadow"))
			{
				m_root.m_shadow = Boolean.parseBoolean(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("fontSize"))
			{
				m_root.m_fontSize = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("layout"))
			{
				m_root.m_layout = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Color"))
		{
			m_root.m_Color = new GIColor();
			GIParserColor parser = new GIParserColor(m_ParserCurrent,  m_root.m_Color);
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
