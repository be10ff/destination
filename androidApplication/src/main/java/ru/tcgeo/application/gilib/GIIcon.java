package ru.tcgeo.application.gilib;

import ru.tcgeo.application.gilib.parser.GISource;

public class GIIcon
{
	public GISource m_source;
	/*IconProperties()
	{
		m_source = new SourceProperties();
	}*/
	public String ToString()
	{
		String Res = "Icon \n";
		if(m_source != null)
		{
			Res += "m_source=" + m_source.ToString() +  "\n";
		}
		return Res;
	}	
}
