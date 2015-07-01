package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import ru.tcgeo.gilib.planimetry.GIShape;

/**
 * абстрактный класс слоя надписей */
public abstract class GILabeledLayer {

	public ArrayList<GIShape> m_shapes;
	
	public GILabeledLayer()
	{
		m_shapes = new ArrayList<GIShape>();
	}

	public void add (GIShape shape)
	{
		m_shapes.add(shape);
	}
	public void Draw(Canvas canvas, Paint paint)
	{
		for(GIShape shape: m_shapes)
		{
			shape.DrawGeometry(canvas);
			shape.DrawRects(canvas, paint);
			shape.DrawBoundaries(canvas);
		}
	}
	
	public void DrawGeometry(Canvas canvas)
	{
		for(GIShape shape: m_shapes)
		{
			shape.DrawGeometry(canvas);
		}
	}
	
	public void DrawBoundaries(Canvas canvas)
	{
		for(GIShape shape: m_shapes)
		{
			shape.DrawBoundaries(canvas);
		}
	}
	public abstract void FoundCandidates(RectF bounds, ArrayList<GIShape> used_space, Paint paint_text);
}
