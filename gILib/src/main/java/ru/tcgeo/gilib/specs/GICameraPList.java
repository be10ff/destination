package ru.tcgeo.gilib.specs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class GICameraPList {
	
//	public class GIMarker
//	{
//		public String m_name;
//		public String m_description;
//		public String m_image;
//		public double m_lon;
//		public double m_lat;
//		public double m_diag;
//	}
	public ArrayList<GISpeedCamera> m_list;
	
	public GICameraPList()
	{
		
		m_list = new ArrayList<GISpeedCamera>();
	}
	public void Load(String path)
	{

		try
		{
			XmlPullParser parser;
			FileInputStream xmlFile = null;
			try
			{
				xmlFile = new FileInputStream(path);
			}
			catch(FileNotFoundException e){}
			XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
			factiry.setNamespaceAware(true);
			parser = factiry.newPullParser();
			parser.setInput(xmlFile, null);
			try
			{
				while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
				{
					if(parser.getEventType() == XmlPullParser.START_TAG)
					{
						if(parser.getName().equalsIgnoreCase("array"))
						{
							GICameraParserArray parser_array = new GICameraParserArray(parser, this);
							parser = parser_array.ReadSection();
						}
					}
					parser.next();
				}
			}
			catch(IOException e)
			{}	
			finally {}

		}
		catch(XmlPullParserException e)
		{}
		return;
		
	}

}


