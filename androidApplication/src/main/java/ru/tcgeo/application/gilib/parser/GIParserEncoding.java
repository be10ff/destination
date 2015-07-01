package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.gilib.GIEncoding;
import ru.tcgeo.gilib.parser.GIParser;

public class GIParserEncoding extends ru.tcgeo.application.gilib.parser.GIParser {
	ru.tcgeo.application.gilib.GIEncoding m_root;
	
	public GIParserEncoding(XmlPullParser parent, ru.tcgeo.application.gilib.GIEncoding root)
	{
		super(parent);
		section_name = "Encoding";
		m_root = root;		
	}
	
	@Override	
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				//
				//m_root.m_encoding = m_ParserCurrent.getAttributeValue(i);
				String encoding = m_ParserCurrent.getAttributeValue(i);
				m_root = new ru.tcgeo.application.gilib.GIEncoding(encoding);
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}
	
	@Override
	protected void FinishSection()
	{
		//m_root.m_icon = m_current;
		return;
	}
	//-----------------------------------------------------------------
}
