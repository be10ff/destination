//package ru.tcgeo.application.gilib.planimetry;
//
//import java.util.ArrayList;
//
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PointF;
//
//
//public class GITextPath extends ArrayList<Edge>
//{
//
//	String m_text;
//
//	public GITextPath()
//	{
//		super();
//	}
//
//	public GITextPath(ArrayList<Edge> path)
//	{
//		super();
//		for(int i = 0; i < path.size(); i++)
//		{
//			add(path.get(i));
//		}
//	}
//
//	public GITextPath(ArrayList<PointF> points, String text, boolean bool)
//	{
//		super();
//		m_text = text;
//		for(int i = 0; i < points.size() -1; i++)
//		{
//			add(new Edge(points.get(i), points.get(i+1)));
//		}
//	}
//
//	public GITextPath(ArrayList<Edge> path, String text)
//	{
//		super();
//		m_text = text;
//		for(int i = 0; i < path.size(); i++)
//		{
//			add(path.get(i));
//		}
//	}
//
//	public float Lenght()
//	{
//		if(size() < 1)
//		{
//			return 0;
//		}
//		float path_part_lenght = 0;
//		for(int j = 0; j < size(); j++)
//		{
//			path_part_lenght = path_part_lenght + get(j).Lenght();
//		}
//		return path_part_lenght;
//	}
//
//	public void Swap()
//	{
//		//int last = size() - 1;
//		//int amount = size();
//		for(int i = 0; i < size(); i++)
//		{
//			Edge current = get(0);
//			Edge edge = current.clone();
//			edge.Swap();
//			add(edge);
//			remove(current);
//		}
//	}
//	public ArrayList<PointF> GetPoints()
//	{
//		ArrayList<PointF> result = new ArrayList<PointF>();
//		if(size() > 0)
//		{
//			result.add(get(0).m_start);
//			for(int i = 0; i < size(); i++)
//			{
//				result.add(get(i).m_end);
//			}
//		}
//		return result;
//	}
//
//	private boolean IsOrientationCorrectly()
//	{
//
//		return get(0).m_start.x <= get(size() -1).m_end.x;
//	}
//
//
//	public void Draw(Canvas canvas, Paint paint)
//	{
//		if(m_text == null)
//		{
//			return;
//		}
//		if(m_text.length() == 0)
//		{
//			return;
//		}
//		if(m_text.equalsIgnoreCase(""))
//		{
//			return;
//		}
//		//TODO debug
//		/*if(!IsOrientationCorrectly())
//		{
//			Swap();
//		}*/
//		//new variant
//		/*if(size() > 0)
//		{
//			Path path = new Path();
//			Edge edge = get(0);
//			path.moveTo(get(0).m_start.x, get(0).m_start.y);
//			for(int k = 0; k < size(); k++)
//			{
//				edge = get(k);
//				path.lineTo(get(k).m_end.x, get(k).m_end.y);
//			}
//			//float offset = (Lenght() - LabelText.getTextRect(m_text, paint).width())/2;
//			canvas.drawTextOnPath(m_text, path, 0, paint.getTextSize()/4, paint);
//
//		}*/
//
//
//		//working good variant
//
//		ArrayList<PointF> points = GetPoints();
//		if(points.size() > 1)
//		{
//			Path path = new Path();
//			if(IsOrientationCorrectly())
//			{
//				path.moveTo(points.get(0).x, points.get(0).y);
//				for(int i = 1; i < points.size();i++)
//				{
//					path.lineTo(points.get(i).x, points.get(i).y);
//				}
//			}
//			else
//			{
//				path.moveTo(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
//				for(int i = points.size()-2; i >= 0; i--)
//				{
//					path.lineTo(points.get(i).x, points.get(i).y);
//				}
//
//			}
//			//float offset = (Lenght() - LabelText.getTextRect(m_text, paint).width())/2;
//			//canvas.drawTextOnPath(m_text, path, offset, paint.getTextSize()/4, paint);
//			canvas.drawTextOnPath(m_text, path, 0, paint.getTextSize()/4, paint);
//		}
//
//
//	}
//
//}
