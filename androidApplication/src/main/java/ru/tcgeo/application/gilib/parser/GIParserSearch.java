package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;

import ru.tcgeo.gilib.parser.GIParser;
import ru.tcgeo.gilib.parser.GIProjectProperties;

public class GIParserSearch extends GIParser
{
	GIParserSearch(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "search";
		ps.m_search_body = "";
		ps.m_search_file = "";
	}
	
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("file"))
			{
				m_ps.m_search_file = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	
	@Override
	protected void ReadSectionText()
	{
		m_ps.m_search_body = m_ParserCurrent.getText();
	}
}
