package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.GIIcon;

public class GIParserIcon extends GIParser
{
	//LayerProperties m_root;
	GIIcon m_root;
	//GIIcon m_current;
	
	public GIParserIcon(XmlPullParser parent, GIIcon root) 
	{
		super(parent);
		section_name = "Icon";
		m_root = root;		
		//m_current = new GIIcon();
	}
	
	@Override	
	protected void ReadSectionsValues()
	{
		return;
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Source"))
		{
			m_root.m_source = new GISource();
			GIParserSource parser = new GIParserSource(m_ParserCurrent, m_root.m_source);
			m_ParserCurrent = parser.ReadSection();
		}
	}
	
	@Override
	protected void FinishSection()
	{
		//m_root = m_current;
	}
	//-----------------------------------------------------------------
}
