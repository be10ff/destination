package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.RectF;

import ru.tcgeo.gilib.planimetry.GILabeledLayer;
import ru.tcgeo.gilib.planimetry.GIShape;

/**
 * класс слоя надписей внутри замкнутых полигонов*/
public class GILabeledPolygons extends GILabeledLayer
{
	//public ArrayList<LabelGeometry> m_labels;
	
	public GILabeledPolygons()
	{
		super();
		//m_labels = new ArrayList<LabelGeometry>();
	}

	@Override
	public void FoundCandidates(RectF bounds, ArrayList<GIShape> used_space, Paint paint_text)
	{
		
		
	}

	/*public void add (LabelGeometry label)
	{
		m_labels.add(label);
	}*/
}