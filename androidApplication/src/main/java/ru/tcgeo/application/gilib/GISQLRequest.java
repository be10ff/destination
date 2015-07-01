package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import ru.tcgeo.gilib.AddressSearchAdapterItem;

public class GISQLRequest {
	public String m_text;
	public String m_mode;
	public String m_path;
	public ArrayList<AddressSearchAdapterItem> m_array;
	
	public GISQLRequest() 
	{
		// TODO Auto-generated constructor stub
		m_text = "";
		m_mode = "";
		m_path = "";
		m_array = new ArrayList<AddressSearchAdapterItem>();
	}
	
	public GISQLRequest(String text, String mode, String path, ArrayList<AddressSearchAdapterItem> array) 
	{
		// TODO Auto-generated constructor stub
		m_text = text;
		m_mode = mode;
		m_path = path;
		m_array = array;
	}

}
