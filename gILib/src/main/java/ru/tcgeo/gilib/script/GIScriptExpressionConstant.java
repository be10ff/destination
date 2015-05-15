package ru.tcgeo.gilib.script;

import java.util.ArrayList;

public class GIScriptExpressionConstant extends GIScriptExpression {

	protected Object _value;
	//protected Object _name;
	public GIScriptExpressionConstant(String name, Object value) {
		super(name);
		_value = value;
	}
	@Override
	public TYPE Type() {
		return GIScriptExpression.TYPE.constant;
	}

	public boolean equal(GIScriptExpressionConstant compare_to)
	{
		if(_value == (compare_to.getValue()))
		{
			return true;
		}
		return false;
	}
	public Object getValue()
	{
		return _value;
	}
	@Override
	public String ToString()
	{
		return (_value.toString());
	}
	@Override
	public GIScriptExpression Clone() {
		return this;
	}

	@Override
	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) {
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
