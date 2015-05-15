package ru.tcgeo.gilib.specs;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import ru.tcgeo.gilib.parser.GIParserArrayItemKey;
import ru.tcgeo.gilib.parser.GIParserArrayItemValue;

public class GICameraParserArrayItem extends GICameraParserArray {
	GISpeedCamera m_item;
	String m_looking_for_key;
	GICameraParserArrayItem(XmlPullParser parent, GICameraPList list) 
	{
		super(parent, list);
		section_name = "dict";
		m_item = new GISpeedCamera();
		m_looking_for_key = new String();
	}
	@Override
	protected void ReadSectionsValues()
	{
	}
	
	@Override
	protected void ReadSectionText()
	{
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		StringBuffer lookingFor = new StringBuffer();
		if(m_ParserCurrent.getName().equalsIgnoreCase("key"))
		{
			GIParserArrayItemKey parser = new GIParserArrayItemKey(m_ParserCurrent, lookingFor);
			m_ParserCurrent = parser.ReadSection();
			m_looking_for_key = new String(lookingFor);
		}
		if(m_looking_for_key.equalsIgnoreCase("ID") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_ID = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("ID") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_ID = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("lon") && m_ParserCurrent.getName().equalsIgnoreCase("real"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "real", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_lon = Double.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("lat") && m_ParserCurrent.getName().equalsIgnoreCase("real"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "real", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_lat = Double.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("point_type") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_type = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("speed") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_speed = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("dir_type") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_direction_type = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("direction") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_direction = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("zone") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_zone = Integer.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("angle") && m_ParserCurrent.getName().equalsIgnoreCase("integer"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_angle = Integer.valueOf(value);
		}
		
		if(m_looking_for_key.equalsIgnoreCase("name") && m_ParserCurrent.getName().equalsIgnoreCase("string"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "string", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);				
			m_item.m_name = value;
		}


	}	
	
	protected void FinishSection()
	{
		m_List.m_list.add(m_item);
		return;
	}
	
}