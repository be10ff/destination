package ru.tcgeo.gilib.script;

import java.util.ArrayList;

public class GIScriptExpressionOperation extends GIScriptExpression {
	protected ArrayList<GIScriptExpression> m_args;
	public GIScriptExpressionOperation(String name) {
		super(name);
		m_args = new ArrayList<GIScriptExpression>();
	}
	
	@Override
	public GIScriptExpressionOperation Clone()
	{
		try 
		{
			GIScriptExpressionOperation cloned = (GIScriptExpressionOperation) this.clone();
			cloned.m_args = new ArrayList<GIScriptExpression>();
			return cloned;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
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
	public TYPE Type() 
	{
		return GIScriptExpression.TYPE.operation;
	}

	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) {
		return null;
	}

	@Override
	public GIScriptExpression eval() {
		return this;
	}

}
