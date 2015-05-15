package ru.tcgeo.gilib.script;

import java.util.ArrayList;

public class GIScriptExpressionBlock extends GIScriptExpression {

	protected ArrayList<GIScriptExpression> m_args;
	public GIScriptDict m_dict;
	public GIScriptExpressionBlock(String name, GIScriptDict dict) {
		super(name);
		m_dict = dict;
		m_args = new ArrayList<GIScriptExpression>();
	}
	
	@Override
	public GIScriptExpressionBlock Clone()
	{
		return this;
	}
	
	public void Append(GIScriptExpression arg)
	{
		if(arg == null)
		{
			return;
		}
		m_args.add(arg);
	}
	@Override
	public TYPE Type() {
		return GIScriptExpression.TYPE.block;
	}
	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) 
	{
		GIScriptExpression exp = m_args.get(0).eval();
		ArrayList<GIScriptExpression> params = new ArrayList<GIScriptExpression>();
		if(m_args.size() > 1)
		{
			for(int i = 1; i < m_args.size(); i++)
			{
				params.add(m_args.get(i).eval());
			}
		}
		return exp.apply(params);
	}

	@Override
	public GIScriptExpression eval() {
		if(m_args.size() == 0)
		{
			return new GIScriptExpressionError("Unexcepted/missed argument", this);
		}
		if(m_args.size() == 1)
		{
			return m_args.get(0).eval();
		}
		
		GIScriptExpression exp =  (GIScriptExpression) m_args.get(0).eval();
		while(exp == null)
		{
			m_args.remove(0);
			exp =  (GIScriptExpression) m_args.get(0).eval();			
		}
		if(exp.Type() == GIScriptExpression.TYPE.define)
		{
			exp.m_dict = m_dict;
			ArrayList<GIScriptExpression> params = new ArrayList<GIScriptExpression>();
			for(int i = 1; i < m_args.size(); i++)
			{
				params.add(m_args.get(i));
			}
			return exp.apply(params);
		}
		ArrayList<GIScriptExpression> params = new ArrayList<GIScriptExpression>();
		for(int i = 1; i < m_args.size(); i++)
		{
			params.add(m_args.get(i));
		}
		GIScriptExpression value = (GIScriptExpression) exp.apply(params);
		return value;
	}
}
