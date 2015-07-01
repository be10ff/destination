//package ru.tcgeo.gilib.script;
//
//import java.util.ArrayList;
//
//import ru.tcgeo.gilib.script.GIScriptQueue;
//
//public class GIScriptExpressionLiteral extends GIScriptExpression {
//
//	String _literal;
//	public GIScriptExpressionLiteral(GIScriptQueue text) {
//		super("literal");
//		_literal = "";
//		while(text.Look() != '\"')
//		{
//			_literal += text.Pop();
//		}
//		text.Pop();
//	}
//	@Override
//	public boolean Equal(GIScriptExpression compare_to)
//	{
//		if(compare_to.Type() != GIScriptExpression.TYPE.literal)
//		{
//			return false;
//		}
//		GIScriptExpressionLiteral compare = (GIScriptExpressionLiteral) compare_to;
//		if(_literal.equalsIgnoreCase(compare.getValue()))
//		{
//			return true;
//		}
//		return false;
//	}
//	@Override
//	public String ToString()
//	{
//		return (_literal);
//	}
//
//	public String getValue()
//	{
//		return _literal;
//	}
//
//	public GIScriptExpressionLiteral(String text) {
//		super(text);
//		_literal = text;
//	}
//
//	@Override
//	public TYPE Type() {
//
//		return GIScriptExpression.TYPE.literal;
//	}
//
//	@Override
//	public GIScriptExpression Clone() {
//		return this;
//	}
//
//	@Override
//	public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//	{
//		if(args.size() > 0)
//		{
//			return new GIScriptExpressionError("Unexcepted/missed argument", this);
//		}
//		return this;
//	}
//
//	@Override
//	public GIScriptExpression eval() {
//		return this;
//	}
//}
