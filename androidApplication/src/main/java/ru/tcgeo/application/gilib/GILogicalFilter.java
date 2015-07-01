package ru.tcgeo.application.gilib;

import ru.tcgeo.gilib.*;
import ru.tcgeo.gilib.GIFilter;

public class GILogicalFilter extends ru.tcgeo.gilib.GIFilter
{
	enum ACTION
	{
		AND,
		OR,
		NOT
	}
	// TODO Refactor to polymorph

	ru.tcgeo.gilib.GIFilter m_arg1;
	ru.tcgeo.gilib.GIFilter m_arg2;
	ACTION m_action;

	@Override
	public boolean Check ()
	{
		switch(m_action)
		{
    		case AND: return m_arg1.Check() && m_arg2.Check();
    		case  OR: return m_arg1.Check() || m_arg2.Check();
    		case NOT: return !m_arg1.Check();
    		default:  return false;
    	}
	}

	private GILogicalFilter (ru.tcgeo.gilib.GIFilter arg1, ru.tcgeo.gilib.GIFilter arg2, ACTION action)
	{
		m_arg1 = arg1;
		m_arg2 = arg2;
		m_action = action;
	}

	private GILogicalFilter (ru.tcgeo.gilib.GIFilter arg1, ACTION action)
	{
		m_arg1 = arg1;
		m_action = action;
	}

	public static ru.tcgeo.gilib.GILogicalFilter And (ru.tcgeo.gilib.GIFilter f1, ru.tcgeo.gilib.GIFilter f2)
	{
		return new ru.tcgeo.gilib.GILogicalFilter(f1, f2, ACTION.AND);
	}

	public static ru.tcgeo.gilib.GILogicalFilter Or (ru.tcgeo.gilib.GIFilter f1, ru.tcgeo.gilib.GIFilter f2)
	{
		return new ru.tcgeo.gilib.GILogicalFilter(f1, f2, ACTION.OR);
	}

	public static ru.tcgeo.gilib.GILogicalFilter Not (GIFilter f1)
	{
		return new ru.tcgeo.gilib.GILogicalFilter(f1, ACTION.NOT);
	}

}
