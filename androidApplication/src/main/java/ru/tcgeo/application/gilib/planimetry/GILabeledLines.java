package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import ru.tcgeo.gilib.planimetry.Edge;
import ru.tcgeo.gilib.planimetry.GIGeometryLine;
import ru.tcgeo.gilib.planimetry.GIGeometryPolygon;
import ru.tcgeo.gilib.planimetry.GILabeledLayer;
import ru.tcgeo.gilib.planimetry.GIQuadtree;
import ru.tcgeo.gilib.planimetry.GIShape;

/**
* класс слоя надписей вдоль (не)замкнутых линий*/
public class GILabeledLines extends GILabeledLayer
{
	//public ArrayList<Line> m_shapes;
	float m_textHeight;
	Paint m_paint;
	Canvas m_canvas;
	RectF m_bounds;
	ArrayList<GIGeometryLine> m_textpaths;
	
	public GILabeledLines(Canvas canvas) 
	{
		super();
		m_canvas = canvas;
		m_textpaths = new ArrayList<GIGeometryLine>();
		//m_shapes = new ArrayList<Line>();
	}
	
	//------------------------------------------------------------------------------------------------------------
	/*public void FoundConcurrents()
	{
		GIQuatroTree tree = new GIQuatroTree(m_bounds, 1, 1);
		tree.setShapes(m_shapes);
		tree.Sort();
		int shape_counter = 0;
		
		while(shape_counter < m_shapes.size())
		{
			GIGeometryLine current_line = (GIGeometryLine) m_shapes.get(shape_counter);
			int code = tree.getCode(current_line);
			ArrayList<GIGeometryObject> ListOfAll = tree.getDependies(code);
			for(int i = 0; i < ListOfAll.size(); i++)
			{
				GIGeometryLine compare_line = (GIGeometryLine)ListOfAll.get(i);
				current_line.ResolveConcurrent(compare_line);
			}

		}
	}*/
	
	//------------------------------------------------------------------------------------------------------------
	public boolean FoundDependies()
	{
		GIQuadtree tree = new GIQuadtree( m_bounds);
		tree.setEdges(m_shapes);
		tree.Sort();
		int compations = 0;
		int shape_counter = 0;
		int total_edges = 0;
		
		while(shape_counter < m_shapes.size())
		{
			GIGeometryLine current_line = (GIGeometryLine) m_shapes.get(shape_counter);
			GIGeometryLine saved_line = current_line.clone();

			int edge_counter = 0; 
			total_edges =  total_edges + current_line.m_edges.size();
			while(edge_counter < current_line.m_edges.size())
			{
				Edge current_edge = current_line.m_edges.get(edge_counter);
				 ArrayList<Edge> excluded_parts = new ArrayList<Edge>();
				 Point code = tree.MortonCode2D(current_edge);
				ArrayList<Edge> ListOfAll = tree.getDependedEdges(code, current_edge.m_ID);
				compations = compations + ListOfAll.size();
				//перебираем все Edge
				for(int c = 0; c < ListOfAll.size(); c++)
				{
					if(Thread.currentThread().isInterrupted())
					{
						//Log.d("LogsThreads", "Thread " + Thread.currentThread().getId() + "Redraw canceled at " + i + " of " + m_list.size());
						return false;
					}
					Edge compare = ListOfAll.get(c);
					//возможно пересечение областей
					GIGeometryPolygon contur = compare.GetOffsetGeomerty(m_textHeight/2);
					Edge to_exclude = current_edge.ExcludingEdgeByGeometry(contur.m_points, m_textHeight/2);
					if(to_exclude != null)
					{
						excluded_parts.add(to_exclude);
					}
				}
				ArrayList<Edge> newpath = current_edge.ExcludeAll(excluded_parts);
				current_line.m_edges.addAll(edge_counter, newpath);
				current_line.m_edges.remove(current_edge);
				if(newpath.size() > 0)
				{
					edge_counter = edge_counter + newpath.size();
				}
			}
			
			//TODO debug
			
			boolean to_debug = current_line.CouldDrawn(m_paint);
			
			if(to_debug)
			{
				m_textpaths.add(current_line);
				m_shapes.remove(current_line);
			}
			else
			{
				m_shapes.set(shape_counter, saved_line);
				shape_counter++;
			}
		}
		return m_shapes.size() == 0;
	}

	
	/**
	 *  основная функция слоя
	 */
	@Override
	public void FoundCandidates(RectF bounds, ArrayList<GIShape> used_space, Paint paint_text)
	{
		m_textHeight = paint_text.getTextSize();
		m_paint = paint_text;
		m_bounds = new RectF(bounds);
		ArrayList<GIShape> res = new ArrayList<GIShape>();
		GIQuadtree tree = new GIQuadtree(bounds);
		tree.setShapes(used_space);
		tree.Sort();
		for(GIShape line: m_shapes)
		{
			if(Thread.currentThread().isInterrupted())
			{
				//Log.d("LogsThreads", "Thread " + Thread.currentThread().getId() + "Redraw canceled at " + i + " of " + m_list.size());
				return;
			}
			if(((GIGeometryLine)line).FoundCandidates(bounds, tree, paint_text))
			{
				res.add(line);
			}
		}
		m_shapes = res;
		//TODO debug
		FoundDependies();
		
		/*int attempts = 2;
		while(attempts >=0 && !FoundDependies())
		{
			attempts--;
		}*/
		

	}

	public void Draw(Canvas canvas, Paint paint)
	{
		for(GIShape shape: m_textpaths)
		{
			GIGeometryLine line = (GIGeometryLine)shape;
			//line.DrawGeometry(canvas);
			//line.DrawRects(canvas, paint);
			//line.DrawBoundaries(canvas);
			line.Draw(canvas, paint);
		}
	}
	
	public void DrawGeometry(Canvas canvas)
	{
		for(GIShape shape: m_shapes)
		{
			GIGeometryLine line = (GIGeometryLine)shape;
			line.DrawGeometry(canvas);
		}
	}
	
	public void DrawBoundaries(Canvas canvas)
	{
		for(GIShape shape: m_shapes)
		{
			GIGeometryLine line = (GIGeometryLine)shape;
			line.DrawBoundaries(canvas);
		}
	}
	public void  DrawRects(Canvas canvas, Paint paint)
	{
		for(GIShape shape: m_shapes)
		{
			GIGeometryLine line = (GIGeometryLine)shape;
			line.DrawRects(canvas, paint);
		}
	}
	public void  DrawRaw(Canvas canvas, Paint paint)
	{
		for(GIShape shape: m_shapes)
		{
			GIGeometryLine line = (GIGeometryLine)shape;
			line.DrawGeometry(canvas);
			line.DrawBoundaries(canvas, paint);
			line.DrawRects(canvas, paint);
		}
	}
}
