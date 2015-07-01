package ru.tcgeo.application.gilib.parser;//
//package ru.tcgeo.gilib.parser;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//public class GIParserPackage extends GIParser {
//
//	GIPropertiesPackage m_current;
//	public GIParserPackage(XmlPullParser parent, GIProjectProperties ps)
//	{
//		super(parent, ps);
//		section_name = "Package";
//		m_current = new GIPropertiesPackage();
//	}
//	//<Package name="MoscowRaster" size="2800000000" CRC="a11b31995e44b5e447f0a01ce309d085" ID="2">
//	@Override
//	protected void ReadSectionsValues()
//	{
//		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
//		{
//			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
//			{
//				m_current.m_name = m_ParserCurrent.getAttributeValue(i);
//			}
//			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("size"))
//			{
//				m_current.m_size = Long.valueOf(m_ParserCurrent.getAttributeValue(i));
//			}
//			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("CRC"))
//			{
//				m_current.m_crc = m_ParserCurrent.getAttributeValue(i);
//			}
//			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("ID"))
//			{
//				m_current.m_id = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
//			}
//		}
//	}
//
//	@Override
//	protected void readSectionEnties() throws XmlPullParserException
//	{
//		String CurrentSectionName = m_ParserCurrent.getName();
//		if(CurrentSectionName.equalsIgnoreCase("File"))
//		{
//			GIParserFile parser = new GIParserFile(m_ParserCurrent, m_current);
//			m_ParserCurrent = parser.ReadSection();
//		}
//
//	}
//
//	@Override
//	protected void FinishSection()
//	{
//			m_ps.m_Entries.add(m_current);
//
//	}
//}
//
//
