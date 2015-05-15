package ru.tcgeo.gilib.script;

import java.util.ArrayList;


public class GIScriptExpressionDefine extends GIScriptExpression 
{
	public GIScriptExpressionDefine(String name) {
		super(name);
	}

	@Override
	public TYPE Type() {
		return GIScriptExpression.TYPE.define;
	}

	@Override
	public GIScriptExpression Clone() {
		return this;
	}

	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) 
	{
		if(args.size() > 1)
		{
			if(args.get(0).Type() != GIScriptExpression.TYPE.block)
			{
				return new GIScriptExpressionError("wrong argument", this);
			}
			GIScriptExpressionBlock header =(GIScriptExpressionBlock) args.get(0);
			if(header.m_args.size() > 1)
			{
				//header
				String name = header.m_args.get(0)._name;
				GIScriptExpressionFunction function = new GIScriptExpressionFunction(name, m_dict);	
				m_dict.ReplaceInParent(name, function);
				//add local variable/parametr to local dict
				for(int i = 1; i < header.m_args.size(); i++)
				{
					String param = header.m_args.get(i)._name;
					m_dict.Add(param, null);
					function.AddLocalVar(header.m_args.get(i));
				}
				//body
				for (int i = 1; i < args.size(); i++)
				{
					function.Append(args.get(i));
				}
			}

		}
		return null;
	}
	@Override
	public GIScriptExpression eval() 
	{
		return this;
	}
}
