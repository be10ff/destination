package ru.tcgeo.application.gilib;

public abstract class GIFilter
{
	abstract public boolean Check (); 
	
	public static GIFilter All ()
	{
		return new GIFilter.GIFilterAll();
	}

	public static GIFilter None ()
	{
		return new GIFilter.GIFilterNone();
	}
	
	private static class GIFilterAll extends GIFilter
	{
		@Override
	    public boolean Check ()
	    {
	        return true;
	    } 
	}
	
	private static class GIFilterNone extends GIFilter
	{
		@Override
	    public boolean Check ()
	    {
	        return false;
	    } 
	}
}
