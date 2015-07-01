package ru.tcgeo.application.gilib;

public class GITuple
{
	GITuple (GILayer layer_, boolean visible_, GIScaleRange scale_range_)
	{
		layer = layer_;
		visible = visible_;
		scale_range = scale_range_;
		
	}
	public GILayer layer;
	public boolean visible;
	public GIScaleRange scale_range;
}