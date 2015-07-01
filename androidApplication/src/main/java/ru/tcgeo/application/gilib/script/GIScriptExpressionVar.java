package ru.tcgeo.application.gilib.script;//package ru.tcgeo.gilib.script;
//
//import java.util.ArrayList;
//
//import ru.tcgeo.gilib.script.GIScriptQueue;
//
//public class GIScriptExpressionVar extends GIScriptExpression {
//
//	String m_key;
//	Object m_value;
//	GIScriptDict m_dict;
//	public void SetVariableValue(GIScriptExpression value)
//	{
//		m_dict.ReplaceInParent(m_key, value);
//	}
//	public GIScriptExpressionVar(String text, GIScriptDict dict) {
//		super(text);
//		m_dict = dict;
//		m_key = text;
//	}
//	public GIScriptExpressionVar(GIScriptQueue text, GIScriptDict dict) {
//		super("variable");
//		String literal = "";
//		m_dict = dict;
//		literal += text.Pop();
//		char current = text.Look();
//		while(Character.isLetter(current)||GIScriptQueue.IsVal(current))
//		{
//			literal += text.Pop();
//			current = text.Look();
//		}
//		m_key = literal;
//
//		GIScriptExpression looking_for = dict.Find(m_key);
//		if(looking_for.Type() == GIScriptExpression.TYPE.variable)
//		{
//			m_value = dict.Find(m_key).apply(new ArrayList<GIScriptExpression>());
//		}
//		else
//		{
//			m_value = null;
//		}
//	}
//	@Override
//	public TYPE Type() {
//
//		return GIScriptExpression.TYPE.variable;
//	}
//	@Override
//	public GIScriptExpression Clone() {
//		return this;
//	}
//	@Override
//	public GIScriptExpression apply(ArrayList<GIScriptExpression> args) {
//		if(args.size() > 0)
//		{
//			return new GIScriptExpressionError("Unexcepted/missed argument", this);
//		}
//
//		return this;
//	}
//	@Override
//	public GIScriptExpression eval() {
//		GIScriptExpression res =  m_dict.Find(m_key);
//		return res;
//	}
//
//}
