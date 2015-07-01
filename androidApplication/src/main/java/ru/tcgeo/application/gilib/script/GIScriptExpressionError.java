package ru.tcgeo.application.gilib.script;//package ru.tcgeo.gilib.script;
//
//import java.util.ArrayList;
//
//public class GIScriptExpressionError extends GIScriptExpression {
//	public String error_text;
//	public GIScriptExpression where;
//
//	public GIScriptExpressionError() {
//		super("Error");
//	}
//
//	public GIScriptExpressionError(String name) {
//		super(name);
//		// TODO Auto-generated constructor stub
//	}
//
//	public GIScriptExpressionError(String msg, GIScriptExpression place) {
//		super("Error");
//		error_text = msg;
//		where = place;
//	}
//	public static GIScriptExpressionError append(GIScriptExpression place, ArrayList<GIScriptExpression> args)
//	{
//		for(int i = 0; i < args.size(); i++)
//		{
//			if(args.get(i) == null)
//			{
//				return new GIScriptExpressionError("Null arg", place);
//			}
//			if(args.get(i).Type() == GIScriptExpression.TYPE.error)
//			{
//				GIScriptExpressionError that = (GIScriptExpressionError)args.get(i);
//				that.error_text += "\n\r" + place.ToString();
//
//				return that;
//			}
//
//		}
//		return null;
//	}
//
//	@Override
//	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) {
//		// TODO Auto-generated method stub
//		return this;
//	}
//
//	@Override
//	public TYPE Type() {
//		// TODO Auto-generated method stub
//		return GIScriptExpression.TYPE.error;
//	}
//
//	@Override
//	public GIScriptExpression Clone() {
//		// TODO Auto-generated method stub
//		return this;
//	}
//
//	@Override
//	public GIScriptExpression eval() {
//		// TODO Auto-generated method stub
//		return this;
//	}
//
//}
