package ru.tcgeo.application.gilib.specs;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.gilib.specs.*;
import ru.tcgeo.gilib.specs.GICameraParserArrayItem;


public class GICameraParserArray /*extends GIParser */
{
	protected GICameraPList m_List;
	
	protected boolean SectionReading;
	protected XmlPullParser m_ParserCurrent;
	protected String section_name;
	
	public GICameraParserArray(XmlPullParser parent, GICameraPList list) 
	{
		SectionReading = true;
		m_ParserCurrent = parent;
		System.setProperty("true", "true");
		m_List = list;
		section_name = "array";
	}
	

	protected void ReadSectionsValues()
	{

	}
	

	protected void ReadSectionText()
	{

	}
	

	protected void readSectionEnties() throws XmlPullParserException 
	{
		if(m_ParserCurrent.getName().equalsIgnoreCase("dict"))
		{
			ru.tcgeo.gilib.specs.GICameraParserArrayItem parser = new GICameraParserArrayItem(m_ParserCurrent, m_List);
			m_ParserCurrent = parser.ReadSection();
		}
	}	
	
	protected void FinishSection()
	{
		return;
	}
	public XmlPullParser ReadSection() throws XmlPullParserException
	{
		if(m_ParserCurrent.getEventType() == XmlPullParser.START_TAG)
		{
			if(m_ParserCurrent.getName().equalsIgnoreCase(section_name))
			{
				ReadSectionsValues();
				
				SectionReading = true;
				//Section loop
				while(SectionReading && (m_ParserCurrent.getEventType() != XmlPullParser.END_DOCUMENT))
				{
					try
					{
						m_ParserCurrent.next();
					}
					catch(IOException e){/*Log.d(LOG_TAG, "Exception in " + section_name);*/}
					if(m_ParserCurrent.getEventType() == XmlPullParser.START_TAG)
					{
						readSectionEnties();
						continue;

					}
					if(m_ParserCurrent.getEventType() == XmlPullParser.TEXT)
					{
						ReadSectionText();
						continue;
					}

					if(m_ParserCurrent.getEventType() == XmlPullParser.END_TAG)
					{
						if(m_ParserCurrent.getName().equalsIgnoreCase(section_name))
						{
							SectionReading = false;
							//TODO check here
							/*try
							{
								m_ParserCurrent.next();
							}
							catch(IOException e){};*/
						}
						continue;
					}
				}
				FinishSection();
			}
		}
		return m_ParserCurrent;
	}
}
