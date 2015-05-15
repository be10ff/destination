package ru.tcgeo.gilib.script;

import ru.tcgeo.gilib.GIDataRequestorImp;
import ru.tcgeo.gilib.GISQLRequest;
import ru.tcgeo.gilib.script.GIScriptQueue;

public class GIScriptParser {

	private String m_origin_text;
	private GIScriptQueue m_text;
	//private GIDataRequestorImp m_requestor;
	private GIScriptExpression m_exp;
	//private GIMap m_map;
	private GIScriptDict m_root_dict;

	
	public GIScriptParser(String script_text)//, GIDataRequestorImp requestor, GIMap map) 
	{
		
		m_origin_text = script_text;
		m_text = new GIScriptQueue(m_origin_text);
		m_root_dict = new GIScriptDict();
		m_exp = Read(m_text, m_root_dict);//m_requestor, m_map));


	}
	public Object Eval(GIDataRequestorImp requestor)
	{
		m_root_dict.m_requestor = requestor;
		m_root_dict.m_sql = null;
		return m_exp.eval();
	}
	public Object Eval(GISQLRequest sql)
	{
		m_root_dict.m_requestor = null;
		m_root_dict.m_sql = sql;
		return m_exp.eval();
	}
	public GIScriptExpression Read(GIScriptQueue text, GIScriptDict dict)
	{
		GIScriptExpression exp = null;
		while(!text.Empty())
		{
			char current = text.Look();
			
			if(current == '(')
			{
				text.Pop();
				GIScriptDict local_dict = new GIScriptDict(dict);
				GIScriptExpressionBlock f_exp = new GIScriptExpressionBlock("block", local_dict);
				while((text.Look() != ')') && (!text.Empty()) )
				{
					f_exp.Append(Read(text, local_dict));
				}
				exp = f_exp;
				text.Pop();	
				return exp;
			}
			else if(current == '\"')
			{
				text.Pop();
				return exp = new GIScriptExpressionLiteral(text);
			}
			else if(Character.isDigit(current))
			{
				return exp = new GIScriptExpressionNumeral(text);
			}
			else if(Character.isLetter(current)|| GIScriptQueue.IsVal(current))
			{
				String name ="";
				while((Character.isLetter(current)|| GIScriptQueue.IsVal(current))&&!Character.isSpace(current))
				{
					name += text.Pop();
					current = text.Look();
				}
				GIScriptExpression unk = dict.Find(name);
				if(unk != null)
				{
					if(unk.Type() == GIScriptExpression.TYPE.define)
					{
						if(name.equalsIgnoreCase("define"))
						{
							exp = new GIScriptExpressionDefine("define");
							return exp;
						}
					}
					else if(unk.Type() == GIScriptExpression.TYPE.constant)
					{
						exp = (GIScriptExpression)dict.Find(name);
						return exp;
					}
					
					else if(unk.Type() == GIScriptExpression.TYPE.lonlat)
					{
						exp = (GIScriptExpression)dict.Find(name);
						return exp;
					}

					else if(unk.Type() == GIScriptExpression.TYPE.operation)
					{
						exp = ((GIScriptExpressionOperation)unk).Clone(); 
						return exp; 
					}
					else if(unk.Type() == GIScriptExpression.TYPE.set)
					{
						exp = (GIScriptExpressionSet)unk.Clone();
						return exp;
					}
				}
				return exp = new GIScriptExpressionVar(name, dict);
			}
			else
			{
				current = text.Pop();
			}
		}
		return exp;
	}
}
