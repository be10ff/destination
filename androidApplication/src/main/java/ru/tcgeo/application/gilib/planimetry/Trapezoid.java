//package ru.tcgeo.application.gilib.planimetry;
//
//import android.graphics.PointF;
//import android.graphics.RectF;
//
//public class Trapezoid {
//
//	public Edge m_top_edge;
//	public Edge m_bottom_edge;
//
//	public Trapezoid(Edge top, Edge bottom)
//	{
//		m_top_edge = top;
//		m_bottom_edge = bottom;
//	}
//	public String toString()
//	{
//		return "(" + m_top_edge.m_start.x + "..." + m_top_edge.m_end.x + ", " + m_top_edge.m_end.y + ") (" + m_bottom_edge.m_start.x + "..." + m_bottom_edge.m_end.x + ", " + m_bottom_edge.m_end.y + ")";
//	}
//
//	public float getMiddlelength()
//	{
//		return (Math.abs(m_top_edge.m_start.x - m_top_edge.m_end.x) + Math.abs(m_bottom_edge.m_start.x - m_bottom_edge.m_end.x))/2;
//	}
//
//	public float getTop()
//	{
//		return m_top_edge.m_start.y;
//	}
//	public float getBottom()
//	{
//		return m_bottom_edge.m_start.y;
//	}
//	public float getLeftBound()
//	{
//		//if(!m_top_edge.isInOrder(m_bottom_edge))
//		return Math.max(m_top_edge.m_start.x, m_bottom_edge.m_start.x);
//	}
//	public float getRightBound()
//	{
//		return Math.min(m_top_edge.m_end.x, m_bottom_edge.m_end.x);
//	}
//	public RectF getRect()
//	{
//		return new RectF(getLeftBound(), getTop(), getRightBound(), getBottom());
//	}
//	public float getHeight()
//	{
//		return getBottom() - getTop();
//	}
//	public float getWidth()
//	{
//		return getRightBound() - getLeftBound();
//	}
//	public static Trapezoid SplitTrapezoidAtHeights(Trapezoid segment, float top, float bottom)
//	{
//		if(top < segment.getTop() - Vertex.delta|| top > segment.getBottom() + Vertex.delta)
//		{
//			return null;
//		}
//		if(bottom < segment.getTop() - Vertex.delta|| bottom > segment.getBottom() + Vertex.delta)
//		{
//			return null;
//		}
//
//		float _top = Math.min(top, bottom);
//		float _bottom = Math.max(top, bottom);
//		float h1 = _top - segment.getTop();
//		float h2 = _bottom - segment.getTop();
//		float _top_left = (h1/segment.getHeight())*(segment.m_bottom_edge.m_start.x - segment.m_top_edge.m_start.x) + segment.m_top_edge.m_start.x;
//		Vertex v_top_left = new Vertex(new PointF(_top_left, _top));
//		float _bottom_left = (h2/segment.getHeight())*(segment.m_bottom_edge.m_start.x - segment.m_top_edge.m_start.x) + segment.m_top_edge.m_start.x;
//		Vertex v_bottom_left = new Vertex(new PointF(_bottom_left, _bottom));
//		float _top_right = (h1/segment.getHeight())*(segment.m_bottom_edge.m_end.x - segment.m_top_edge.m_end.x) + segment.m_top_edge.m_end.x;
//		Vertex v_top_right = new Vertex(new PointF(_top_right, _top));
//		float _bottom_right = (h2/segment.getHeight())*(segment.m_bottom_edge.m_end.x - segment.m_top_edge.m_end.x) + segment.m_top_edge.m_end.x;
//		Vertex v_bottom_right = new Vertex(new PointF(_bottom_right, _bottom));
//		return new Trapezoid(new Edge(v_top_left, v_top_right), new Edge(v_bottom_left, v_bottom_right));
//	}
//
//}
