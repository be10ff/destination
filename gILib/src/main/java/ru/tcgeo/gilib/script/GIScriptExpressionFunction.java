//package ru.tcgeo.gilib.script;
//
//import java.util.ArrayList;
//
//
//public class GIScriptExpressionFunction extends GIScriptExpression
//{
//	protected ArrayList<GIScriptExpression> m_args;
//	protected ArrayList<GIScriptExpression> m_local_vars;
//	public GIScriptExpressionFunction(String name, GIScriptDict dict) {
//		super(name);
//		m_args = new ArrayList<GIScriptExpression>();
//		m_local_vars = new ArrayList<GIScriptExpression>();
//		m_dict = dict;
//	}
//
//	@Override
//	public GIScriptExpressionFunction Clone()
//	{
//		return this;
//	}
//
//	@Override
//	public TYPE Type() {
//		return GIScriptExpression.TYPE.function;
//	}
//
//	@Override
//	public GIScriptExpression eval()
//	{
//		return this;
//	}
//
//	@Override
//	public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//	{
//		ArrayList<GIScriptExpression> params = new ArrayList<GIScriptExpression>();
//		GIScriptExpression exp = null;
//		if(args.size() != m_local_vars.size())
//		{
//			return new GIScriptExpressionError("Unexcepted/missed argument", this);
//		}
//		for(int i = 0; i < args.size(); i++)
//		{
//			((GIScriptExpressionVar)m_local_vars.get(i)).SetVariableValue(args.get(i).eval());
//		}
//		int c = 0;
//		while(exp == null && c < m_args.size())
//		{
//			exp = m_args.get(c).eval();
//			c++;
//		}
//
//		for(int i = c; i < m_args.size(); i++)
//		{
//			params.add(m_args.get(i));
//		}
//		return exp.apply(params);
//	}
//
//	public void Append(GIScriptExpression arg)
//	{
//		if(arg == null)
//		{
//			return;
//		}
//		m_args.add(arg);
//	}
//	public void AddLocalVar(GIScriptExpression arg)
//	{
//		if(arg == null)
//		{
//			return;
//		}
//		m_local_vars.add(arg);
//	}
//
//}
