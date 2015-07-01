package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;

import ru.tcgeo.gilib.*;
import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.GIStyle;

public abstract class GIRenderer
{
	public abstract void RenderImage (GILayer layer, ru.tcgeo.gilib.GIBounds area, int opacity, Bitmap bitmap, double scale);
	public abstract void RenderText (GILayer layer, ru.tcgeo.gilib.GIBounds area, Bitmap bitmap, double scale);
	public abstract void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor, double scale);
	public abstract void AddStyle (GIStyle style);
	public abstract int getType(GILayer layer);
}
