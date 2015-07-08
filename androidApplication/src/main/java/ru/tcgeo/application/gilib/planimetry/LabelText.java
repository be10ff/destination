//package ru.tcgeo.application.gilib.planimetry;
//
//import java.util.ArrayList;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PointF;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Paint.Style;
//
//
//public class LabelText {
//
//	public String m_text;
//	public GIGeometryPolygon m_geometry;
//
//	public ArrayList<RectF> m_rects;
//	public ArrayList<Trapezoid> m_row_segments;
//	public ArrayList<Vertex> m_candidates;
//
//	public LabelText(String text, GIGeometryPolygon geometry)
//	{
//		m_text = text;
//		m_geometry = geometry;
//		m_rects = new ArrayList<RectF>();
//		m_row_segments = new ArrayList<Trapezoid>();
//	}
//	public LabelText(GIGeometryPolygon geometry)
//	{
//		m_text = geometry.m_labeltext;
//		m_geometry = geometry;
//		m_rects = new ArrayList<RectF>();
//		m_row_segments = new ArrayList<Trapezoid>();
//	}
//	public static Rect getTextRect(String text, Paint paint)
//	{
//		Rect bounds = new Rect();
//		paint.getTextBounds(text, 0, text.length(), bounds);
//		bounds.offset((int) (bounds.width()*1.2), 0);
//		return bounds;
//	}
//	public void Draw(Canvas canvas, Paint paint)
//	{
//		if(m_candidates != null)
//		{
//	        for(int k = 0; k < m_candidates.size(); k++)
//	        {
//	        	Vertex cur = m_candidates.get(k);
//	        	canvas.drawText(cur.m_string, cur.x, cur.y, paint);
//	        }
//		}
//	}
//	public void DrawGeometry(Canvas canvas)
//	{
//		m_geometry.DrawGeometry(canvas);
//	}
//	public void DrawLevels(Canvas canvas)
//	{
//		m_geometry.DrawLevels(canvas);
//	}
//	public void DrawRects(Canvas canvas)
//	{
//		Paint paint = new Paint();
//        paint.setColor(Color.CYAN);
//        paint.setAlpha(128);
//        paint.setStyle(Style.FILL);
//
//		Paint paint_contur = new Paint();
//		paint_contur.setColor(Color.BLUE);
//		paint_contur.setAlpha(128);
//		paint_contur.setStyle(Style.STROKE);
//
//        if(m_rects == null)
//        {
//        	return;
//        }
//        for(int k = 0; k < m_rects.size(); k++)
//		{
//        	RectF rect = m_rects.get(k);
//        	canvas.drawRect(rect, paint);
//        	canvas.drawRect(rect, paint_contur);
//        }
//	}
//
//
//	public void DrawTrapezoid(Canvas canvas)
//	{
//		//m_geometry.DrawRects(canvas);
//		Paint paint = new Paint();
//        //Path path= new Path();
//        paint.setColor(Color.YELLOW);
//        paint.setStyle(Style.STROKE);
//        paint.setStrokeWidth(6);
//
//        if(m_row_segments == null)
//        {
//        	return;
//        }
//        for(int k = 0; k < m_row_segments.size(); k++)
//		{
//        	Trapezoid tr = m_row_segments.get(k);
//        	/*canvas.drawLine(tr.m_top_edge.m_start.x, tr.m_top_edge.m_start.y, tr.m_bottom_edge.m_end.x, tr.m_bottom_edge.m_end.y, paint);
//        	canvas.drawLine(tr.m_top_edge.m_end.x, tr.m_top_edge.m_end.y, tr.m_bottom_edge.m_start.x, tr.m_bottom_edge.m_start.y, paint);*/
//        	Edge left = new Edge(tr.m_top_edge.m_start, tr.m_bottom_edge.m_start);
//        	Edge right = new Edge(tr.m_top_edge.m_end, tr.m_bottom_edge.m_end);
//        	PointF from = left.center_point();
//        	PointF to = right.center_point();
//
//        	canvas.drawLine(from.x, from.y, to.x, to.y, paint);
//        }
//	}
//
//	public void DrawEdges(Canvas canvas)
//	{
//		m_geometry.DrawEdges(canvas);
//	}
//
//	public String[] SplitText(String text)
//	{
//		//String seq = new String("\\s+|,\\s*|\\.\\s*");
//		String seq = new String("\\ ");
//		String string = new String(text);
//		//ArrayList<String> result = new ArrayList<String>();
//		String[] result =string.split(seq);
//		return result;
//	}
//	public void getRectArray(float fontsize)
//	{
//        RectF rest_part = null;
//		float levelY = m_row_segments.get(0).getTop();
//        float maxY =  m_row_segments.get(m_row_segments.size() - 1).getBottom();
//        int index = 0;
//        do
//        {
//           	Trapezoid current_segment = m_row_segments.get(index);
//        	float rest_height = 0;
//        	if(rest_part != null)
//        	{
//        		rest_height = rest_part.height();
//        	}
//        	//------------------------------------------
//        	//разрыв. отбрасываем rest и начинаем с начала текущего сегмента
//        	if((current_segment.getTop() - (levelY+rest_height)) > Vertex.delta)
//        	{
//        		rest_part = null;
//        		levelY = current_segment.getTop();
//        	}
//        	//разрыва нет
//        	else
//        	{
//        		float levelN = levelY;
//        		//помещаемся в следующий : levelN по высоте строки
//        		if(current_segment.getBottom() >= levelY  + fontsize)
//            	{
//        			levelN = levelY  + fontsize;
//            	}
//            	//не поместились в следующий : levelN по высоте сегмента
//            	else
//            	{
//            		levelN = current_segment.getBottom();
//            		//на следующем проходе берем следующий;
//            		index++;
//            	}
//        		//режем
//        		Trapezoid tmp = Trapezoid.SplitTrapezoidAtHeights(current_segment, levelY+rest_height, levelN);
//        		if(tmp == null)
//        		{
//        			rest_height += 0.5;
//        			continue;
//        		}
//        		RectF part = tmp.getRect();
//        		//если есть с чем - объеденяем
//            	if(rest_part != null)
//            	{
//        			//нашли объединенную ширину
//    				float left = Math.max(rest_part.left, part.left);
//            		float right = Math.min(rest_part.right, part.right);
//            		//достаточно ширины
//            		//if(right - left > fontsize)
//            		{
//	            		float top = rest_part.top;
//	            		float bottom = part.bottom;
//	            		part = new RectF(left, top, right, bottom);
//	            		//float height = part.height();
//	            		//levelY = rest_part.bottom;
//	            		//m_rects.add(rest_part);
//	            		//rest_part = null;
//            		}
//            		//недостаточно ширины
//            		/*else
//            		{
//            			rest_part = null;
//            			//levelY = rest_part.top;
//            		}*/
//            	}
//
//            	if(fontsize - part.height() < Vertex.delta)
//            	{
//            		m_rects.add(part);
//            		levelY += fontsize;
//            		rest_part = null;
//            	}
//            	else
//            	{
//            		if(Math.abs(part.height()) > Vertex.delta)
//            		{
//            			rest_part = part;
//            		}
//            		else
//            		{
//            			rest_part = null;
//            		}
//            	}
//        	}
//        }while((levelY <= maxY)&&(index < m_row_segments.size())) ;
//	}
//
//	public void getRectArray_old(float fontsize)
//	{
//        //-----------------------------------------------------------------------------------------------------------------------------
//        //TODO: working but too difficult & hard
//        //-----------------------------------------------------------------------------------------------------------------------------
//        //unfinished loop here
//        //-----------------------------------------------------------------------------------------------------------------------------
//        //Trapezoid rest = null;
//        RectF rest_part = null;
//		float levelY = m_row_segments.get(0).getTop();
//        float maxY =  m_row_segments.get(m_row_segments.size() - 1).getBottom();
//        int index = 0;
//        do
//        {
//        	//next
//        	Trapezoid current_segment = m_row_segments.get(index);
//        	//checking rest ---------------------------
//        	float rest_height = 0;
//        	if(rest_part != null)
//        	{
//        		rest_height = rest_part.height();
//        	}
//        	if(rest_height > fontsize)
//        	{
//
//        		RectF part =  new RectF(rest_part.left, rest_part.top, rest_part.right, rest_part.top + fontsize);
//        		m_rects.add(part);
//        		levelY = rest_part.top + fontsize;
//        		rest_part =  new RectF(rest_part.left, rest_part.top + fontsize, rest_part.right, rest_part.bottom);
//        		rest_height = rest_part.height();
//        	}
//        	//------------------------------------------
//        	levelY = levelY + rest_height;
//
//        	if(current_segment.getBottom() >= levelY - rest_height + fontsize)
//        	{
//        		//помещаемся в следующий
//        		Trapezoid tmp = Trapezoid.SplitTrapezoidAtHeights(current_segment, levelY, levelY + fontsize - rest_height);
//        		//есть пересечение. можно проверить тупо что следующий начинается с конца предыдущего
//        		if(tmp != null)
//        		{
//	        		RectF part = tmp.getRect();
//	        		//есть остаток
//	        		if(rest_part != null)
//	        		{
//	        			//следующий начинается с конца предыдущего
//	        			//кажется это уже лишнее тут.... может выше поднять
//	        			if(Math.abs(rest_part.bottom - part.top) < Vertex.delta)
//	            		{
//	            			//нашли объединенную ширину
//	        				float left = Math.max(rest_part.left, part.left);
//		            		float right = Math.min(rest_part.right, part.right);
//		            		//достаточно ширины
//		            		if(right - left > fontsize)
//		            		{
//			            		float top = rest_part.top;
//			            		float bottom = part.bottom;
//			            		rest_part = new RectF(left, top, right, bottom);
//			            		levelY = rest_part.bottom;
//			            		m_rects.add(rest_part);
//			            		rest_part = null;
//		            		}
//		            		//недостаточно ширины
//		            		else
//		            		{
//		            			rest_part = null;
//		            			//levelY = rest_part.top;
//		            		}
//		            	}
//	            		else
//	            		//следующий НЕ начинается с конца предыдущего
//	            		{
//	            			rest_part = null;
//	            			//levelY = rest_part.top;
//	            		}
//	        		}
//	        		else
//	        		// остаток нулевой
//	        		{
//		        		m_rects.add(part);
//		        		levelY += fontsize;
//	        		}
//	        	}
//        		else
//        		//нет пересечения. или следующий НЕ начинается с конца предыдущего
//        		{
//        			levelY = current_segment.getTop(); //повторяем с начала current_segment
//        			rest_part = null; //rest использовать не удастся.
//        		}
//	        }
//        	//не поместились в следующий
//        	else
//        	{
//        		Trapezoid tmp = Trapezoid.SplitTrapezoidAtHeights(current_segment, levelY, current_segment.getBottom());
//        		//есть пересечение. можно проверить тупо что следующий начинается с конца предыдущего
//        		if(tmp != null)
//        		{
//	        		RectF part = tmp.getRect();
//	        		//предыдущий остаток нулевой. все просто. сохранили и ушли к инкрементации сегмента
//	            	if(rest_part == null)
//	            	{
//	            		rest_part = part;
//	            	}
//	            	//предыдущий остаток НЕ нулевой
//	            	else
//	            	{
//	            		//следующий начинается с конца предыдущего
//	            		//кажется это уже лишнее тут.... может выше поднять
//	            		if(Math.abs(rest_part.bottom - part.top) < Vertex.delta)
//	            		{
//	            			float left = Math.max(rest_part.left, part.left);
//		            		float right = Math.min(rest_part.right, part.right);
//		            		if(right - left > fontsize)
//		            		{
//			            		float top = rest_part.top;
//			            		float bottom = part.bottom;
//			            		rest_part = new RectF(left, top, right, bottom);
//		            		}
//		            		else
//		            		{
//		            			rest_part = null;
//		            		}
//		            	}
//	            		//следующий НЕ начинается с конца предыдущего
//	            		else
//	            		{
//	            			rest_part = null;
//	            		}
//	            	}
//	            	index++;
//	        	}
//        		//нет пересечения. или следующий НЕ начинается с конца предыдущего
//        		else
//        		{
//        			levelY = current_segment.getTop(); //повторяем с начала current_segment
//        			rest_part = null; //rest использовать не удастся.
//        		}
//        	}
//        }while((levelY <= maxY)&&(index < m_row_segments.size())) ;
//	}
//	public ArrayList<Vertex> FoundCandidates(RectF bounds, Paint paint)
//	{
//		//praparing text
//		//boolean IsPossible = true;
//		//String seq = new String("\\ |\\-");
//		String seq = new String("\\ ");
//
//		String[] res = m_text.split(seq);
//		ArrayList<String> strings = new ArrayList<String>();
//		for(int i=0; i < res.length;i++)
//		{
//			strings.add(res[i]);
//		}
//
//		ArrayList<Rect> substrings_bounds = new ArrayList<Rect>();
//		for(int i = 0; i <strings.size();i++)
//		{
//			substrings_bounds.add(getTextRect(strings.get(i), paint));
//		}
//		//int max_length_index = 0;
//		int max_length = 0;
//
//
//		for(int i = 0; i <substrings_bounds.size();i++)
//		{
//			if(substrings_bounds.get(i).width() > max_length)
//			{
//				max_length = substrings_bounds.get(i).width();
//				//max_length_index = i;
//			}
//		}
//
//        //float str_height = getTextRect("Eq|", paint).height();
//		float str_height = paint.getTextSize();
//        //unsucessfull
//        m_geometry.generalize( /*paint.getTextSize()*/1);
//		m_geometry.IntersectByRect(bounds);
//		if(m_geometry.getBounds().width() < max_length)
//		{
//			return null;
//		}
//		if(m_geometry.getBounds().height() < str_height)
//		{
//			return null;
//		}
//		//TODO: full logic
//        m_row_segments = m_geometry.getTextMinTrapezoidArray();
//		//
//        if(m_row_segments == null)
//        {
//        	return null;
//        }
//        if(m_row_segments.size() == 0)
//        {
//        	return null;
//        }
//
//        float max_rect_width = 0;
//        int max_widthy_rect_index = 0;
//
//        //Trapezoid rest = null;
//        //RectF rest_part = null;
//
//        for(int i = 0; i < m_row_segments.size(); i++)
//        {
//        	Trapezoid segment_to_split = m_row_segments.get(i);
//
//            if(segment_to_split.getWidth() > max_rect_width)
//            {
//            	max_rect_width = segment_to_split.getWidth();
//            	max_widthy_rect_index = i;
//            }
//        } // old
//
//        getRectArray(paint.getTextSize());
//
////---------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
//        if(m_row_segments.size() == 0)
//        {
//        	return null;
//        }
//
//        RectF geometry_bounds = m_geometry.getBounds();
//		// fail
//        if(m_rects.size() == 0)
//        {
//        	return null;
//        }
//		if(max_length > geometry_bounds.width())
//		{
//			return null;
//		}
//		if(geometry_bounds.height() < str_height)
//		{
//			return null;
//		}
//		//trivial
//		//int full_text_lenght = getTextRect(m_text, paint).width();
//		int middle_index = (m_rects.size() - 1)/2;
//		if(getTextRect(m_text, paint).width() < m_rects.get(middle_index).width())
//		{
//			RectF candidate = new RectF(m_rects.get(middle_index).left, m_rects.get(middle_index).top + str_height, m_rects.get(middle_index).right, m_rects.get(middle_index).bottom);
//			ArrayList<Vertex> result = new ArrayList<Vertex>();
//			float next_lenght = getTextRect(m_text, paint).width();
//        	float curr_height = getTextRect(m_text, paint).height();
//			PointF draw_point = new PointF((candidate.left + candidate.right)/2 - next_lenght/2  , curr_height/2 + (candidate.bottom + candidate.top)/2);
//			Vertex vertex = new Vertex(draw_point, m_text);
//			result.add(vertex);
//	        m_candidates = result;
//			return result;
//		}
//		if(getTextRect(m_text, paint).width() < max_length)
//		{
//			RectF candidate = new RectF(m_rects.get(max_widthy_rect_index).left, m_rects.get(max_widthy_rect_index).top + str_height, m_rects.get(max_widthy_rect_index).right, m_rects.get(max_widthy_rect_index).bottom);
//			ArrayList<Vertex> result = new ArrayList<Vertex>();
//			float next_lenght = getTextRect(m_text, paint).width();
//        	float curr_height = getTextRect(m_text, paint).height();
//			PointF draw_point = new PointF((candidate.left + candidate.right)/2 - next_lenght/2  , curr_height/2 + (candidate.bottom + candidate.top)/2);
//			Vertex vertex = new Vertex(draw_point, m_text);
//			result.add(vertex);
//	        m_candidates = result;
//			return result;
//		}
//		//
//        //main stupid from top
//        //-----------------------------------------------------
//		/*
//        ArrayList<Vertex> result = new ArrayList<Vertex>();
//
//        boolean ready = false;
//        int subs_counter=0;
//        int rects_counter=0;
//        do
//        {
//        	String substr = strings.get(subs_counter);
//        	float curr_lenght = getTextRect(substr, paint).width();
//        	float curr_height = getTextRect(substr, paint).height();
//        	RectF candidate = m_rects.get(rects_counter);
//        	float candidate_width = candidate.width();
//        	float candidate_height = candidate.height();
//			Vertex container = null;
//			boolean sucsess = false;
//			int string_amount = ((int)candidate_height)/((int) curr_height);
//
//
//			while((candidate.width() >= getTextRect(substr, paint).width()) && (subs_counter < strings.size()))
//        	{
//        		float next_lenght = getTextRect(substr, paint).width();
//        		sucsess = true;
//        		subs_counter++;
//    			PointF draw_point = new PointF((candidate.left + candidate.right)/2 - next_lenght/2  , curr_height/2 + (candidate.bottom + candidate.top)/2);
//        		container = new Vertex(draw_point, substr);
//        		if(subs_counter < strings.size())
//        		{
//        			substr = substr + " " + strings.get(subs_counter);
//        		}
//
//        	}
//        	if(sucsess)
//        	{
//        		result.add(container);
//        	}
//        	rects_counter++;
//
//        }
//		while(subs_counter < strings.size() && rects_counter < m_rects.size()-1);
//        if(subs_counter == strings.size())
//        {
//        	return result;
//        }
//        else
//        {
//        	int i = result.size();
//        	return result;
//        	//return null;
//        }
//        */
//        //-----------------------------------------------------
//        //from middle
//        ArrayList<Vertex> result = new ArrayList<Vertex>();
//
//        float rects_total_width = 0;
//        for(int i = 0; i < m_rects.size(); i++)
//        {
//        	RectF curr = m_rects.get(i);
//        	float add = curr.width();
//        	rects_total_width += add;
//        }
//        float gain_width = 0;
//        int mid_rect_index = 0;
//        while((gain_width < rects_total_width/2) && (mid_rect_index < m_rects.size()))
//        {
//        	gain_width += m_rects.get(mid_rect_index).width();
//        	mid_rect_index++;
//        }
//
//        if(getTextRect(m_text, paint).width() > rects_total_width)
//        {
//        	return null;
//        }
//        gain_width = 0;
//        int mid_world_index = 0;
//        while((gain_width < getTextRect(m_text, paint).width()/2) && (mid_world_index < strings.size()))
//        {
//        	gain_width += getTextRect(strings.get(mid_world_index), paint).width();
//			mid_world_index++;
//        }
//
//        boolean ready = false;
//        //go bottom
//
//        int subs_counter = mid_world_index;
//        int rects_counter = mid_rect_index;
//        if(!( subs_counter > strings.size() - 1 || rects_counter > m_rects.size() - 1 ))
//        {
//
//	        do
//	        {
//	        	String substr = strings.get(subs_counter);
//	        	//float curr_lenght = getTextRect(substr, paint).width();
//	        	//float curr_height = getTextRect(substr, paint).height();
//	        	float curr_height = str_height;
//	        	RectF candidate = m_rects.get(rects_counter);
//	        	//float candidate_width = candidate.width();
//	        	//float candidate_height = candidate.height();
//				Vertex container = null;
//				boolean sucsess = false;
//				//int string_amount = ((int)candidate_height)/((int) curr_height);
//
//
//				while((candidate.width() >= getTextRect(substr, paint).width()) && (subs_counter < strings.size()))
//	        	{
//	        		float next_lenght = getTextRect(substr, paint).width();
//	        		sucsess = true;
//	        		subs_counter++;
//	    			PointF draw_point = new PointF((candidate.left + candidate.right)/2 - next_lenght/2  , curr_height/2 + (candidate.bottom + candidate.top)/2);
//	        		container = new Vertex(draw_point, substr);
//	        		if(subs_counter < strings.size())
//	        		{
//	        			substr = substr + " " + strings.get(subs_counter);
//	        		}
//
//	        	}
//	        	if(sucsess)
//	        	{
//	        		result.add(container);
//	        	}
//	        	rects_counter++;
//
//	        }
//			while(subs_counter < strings.size() && rects_counter < m_rects.size()/*-1*/);
//	        ready = true;
//		}
//        ready = (subs_counter == strings.size());
//	//-------------------------------------------------------------------------------------------------------------------
//        subs_counter = mid_world_index - 1;
//        rects_counter = mid_rect_index - 1;
//        if(!( subs_counter < 0 || rects_counter <0 ))
//        {
//	        do
//	        {
//	        	String substr = strings.get(subs_counter);
//	        	//float curr_lenght = getTextRect(substr, paint).width();
//	        	float curr_height = str_height;
//	        	//float curr_height = getTextRect(substr, paint).height();
//	        	RectF candidate = m_rects.get(rects_counter);
//	        	//float candidate_width = candidate.width();
//	        	//float candidate_height = candidate.height();
//				Vertex container = null;
//				boolean sucsess = false;
//				//int string_amount = ((int)candidate_height)/((int) curr_height);
//
//
//				while((candidate.width() >= getTextRect(substr, paint).width()) && (subs_counter >= 0))
//	        	{
//	        		float next_lenght = getTextRect(substr, paint).width();
//	        		sucsess = true;
//	        		subs_counter--;
//	    			PointF draw_point = new PointF((candidate.left + candidate.right)/2 - next_lenght/2  , curr_height/2 + (candidate.bottom + candidate.top)/2);
//	        		container = new Vertex(draw_point, substr);
//	        		if(subs_counter >= 0)
//	        		{
//	        			substr = strings.get(subs_counter) + " " + substr;
//	        		}
//
//	        	}
//	        	if(sucsess)
//	        	{
//	        		result.add(container);
//	        	}
//	        	rects_counter--;
//
//	        }
//			while(subs_counter >= 0 && rects_counter >= 0);
//        }
//        ready = ready && (subs_counter < 0);
////--------------------------------------------------------------------------------------------------------------------
//
//        m_candidates = result;
//        if(ready)
//        {
//        	m_candidates = result;
//        	return result;
//        }
//        else
//        {
//        	m_candidates = null;
//        	return null;
//        }
//	}
//
//
//}
