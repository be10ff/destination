//package ru.tcgeo.application.gilib;
//
//
//
//public class GILogicalFilter extends GIFilter
//{
//	enum ACTION
//	{
//		AND,
//		OR,
//		NOT
//	}
//	// TODO Refactor to polymorph
//
//	GIFilter m_arg1;
//	GIFilter m_arg2;
//	ACTION m_action;
//
//	@Override
//	public boolean Check ()
//	{
//		switch(m_action)
//		{
//    		case AND: return m_arg1.Check() && m_arg2.Check();
//    		case  OR: return m_arg1.Check() || m_arg2.Check();
//    		case NOT: return !m_arg1.Check();
//    		default:  return false;
//    	}
//	}
//
//	private GILogicalFilter (GIFilter arg1, GIFilter arg2, ACTION action)
//	{
//		m_arg1 = arg1;
//		m_arg2 = arg2;
//		m_action = action;
//	}
//
//	private GILogicalFilter (GIFilter arg1, ACTION action)
//	{
//		m_arg1 = arg1;
//		m_action = action;
//	}
//
//	public static GILogicalFilter And (GIFilter f1, GIFilter f2)
//	{
//		return new GILogicalFilter(f1, f2, ACTION.AND);
//	}
//
//	public static GILogicalFilter Or (GIFilter f1, GIFilter f2)
//	{
//		return new GILogicalFilter(f1, f2, ACTION.OR);
//	}
//
//	public static GILogicalFilter Not (GIFilter f1)
//	{
//		return new GILogicalFilter(f1, ACTION.NOT);
//	}
//
//}
