package ru.tcgeo.gilib.script;

import java.util.ArrayList;

public class GIScriptExpressionSet extends GIScriptExpression {

	protected ArrayList<GIScriptExpression> m_args;
	public GIScriptExpressionSet(String name) {
		super(name);
		m_args = new ArrayList<GIScriptExpression>();
	}

	public ArrayList<GIScriptExpression> getValue()
	{
		return m_args;
	}
	@Override
	public boolean Equal(GIScriptExpression compare_to)
	{
		if(compare_to.Type() != GIScriptExpression.TYPE.set)
		{
			return false;
		}
		GIScriptExpressionSet compare = (GIScriptExpressionSet) compare_to;
		if(m_args.size() != compare.getValue().size())
		{
			return false;
		}
		for(int i = 0; i < m_args.size(); i++)
		{
			if(!m_args.get(i).Equal(compare.getValue().get(i)))
			{
				return false;
			}
		}
		return true;
	}	
	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) 
	{
		m_args = new ArrayList<GIScriptExpression>();
		for(int i = 0; i < args.size(); i++)
		{
			m_args.add(args.get(i));
		}
		return this;
	}
	

	@Override
	public TYPE Type() {
		return GIScriptExpression.TYPE.set;
	}

	@Override
	public GIScriptExpression Clone() {
		try 
		{
			GIScriptExpressionSet cloned = (GIScriptExpressionSet) this.clone();
			cloned.m_args = new ArrayList<GIScriptExpression>();
			return cloned;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public GIScriptExpression eval() {
		return this;
	}

}
