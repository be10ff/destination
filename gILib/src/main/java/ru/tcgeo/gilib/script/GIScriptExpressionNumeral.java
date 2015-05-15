package ru.tcgeo.gilib.script;

import java.util.ArrayList;

import ru.tcgeo.gilib.script.GIScriptQueue;

public class GIScriptExpressionNumeral extends GIScriptExpression {
	double _double_value; 
	public GIScriptExpressionNumeral(double value) {
		super("numeral");
		_double_value = value;
	}

	public GIScriptExpressionNumeral(GIScriptQueue text) {
		super("numeral");
		String literal = "";
		literal += text.Pop();
		char current = text.Look();
		while(Character.isDigit(current)||current == '.')
		{
			literal += text.Pop();
			current = text.Look();
		}
		_double_value = Double.valueOf(literal);
	}
	public double getValue()
	{
		return _double_value;
	}
	@Override
	public String ToString()
	{
		return ( "" + _double_value);
	}
	@Override
	public boolean Equal(GIScriptExpression compare_to)
	{
		if(compare_to.Type() != GIScriptExpression.TYPE.numeral)
		{
			return false;
		}
		GIScriptExpressionNumeral compare = (GIScriptExpressionNumeral) compare_to;
		if(_double_value == (compare.getValue()))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public TYPE Type() {
		return GIScriptExpression.TYPE.numeral;
	}

	@Override
	public GIScriptExpression Clone() {
		return this;
	}

	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) 
	{
		if(args.size() > 0)
		{
			return new GIScriptExpressionError("Unexcepted/missed argument", this);
		}
		return this;
	}

	@Override
	public GIScriptExpression eval() {
		return this;
	}


}
