package ru.tcgeo.gilib.parser;

import org.xmlpull.v1.XmlPullParser;


public class GIParserFile extends GIParser
{
	GIPropertiesPackage m_root;
	GIPropertiesFile m_current;

	public GIParserFile(XmlPullParser parent, GIPropertiesPackage root)
	{
		super(parent);
		section_name = "File";
		m_root = root;
		m_current = new GIPropertiesFile();
	}

	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_current.m_name = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("enabled"))
			{
				m_current.m_crc = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}



	@Override
	protected void FinishSection()
	{
		m_root.m_Entries.add(m_current);
	}
	//-----------------------------------------------------------------
}
