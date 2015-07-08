//package ru.tcgeo.application.gilib.planimetry;
//import java.util.ArrayList;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Point;
//import android.graphics.PointF;
//import android.graphics.RectF;
//import android.graphics.Paint.Style;
//
//
///**
// * Дерево квадрантов на Z-кривой Мортона
// * @author artem
// *
// */
//public class GIQuadtree
//{
//	public static int m_max_level;
//
//	private ArrayList<GIGeometryObject> m_shapes;
//	public int m_lvl;
//	public int m_code_L;
//	public int m_code_H;
//	public static RectF m_area;
//	public ArrayList<GIQuadtree> m_brunches;
//	public static int m_dim;
//
////	public class Point
////	{
////		Point()
////		{
////			this.x = 0;
////			this.y = 0;
////		}
////		Point(int x, int y)
////		{
////			this.x = x;
////			this.y = y;
////		}
////		public int x;
////		public int y;
////	}
//
//
//	public GIQuadtree(RectF area)
//	{
//		m_lvl = 0;
//		m_max_level = 4;
//		m_area = area;
//		m_dim =  (int) Math.pow(2, m_max_level);
//		//TODO
//		m_code_L = MortonCode2D(0, 0);
//		//TODO
//		m_code_H = MortonCode2D(m_dim - 1, m_dim - 1);
//		m_brunches = new ArrayList<GIQuadtree>();
//		m_shapes = new ArrayList<GIGeometryObject>();
//	}
//
//	public GIQuadtree(RectF area, int lvl)
//	{
//		m_lvl = 0;
//		m_max_level = lvl;
//		m_area = area;
//		m_dim =  (int) Math.pow(2, m_max_level);
//		m_code_L = MortonCode2D(0, 0);
//		m_code_H = MortonCode2D(m_dim - 1, m_dim - 1);
//		m_brunches = new ArrayList<GIQuadtree>();
//		m_shapes = new ArrayList<GIGeometryObject>();
//	}
//
//	protected GIQuadtree(int lvl, int code_l, int code_h)
//	{
//		m_lvl = lvl;
//		m_code_L = code_l;
//		m_code_H = code_h;
//		m_brunches = new ArrayList<GIQuadtree>();
//		m_shapes = new ArrayList<GIGeometryObject>();
//	}
//
//	/**
//	 * Получение "через_строчного" бинарного представления одной координаты
//	 * @param x
//	 * @return
//	 */
//	private int SeparateBy1(int x)
//	{
//		x &= 0x0000ffff;                  // x = ---- ---- ---- ---- fedc ba98 7654 3210
//	    x = (x ^ (x <<  8)) & 0x00ff00ff; // x = ---- ---- fedc ba98 ---- ---- 7654 3210
//	    x = (x ^ (x <<  4)) & 0x0f0f0f0f; // x = ---- fedc ---- ba98 ---- 7654 ---- 3210
//	    x = (x ^ (x <<  2)) & 0x33333333; // x = --fe --dc --ba --98 --76 --54 --32 --10
//	    x = (x ^ (x <<  1)) & 0x55555555; // x = -f-e -d-c -b-a -9-8 -7-6 -5-4 -3-2 -1-0
//	    return x;
//	}
//
//
//	/***
//	 * Получение кода Мортона для двумерной точки
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	private int MortonCode2D(int x, int y)
//	{
//		return SeparateBy1(x)|(SeparateBy1(y) << 1);
//	}
//
//	/***
//	 * Получение кода Мортона для двумерной точки
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	private int MortonCode2D(float x, float y)
//	{
//
//		int nX = (int) Math.round((x/m_area.width())*m_dim);
//		int nY = (int) Math.round((y/m_area.height())*m_dim);
//		return SeparateBy1(nX)|(SeparateBy1(nY) << 1);
//	}
//	/***
//	 * Получение кода Мортона для двумерной точки
//	 * @param point
//	 * @return
//	 */
//	private int MortonCode2D(PointF point)
//	{
//		//return SeparateBy1(point.x)|(SeparateBy1(point.y) << 1);
//		return MortonCode2D(point.x, point.y);
//	}
//	public Point MortonCode2D(GIGeometryObject obj)
//	{
//		RectF bounds = obj.getBounds();
//
////		PointF start = new PointF(bounds.left/* - m_area.left*/, bounds.top/* - m_area.top*/);
////		PointF end = new PointF(bounds.right/* - m_area.left*/, bounds.bottom/* - m_area.top*/);
//
////		PointF start = new PointF(bounds.left - m_area.left, bounds.top - m_area.top);
////		PointF end = new PointF(bounds.right - m_area.left, bounds.bottom - m_area.top);
//		//Original
//		PointF start = new PointF(bounds.left, bounds.top);
//		PointF end = new PointF(bounds.right, bounds.bottom);
//		int code_l = MortonCode2D(start);
//		int code_h = MortonCode2D(end);
//		Point res = new Point(code_l, code_h);
//		obj.setMortonCodes(res);
//		return res;
//	}
//
//	/**
//	 * Получение координаты из ее "черезстрочного" представления
//	 * @param x
//	 * @return
//	 */
//	private int CompactBy1(int x)
//	{
//	    x &= 0x55555555;                  // x = -f-e -d-c -b-a -9-8 -7-6 -5-4 -3-2 -1-0
//	    x = (x ^ (x >>  1)) & 0x33333333; // x = --fe --dc --ba --98 --76 --54 --32 --10
//	    x = (x ^ (x >>  2)) & 0x0f0f0f0f; // x = ---- fedc ---- ba98 ---- 7654 ---- 3210
//	    x = (x ^ (x >>  4)) & 0x00ff00ff; // x = ---- ---- fedc ba98 ---- ---- 7654 3210
//	    x = (x ^ (x >>  8)) & 0x0000ffff; // x = ---- ---- ---- ---- fedc ba98 7654 3210
//	    return x;
//	}
//	/**
//	 * Получение координат по коду Мортона
//	 * @param c
//	 * @return
//	 */
//	private Point  MortonDecode2(int c)
//	{
//	    int x = CompactBy1(c);
//	    int y = CompactBy1(c >> 1);
//	    Point res = new Point(x, y);
//	    return res;
//	}
//
//	/**
//	 * Получение нормированных (0, 1) координат точки в области
//	 * @param view_rect - заданная в RectF область
//	 * @param point - координаты PointF
//	 * @return - нормированные координаты
//	 */
//	private Point Screen2Z(RectF view_rect, PointF point)
//	{
//		Point result = new Point();
//		result.x = (int) Math.round((point.x / view_rect.width())*Math.pow(2, m_max_level));
//		result.y = (int) Math.round((point.y / view_rect.height())*Math.pow(2, m_max_level));
//		return result;
//	}
//
//
//	private boolean IsInclude(RectF object)
//	{
//		int code_L = MortonCode2D((int)object.left, (int)object.top);
//		int code_H = MortonCode2D((int)object.right, (int)object.bottom);
//		return ((code_L >= m_code_L)&&(code_H <= m_code_H));
//	}
//
//
//	private boolean IsInclude(int code_L, int code_H)
//	{
//		return ((code_L >= m_code_L)&&(code_H <= m_code_H));
//	}
//
//	/**
//	 * построение квадродерева заданной вложености
//	 */
//	public int Sort()
//	{
//	    return Sort(false);
//	}
//	private int Sort(boolean full)
//	{
//
//		int res = 1;
//		if(m_lvl <= m_max_level)
//		{
//			int step = (m_code_H + 1 - m_code_L)/4;
//			ArrayList<GIQuadtree> brunches = new ArrayList<GIQuadtree>();
//			//m_brunches = new ArrayList<GIQuadtree>();
//			//RectF bounds = new RectF((int)m_area.left, (int)m_area.top, (int)(m_area.left + m_area.width()/2), (int)(m_area.top + m_area.height()/2));
//			//brunches.add(new GIQuadtree( m_lvl + 1, bounds));
//			brunches.add(new GIQuadtree( m_lvl + 1, m_code_L, m_code_L + step -1));
//			//Point control_l = MortonDecode2(brunches.get(0).m_code_L);
//			//Point control_h = MortonDecode2(brunches.get(0).m_code_H);
//			//bounds = new RectF((int)(m_area.left + m_area.width()/2), (int)m_area.top, (int)m_area.right, (int)(m_area.top + m_area.height()/2));
//			//brunches.add(new GIQuadtree(m_lvl + 1, bounds));
//			brunches.add(new GIQuadtree(m_lvl + 1,  m_code_L + step, m_code_L + step*2 -1));
//			//control_l = MortonDecode2(brunches.get(1).m_code_L);
//			//control_h = MortonDecode2(brunches.get(1).m_code_H);
//			//bounds = new RectF((int)m_area.left, (int)(m_area.top + m_area.height()/2), (int)(m_area.left + m_area.width()/2), (int)m_area.bottom);
//			brunches.add(new GIQuadtree( m_lvl + 1, m_code_L + step*2, m_code_L + step*3 -1));
//			//control_l = MortonDecode2(brunches.get(2).m_code_L);
//			//control_h = MortonDecode2(brunches.get(2).m_code_H);
//			//bounds = new RectF((int)(m_area.left + m_area.width()/2), (int)(m_area.top + m_area.height()/2),(int) m_area.right ,(int) m_area.bottom);
//			brunches.add(new GIQuadtree(m_lvl + 1, m_code_L + step*3, m_code_H));
//			//control_l = MortonDecode2(brunches.get(3).m_code_L);
//			//control_h = MortonDecode2(brunches.get(3).m_code_H);
//
//			//there are Points just like a pair (int, int)
//
//			for(int i = 0; i < 4; i++)
//			{
//				GIQuadtree branch = brunches.get(i);
//				int shape_counter = 0;
//				while(shape_counter < m_shapes.size())
//				{
//					GIGeometryObject shape = m_shapes.get(shape_counter);
//					if(branch.IsInclude(shape.getMortonCodes().x, shape.getMortonCodes().y))
//					{
//						branch.m_shapes.add(shape);
//						m_shapes.remove(shape);
//					}
//					else
//					{
//						shape_counter++;
//					}
//				}
//				if((branch.m_shapes.size() > 0)||(full))
//				{
//					m_brunches.add(branch);
//				}
//				if((branch.m_shapes.size() > 0)||(full))	//1
//				{
//					res = res + branch.Sort(full);
//				}
//				if(( m_shapes.size() == 0)&&(!full))
//				{
//					break;
//				}
//			}
//		}
//		return res;
//	}
//
//	/**
//	 * наполняет квадродерево отрезками  из массива фигур
//	 * @param shapes входной массив
//	 */
//	public void setEdges(ArrayList<GIShape> shapes)
//	{
//		for(int i = 0; i < shapes.size(); i++)
//		{
//			GIShape current = shapes.get(i);
//			if(current.m_edges == null)
//			{
//				current.MakeEdgesRing();
//			}
//			if(current.m_edges.size() == 0)
//			{
//				current.MakeEdgesRing();
//			}
//			for(int j = 0; j < current.m_edges.size(); j++)
//			{
//				current.m_edges.get(j).m_ID = current.m_objectID;
//				Edge res = current.m_edges.get(j).clone();
//				res.m_ID = current.m_objectID;
//				if(current.getMortonCodes() != null)
//				{
//					res.setMortonCodes(current.m_edges.get(j).getMortonCodes());
//				}
//				else
//				{
//					res.setMortonCodes(new Point(MortonCode2D(res.getBounds().left, res.getBounds().top), MortonCode2D(res.getBounds().right, res.getBounds().bottom)));
//				}
//				m_shapes.add(res);
//			}
//		}
//	}
//	/**
//	 * наполняет квадродерево фигурами из массива
//	 * @param shapes входной массив
//	 */
//	public void setShapes(ArrayList<GIShape> shapes)
//	{
//		for(int i = 0; i < shapes.size(); i++)
//		{
//			shapes.get(i).setMortonCodes(MortonCode2D(shapes.get(i)));
//			m_shapes.add(shapes.get(i));
//		}
//	}
//	/**
//	 * @param id клонов для исключения
//	 * @return возвращает список всех объектов заданной ветви, кроме клонов с указанным id
//	 */
//	/*public ArrayList<Edge> getEveryEgdesIn(int id)
//	{
//		ArrayList <Edge> result = new ArrayList<Edge>();
//		for(int i = 0; i < m_shapes.size(); i++)
//		{
//			Edge res = (Edge)m_shapes.get(i);
//			if(res != null)
//			{
//				if(res.m_ID != id)
//				result.add(res);
//			}
//		}
//		for(int i = 0; i < m_brunches.size(); i++)
//		{
//			result.addAll(m_brunches.get(i).getEveryEgdesIn(id));
//		}
//		return result;
//	}*/
//
//	/**
//	 * @return возвращает список всех объектов заданной ветви
//	 */
//	/*public ArrayList<GIShape> getEveryShapeIn()
//	{
//		ArrayList <GIShape> result = new ArrayList<GIShape>();
//		for(int i = 0; i < m_shapes.size(); i++)
//		{
//			GIShape res = (GIShape)m_shapes.get(i);
//			if(res != null)
//			{
//				result.add(res);
//			}
//		}
//		for(int i = 0; i < m_brunches.size(); i++)
//		{
//			result.addAll(m_brunches.get(i).getEveryShapeIn());
//		}
//		return result;
//	}*/
//
///**
// * список всех влияющих объектов для заданного кода
// * @param code заданного кода
// * @return список всех влияющих объектов для заданного кода
// */
//	public ArrayList<GIGeometryObject> getDependies(Point code)
//	{
//		ArrayList <GIGeometryObject> result = new ArrayList<GIGeometryObject>();
//		//TODO: no
//		//объект не попадает в квадрант
//		if((code.x >= m_code_H)||(code.y <= m_code_L))
//		{
//			return result;
//		}
//		/*if((code.x <= m_code_L)&&(code.y >= m_code_H))
//		{
//			//объект этого уровня. передаем все этого и потомков
//			result.addAll(getEveryShapeIn());
//		}*/
//		//объект полностью внутри квадранта
//		if(!(code.x > m_code_H || code.y < m_code_L ))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				GIShape res = (GIShape)m_shapes.get(i);
//				if(res != null)
//				{
//					if(!(code.x > res.m_morton_codes.y||code.y < res.m_morton_codes.x))
//					{
//						result.add(res);
//					}
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependies(code));
//			}
//			return result;
//		}
//		/*
//		//объект полностью внутри квадранта
//		if((code.x >= m_code_L)&&(code.y <= m_code_H))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				GIShape res = (GIShape)m_shapes.get(i);
//				if(res != null)
//				{
//					result.add(res);
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependies(code));
//			}
//			return result;
//		}
//		//квадрант меньше и внутри bounds объекта
//
//		//объект попадает в квадрант началом
//		if((code.x >= m_code_L)&&(code.y >= m_code_H))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				GIShape res = (GIShape)m_shapes.get(i);
//				if(res != null)
//				{
//					result.add(res);
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependies(code));
//			}
//			return result;
//		}
//		//объект попадает в квадрант концом
//		if((code.x <= m_code_L)&&(code.y <= m_code_H))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				GIShape res = (GIShape)m_shapes.get(i);
//				if(res != null)
//				{
//					result.add(res);
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependies(code));
//			}
//			return result;
//		}	*/
//		return result;
//	}
//
//	/**
//	 * список всех влияющих объектов для заданного кода кроме его клонов
//	 * @param code код объекта
//	 * @param id оодинаковый для всех клонов одного объекта
//	 * @return всех влияющих объектов для заданного кода кроме его клонов
//	 */
//	public ArrayList<Edge> getDependedEdges(Point code, int id)
//	{
//
//		ArrayList <Edge> result = new ArrayList<Edge>();
//		//TODO: no
//		//объект не попадает в квадрант
//		if((code.x >= m_code_H)||(code.y <= m_code_L))
//		{
//			return result;
//		}
//		//квадрант меньше и внутри bounds объекта
//		/*if((code.x <= m_code_L)&&(code.y >= m_code_H))
//		{
//			//объект этого уровня. передаем все этого и потомков
//			result.addAll(getEveryEgdesIn(id));
//		}*/
//
//		//объект полностью внутри квадранта
//		//if((code.x >= m_code_L)&&(code.y <= m_code_H))
//
//		//объект полностью внутри квадранта             //объект попадает в квадрант началом          //объект попадает в квадрант концом
//		//if(((code.x >= m_code_L)&&(code.y <= m_code_H))||((code.x >= m_code_L)&&(code.y >= m_code_H))||((code.x <= m_code_L)&&(code.y <= m_code_H)))
//		if(!(code.x > m_code_H||code.y < m_code_L))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				Edge res = (Edge)m_shapes.get(i);
//				if(res != null)
//				{
//					if(res.m_ID != id)
//					{
//						//if(((code.x >= res.m_morton_codes.x)&&(code.y <= res.m_morton_codes.y))||((code.x >= res.m_morton_codes.x)&&(code.y >= res.m_morton_codes.y))||((code.x <= res.m_morton_codes.x)&&(code.y <= res.m_morton_codes.y)))
//						if(!(code.x > res.m_morton_codes.y||code.y < res.m_morton_codes.x))
//						{
//							result.add(res);
//						}
//					}
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependedEdges(code, id));
//			}
//			return result;
//		}
//		//объект попадает в квадрант началом
//		/*if((code.x >= m_code_L)&&(code.y >= m_code_H))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				Edge res = (Edge)m_shapes.get(i);
//				if(res != null)
//				{
//					if(res.m_ID != id)
//					{
//						result.add(res);
//					}
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependedEdges(code, id));
//			}
//			return result;
//		}
//		//объект попадает в квадрант концом
//		if((code.x <= m_code_L)&&(code.y <= m_code_H))
//		{
//			//все объекты этого уровня
//			for(int i = 0; i < m_shapes.size(); i++)
//			{
//				Edge res = (Edge)m_shapes.get(i);;
//				if(res != null)
//				{
//					if(res.m_ID != id)
//					{
//						result.add(res);
//					}
//				}
//			}
//			//и передаем потомкам
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				result.addAll(brunch.getDependedEdges(code, id));
//			}
//			return result;
//		}	*/
//		return result;
//	}
//	public void DrawRects(Canvas canvas)
//	{
//		Paint paint = new Paint();
//        paint.setColor(Color.GRAY);
//        paint.setAlpha(64);
//        paint.setStyle(Style.FILL_AND_STROKE);
//        if(m_shapes.size() > 0)
//        {
//        	Point lt = MortonDecode2(m_code_L);
//        	Point rb = MortonDecode2(m_code_H);
//    		float left =  ((((float)lt.x)/m_dim)*m_area.width());
//    		float top =  ((((float)lt.y)/m_dim)*m_area.height());
//       		float right =  ((((float)rb.x)/m_dim)*m_area.width());
//    		float bottom =  ((((float)rb.y)/m_dim)*m_area.height());
//    		canvas.drawRect(left, top, right, bottom, paint);
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				brunch.DrawRects(canvas);
//			}
//        }
//        else
//        {
//			for(int i = 0; i < m_brunches.size(); i++)
//			{
//				GIQuadtree brunch = m_brunches.get(i);
//				brunch.DrawRects(canvas);
//			}
//        }
//
//	}
//}
