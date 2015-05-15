package ru.tcgeo.gilib.script;

import java.util.ArrayList;

public abstract class GIScriptExpression  implements Cloneable{

	protected String _name;
	protected GIScriptDict m_dict;
	//protected Object m_value;
	public static enum TYPE {literal, numeral, variable, function, operation, constant, define, block, set, error, lonlat, layer, objects, semantic, request, marker, markers};
	abstract public GIScriptExpression apply(ArrayList<GIScriptExpression> args);
	abstract public TYPE Type();
	abstract public GIScriptExpression Clone();
	abstract public GIScriptExpression eval();
	
	public String ToString()
	{
		return ("");
	}
	
	public boolean Equal(GIScriptExpression c)
	{
		return (this == c);
	}

	GIScriptExpression(String name)
	{
		_name = name;
	}

	public String toString()
	{
		/*if((Type() == GIScriptExpression.TYPE.literal) 
				|| (Type() == GIScriptExpression.TYPE.numeral) 
				|| (Type() == GIScriptExpression.TYPE.constant) 
				|| (Type() == GIScriptExpression.TYPE.set)
				{
					return 
				}*/
		return _name;
	}
}
