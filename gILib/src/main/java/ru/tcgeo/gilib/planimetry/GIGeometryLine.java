package ru.tcgeo.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * класс ломанной линии на плоскости
 */
public class GIGeometryLine extends GIShape {
	
	public String[] SplitText(String text)
	{
		String seq = new String("\\ ");
		String string = new String(text);
		String[] result =string.split(seq);
		return result;
	}
	protected Boolean m_unique;
	public ArrayList<GITextPath> m_paths;
	public float m_textHeight;
	public GIGeometryLine() 
	{
		super();
		m_paths = new ArrayList<GITextPath>();
	}
	public GIGeometryLine(String text, ArrayList<PointF> points) 
	{
		super(text);
		for(int i = 0; i < points.size(); i++)
		{
			add(points.get(i));
		}
		m_paths = new ArrayList<GITextPath>();
		m_unique = null;
	}
	
	public void setUnique(boolean unique)
	{
		m_unique = unique;
	}
	public boolean getUnique()
	{
		if(m_unique != null)
		{
			return m_unique.booleanValue();
		}
		return IsUnique();
	}
	
	private boolean IsUnique()
	{
		for(int i = 0; i < m_edges.size(); i++)
		{
			if(!m_edges.get(i).m_unique)
			{
				m_unique = false;
				return false;
			}
		}
		return true;
	}
	
	public boolean ResolveConcurrent(GIGeometryLine line)
	{
		if(!m_labeltext.equals(line.m_labeltext))
		{
			return false;
		}
		boolean res = false;
		for(int i = m_edges.size() - 1; i >= 0; i++)
		{
			Edge current = m_edges.get(i);
			for(int j = 0; j < line.m_edges.size(); j++)
			{
				Edge compare = line.m_edges.get(j);
				if(current.equal(compare))
				{
					res = true;
					if(getUnique())
					{
						compare.m_unique = false;
						line.setUnique(false);
					}
					else
					{
						current.m_unique = false;
						setUnique(false);
					}
				}
			}
		}
		
		return res;
	}
	public GIGeometryLine(String text) 
	{
		super(text);
		m_paths = new ArrayList<GITextPath>();
	}
	public TYPE getType()
	{
		return GIShape.TYPE.line;
	}
	public GIGeometryLine clone()
	{
		GIGeometryLine result = new GIGeometryLine(m_labeltext);
		for(int i = 0; i < m_points.size(); i++)
		{
			result.add(m_points.get(i).clone());
		}
		result.m_edges = new ArrayList<Edge>();
		for(int i = 0; i < m_edges.size(); i++)
		{
			result.m_edges.add( m_edges.get(i).clone());
		}

		return result;
	}
	public void CorrectOrientation()
	{
		
		if(m_points.size() > 0)
		{
			if(!(m_points.get(0).x <= m_points.get(m_points.size() -1).x))
			{
				ArrayList<Vertex> res = new ArrayList<Vertex>();
				for(int i = m_points.size() - 1; i >= 0; i--)
				{
					res.add(m_points.get(i));
				}
				m_points = res;
			}
		}
	}
	
	/**
	 * Упрощение полигональной цепи по Рейманну-Виткаму
	 * для незамкнутой полигональной цепи
	 */
	public void generalize(float factor)
	{
		ArrayList<Vertex> points = new ArrayList<Vertex>();
		if(m_points.size() < 3)	
		{
			return;
		}
		points.add(m_points.get(0));
		points.add(m_points.get(1));
		int count = 2;
		while(count < m_points.size())
		{
			Vertex a =  points.get(points.size() - 2);
			Vertex b =  points.get(points.size() - 1);
			boolean in_sigma = true;
			Vertex c;
			Vertex next_b = b;
			do
			{
				c =  m_points.get(count);
				double sigma = GIGeometryPolygon.epsilon(a, b, c);
				if(sigma > factor)
				{
					in_sigma = false;
				}
				else
				{
					next_b = c;
					count++;
				}
			}
			while((count < m_points.size()) && in_sigma); // + 1
			points.set(points.size() - 1, next_b);
			if(!in_sigma)
			{
				points.add(c);
			}
			count++;
		}
		m_points = points;
	}
	
	public boolean CouldDrawn(Paint paint)
	{
		String[] substrings = SplitText(m_labeltext);
		if(m_edges == null)
		{
			return true;
		}
		//TODO MakeTextPaths
		MakeTextPaths();
		if(m_paths == null)
		{
			return true;
		}
		int subs_count = 0;
		for(int i = 0; i < m_paths.size() && subs_count < substrings.length; i++)
		{
			GITextPath current_path = m_paths.get(i);
			float path_part_lenght = current_path.Lenght();
			String curr_str = substrings[subs_count];
			while((subs_count < substrings.length)&&(path_part_lenght > (LabelText.getTextRect(curr_str, paint).width()) ))
			{
				current_path.m_text = curr_str;
				if(subs_count + 1 < substrings.length)
				{
					curr_str = curr_str + " " + substrings[subs_count+1];
				}
				subs_count++;
			}
		}
		return subs_count == substrings.length;
	}
	public void Draw(Canvas canvas, Paint paint)
	{
		/*if(m_paths == null)
		{
			MakeTextPaths();
			//return;
		}*/
		for(GITextPath path : m_paths)
		{
			path.Draw(canvas, paint);
		}
	}
	/**
	 *  производит сечение GIGeometryLine ограничивающим rect
	 *  по Коэну-Сазерленду
	 *  добавляет точки пересечения и оставляет только лежащие внутри точки 
	 */

	public boolean IntersectByRect_kohen(RectF rect) 
	{
		m_bounds = rect;
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			Vertex current = new Vertex(m_points.get(i));
			Vertex next = new Vertex(m_points.get(i+1));
			//exclude Edge. invisible.
			if((current.getCode(rect) & next.getCode(rect)) != 0)
			{
				continue;
			}
			//include Edge. visible.
			if((current.getCode(rect) | next.getCode(rect)) == 0)
			{
				if(result.size() == 0 || !current.equals(result.get(result.size()-1)))
				{
					result.add(current);
				}
				if(result.size() == 0 || !next.equals(result.get(result.size()-1)))
				{
					result.add(next);
				}
				continue;
			}
			Edge clipped = Edge.Clipping(new Edge(current, next), rect);
			if(clipped != null)
			{
				//visible part
				if(result.size() == 0 || !clipped.m_start.equals(result.get(result.size()-1)))
				{
					result.add(clipped.m_start);
				}
				if(result.size() == 0 || !clipped.m_end.equals(result.get(result.size()-1)))
				{
					result.add(clipped.m_end);
				}
			}
		}
		m_points = result;
		return true;
	}
	/**
	 *  производит сечение GIGeometryLine ограничивающим rect
	 *  по Кириусу-Беку
	 */
	@Override
	public boolean IntersectByRect(RectF rect) 
	{
		ArrayList<Vertex> geom = new ArrayList<Vertex>();
		geom.add(new Vertex(rect.left, rect.top));
		geom.add(new Vertex(rect.right, rect.top));
		geom.add(new Vertex(rect.right, rect.bottom));
		geom.add(new Vertex(rect.left, rect.bottom));
		geom.add(new Vertex(rect.left, rect.top));
		
		ArrayList<Edge> res =  SectionByGenericFigure(geom, 1);

		return res.size() != 0;
	}
	
	/**
	 * сечение ломанной линии произвольным выпуклым, замкнутым полигоном по Кириусу-Беку
	 * @param polygon - выпуклый, замкнутый задан массовом точек
	 * @param section_type - 0 - отсечение линий
	 * @param section_type - 1 - отсечение построенных на линиях прямоугольников внутренее
	 * @param section_type - -1 - отсечение построенных на линиях прямоугольников внешнее
	 * @return массив ребер. отсекаются все внешние ребра или их части для упорядоченного по часовой стрелке полигона
	 * массив ребер. отсекаются все внутренние ребра или их части для упорядоченного по противчасовой стрелки полигона
	 */
	public ArrayList<Edge> SectionByGenericFigure(ArrayList<Vertex> polygon, int section_type)
	{
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			Edge current_edge = new Edge(m_points.get(i), m_points.get(i+1));
			Edge res = null;
			switch(section_type)
			{
				case 0: //отсечение линий
				{
					res = current_edge.SectionByPolygon(polygon);
					break;
				}
				case 1: //отсечение построенных на линиях прямоугольников внутренее
				{
					res = current_edge.IncludedEdgeByGeometry(polygon, m_textHeight/2);
					break;
				}
				case -1: //отсечение построенных на линиях прямоугольников внешнее
				{
					res = current_edge.ExcludingEdgeByGeometry(polygon, m_textHeight);
					break;
				}
				default:
				{
					break;
				}
			};
			if(res != null)
			{
				edges.add(res);
			}
		}
		m_edges = edges;
		return edges;
	}
	
	/*public void FindUnUsedSpace(GIQuatroTree tree, RectF bounds)
	{
		int edge_counter = 0; 
		while(edge_counter < m_edges.size())
		{
			Edge current_edge = m_edges.get(edge_counter);
			current_edge.m_excluded_parts = new ArrayList<Edge>();
			int code = tree.getCode(current_edge);
			ArrayList<GIGeometryObject> ListOfAll = tree.getDependies(code);
			for(int c = 0; c < ListOfAll.size(); c++)
			{
				GIGeometryPolygon compare = (GIGeometryPolygon)ListOfAll.get(c);
				Edge to_exclude = current_edge.ExcludingEdgeByGeometry(compare.m_points, m_textHeight/2);
				if(to_exclude != null)
				{
					current_edge.m_excluded_parts.add(to_exclude);
				}
			}
			ArrayList<Edge> newpath = current_edge.ExcludeAll();
			m_edges.addAll(edge_counter, newpath);
			m_edges.remove(current_edge);
			edge_counter++;
		}
	}*/
	/**
	 * удаляет уже заведомо занятое пространство 
	 * @param tree квадродерево шейпов "занятого"
	 * @param bounds видимый прямоугольник
	 */
	public void RemoveBusySpace(GIQuadtree tree, RectF bounds)
	{
		int edge_counter = 0; 
		while(edge_counter < m_edges.size())
		{
			Edge current_edge = m_edges.get(edge_counter);
			ArrayList<Edge> excluded_parts = new ArrayList<Edge>();
			
			Point code = tree.MortonCode2D(current_edge);
			
			ArrayList<GIGeometryObject> ListOfAll = tree.getDependies(code);
			for(int c = 0; c < ListOfAll.size(); c++)
			{
				GIGeometryPolygon compare = (GIGeometryPolygon)ListOfAll.get(c);
				Edge to_exclude = current_edge.ExcludingEdgeByGeometry(compare.m_points, m_textHeight/2);
				if(to_exclude != null)
				{
					excluded_parts.add(to_exclude);
				}
			}
			ArrayList<Edge> newpath = current_edge.ExcludeAll(excluded_parts);
			m_edges.addAll(edge_counter, newpath);
			m_edges.remove(current_edge);
			if(newpath.size() > 0)
			{
				edge_counter++;
			}
		}
	}
	public boolean Simplify(Paint paint)
	{
		float total_length = 0;
		float delta = paint.getTextSize();
		ArrayList<Edge> res = new  ArrayList<Edge>(); 
		for(Edge edge : m_edges)
		{
			float lenght = edge.Lenght();
			if(lenght > delta)
			{
				res.add(edge);
				total_length = total_length + lenght;
			}
		}
		m_edges = res;
		if(total_length > LabelText.getTextRect(m_labeltext, paint).width())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 *  основная функция класса линий.
	 */
	public boolean FoundCandidates(RectF bounds, GIQuadtree tree, Paint paint)
	{
		
		m_textHeight = paint.getTextSize();
		IntersectByRect(bounds);
		//MakeEdgesRing();
		//TODO debug
		boolean res = true;
		//res = Simplify(paint);
		//TODO debug
		RemoveBusySpace(tree, bounds);
		return res;
	}

	@Override
	public ArrayList<Edge> MakeEdgesRing() 
	{
		m_edges = new ArrayList<Edge>();
		for(int i = 0; i < m_points.size()-1; i++)
		{
			Vertex current = new Vertex(m_points.get(i));
			Vertex next = new Vertex(m_points.get(i+1));
			if(!((current._m_original != 0)&&(next._m_original != 0)))
			{
				Edge edge = new Edge(current, next);
				m_edges.add(edge);
			}
		}
		return m_edges;
	}
	public void DrawGeometry(Canvas canvas)
	{
		Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Style.STROKE);



        if(m_edges == null)
        {
        	MakeEdgesRing();
        }
        if(m_edges.size() == 0)
        {
        	MakeEdgesRing();
        }
        Path path = new Path();
        
        for(int k = 0; k < m_edges.size(); k++)
		{
        	Edge edge = m_edges.get(k);
        	path.moveTo(edge.m_start.x, edge.m_start.y);
        	path.lineTo(edge.m_end.x, edge.m_end.y);
        }
        
        canvas.drawPath(path, paint);
	}
	
	public void DrawBoundaries(Canvas canvas, Paint paint_text)
	{
		Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);

        if(m_edges == null)
        {
        	MakeEdgesRing();
        }
        if(m_edges.size() == 0)
        {
        	MakeEdgesRing();
        }
        Path path = new Path();
        
        for(int k = 0; k < m_edges.size(); k++)
		{
        	Edge edge = m_edges.get(k);
        	ArrayList<Edge> bounds = edge.getTextBounds(paint_text.getTextSize()/2);
        	path.moveTo(bounds.get(0).m_start.x, bounds.get(0).m_start.y);
        	path.lineTo(bounds.get(0).m_end.x, bounds.get(0).m_end.y);
        	
        	path.moveTo(bounds.get(1).m_start.x, bounds.get(1).m_start.y);
        	path.lineTo(bounds.get(1).m_end.x, bounds.get(1).m_end.y);
        }
        canvas.drawPath(path, paint);
	}
	@Override
	public void DrawBoundaries(Canvas canvas) 
	{
		Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);

        if(m_edges == null)
        {
        	MakeEdgesRing();
        }
        if(m_edges.size() == 0)
        {
        	MakeEdgesRing();
        }
        Path path = new Path();
        
        for(int k = 0; k < m_edges.size(); k++)
		{
        	Edge edge = m_edges.get(k);
        	ArrayList<Edge> bounds = edge.getTextBounds(m_textHeight/2);
        	path.moveTo(bounds.get(0).m_start.x, bounds.get(0).m_start.y);
        	path.lineTo(bounds.get(0).m_end.x, bounds.get(0).m_end.y);
        	
        	path.moveTo(bounds.get(1).m_start.x, bounds.get(1).m_start.y);
        	path.lineTo(bounds.get(1).m_end.x, bounds.get(1).m_end.y);
        }
        canvas.drawPath(path, paint);
		
	}
	@Override
	public void DrawRects(Canvas canvas, Paint paint_text)
	{
		Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setStrokeWidth(paint_text.getTextSize());
        paint.setAlpha(128);
		Paint paint_start = new Paint();
		paint_start.setColor(Color.BLUE);
		paint_start.setStyle(Style.FILL_AND_STROKE);
		Paint paint_ID = new Paint();
		paint_ID.setColor(Color.RED);
		paint_ID.setStyle(Style.FILL_AND_STROKE);
		paint_ID.setTextSize(paint_text.getTextSize());
		//paint_start.setAlpha(128);

        if(m_edges == null)
        {
        	MakeEdgesRing();
        }
        if(m_edges.size() == 0)
        {
        	MakeEdgesRing();
        }
        if(m_paths == null)
        {
        	MakeTextPaths();
        }
        if(m_paths.size() == 0)
        {
        	MakeTextPaths();
        }
        //Path path = new Path();

        for(int i = 0; i < m_paths.size(); i++)
        {
        	ArrayList<Edge> current_part = m_paths.get(i);
        	if(current_part.size() > 0)
        	{
        		canvas.drawCircle(current_part.get(0).m_start.x, current_part.get(0).m_start.y, paint_text.getTextSize()/4, paint_start);
        		//canvas.drawText(m_labeltext, current_part.get(0).m_start.x + paint_text.getTextSize()/2, current_part.get(0).m_start.y + paint_text.getTextSize()/2, paint_ID);
        	}
        	for(int j = 0; j < current_part.size(); j++)
        	{
        		Path path = new Path();
        		Edge edge = current_part.get(j);
            	path.moveTo(edge.m_start.x, edge.m_start.y);
            	path.lineTo(edge.m_end.x, edge.m_end.y);
                paint.setShader(new RadialGradient(edge.m_start.x, edge.m_start.y, edge.Lenght(), Color.CYAN, Color.BLUE, Shader.TileMode.MIRROR));
                canvas.drawPath(path, paint);
        	}
        	//canvas.drawPath(path, paint);
        }

	}
	
	public void MakeTextPaths()
	{
		
		if(m_edges == null)
		{
			return;
		}
		if(m_edges.size() < 1)
		{
			return;
		}
		GITextPath current_part = new GITextPath();
		
		current_part.add(m_edges.get(0));
		for(int i = 1; i < m_edges.size(); i++)
		{
			Edge prev = m_edges.get(i-1);
			Edge current = m_edges.get(i);
			if(prev.m_end.equals(current.m_start) &&(((prev.m_end.x - prev.m_start.x) >= 0 && (current.m_end.x - current.m_start.x) >= 0)||((prev.m_end.x - prev.m_start.x) < 0 && (current.m_end.x - current.m_start.x) < 0) ))
			{
				current_part.add(current);
			}
			else
			{
				m_paths.add(current_part);
				current_part =  new GITextPath();
				current_part.add(current);
			}
			/*if(current.Tan() < 0 ) 
			{
				current.Swap();
			}*/
		}
		if(current_part.size() > 0)
		{
			m_paths.add(current_part);
		}
	}


}
