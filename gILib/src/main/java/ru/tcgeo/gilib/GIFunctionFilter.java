package ru.tcgeo.gilib;

public abstract class GIFunctionFilter extends GIFilter
{
	GIEncoding m_encoding;
	int m_semantic;
	
	public GIFunctionFilter(int semantic)
	{
		m_semantic = semantic;
	}
	
	// This function checks 'active' feature
	// TODO: Find better solution
	@Override
	final public native boolean Check ();
	
	public abstract boolean TestValue (String value);
}
