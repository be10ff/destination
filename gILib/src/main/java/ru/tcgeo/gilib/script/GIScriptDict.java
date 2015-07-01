//package ru.tcgeo.gilib.script;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//import ru.tcgeo.gilib.AddressSearchAdapterItem;
//import ru.tcgeo.gilib.GIDataRequestorImp;
//import ru.tcgeo.gilib.GILayer;
//import ru.tcgeo.gilib.GILonLat;
//import ru.tcgeo.gilib.GISQLRequest;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.Toast;
//
//public class GIScriptDict {
//	private Map<String, GIScriptExpression> m_dist;
//	private GIScriptDict m_Parent;
//	public GIDataRequestorImp m_requestor;
//	public GISQLRequest m_sql;
//	public final String LOG_TAG = "ScriptLogs";
//	//LOG_TAG = "myLogs";
//    public String getCoordString(double coord)
//    {
//    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
//    	int mins = (int)Math.floor((coord - degrees)*60);
//    	double secs = ((coord - degrees)*60-mins)*60;
//
//    	return String.format(Locale.ENGLISH, "%d° %d\' %.4f\"", degrees, mins, secs);
//    }
//	GIScriptDict(GIScriptDict parent)
//	{
//		m_Parent = parent;
//		m_dist = new HashMap<String, GIScriptExpression>();
//	}
//
//	public GIScriptExpression Find(String key)
//	{
//		if(m_dist.get(key) != null)
//		{
//			return m_dist.get(key);
//		}
//		else
//		{
//			if(m_Parent != null)
//			{
//				return m_Parent.Find(key);
//			}
//			else
//			{
//				return null;
//			}
//		}
//	}
//	public GIScriptExpression AddToParent(String key, GIScriptExpression exp)
//	{
//		if(m_Parent != null)
//		{
//			GIScriptExpression old = m_Parent.Add(key, exp);
//			return old;
//		}
//		return null;
//	}
//	public GIScriptExpression Add(String key, GIScriptExpression exp)
//	{
//		GIScriptExpression old = Find(key);
//		if(old == null)
//		{
//			old = m_dist.put(key, exp);
//			return old;
//		}
//		return  old;
//	}
//	public GIScriptExpression Replace(String key, GIScriptExpression exp)
//	{
//		GIScriptExpression old = m_dist.put(key, exp);
//		return old;
//	}
//
//	public GIScriptExpression ReplaceInParent(String key, GIScriptExpression exp)
//	{
//		if(m_Parent != null)
//		{
//			GIScriptExpression old = m_Parent.Replace(key, exp);
//			return old;
//		}
//
//		return null;
//	}
//
//	GIScriptDict()  //
//	{
//		m_Parent = null;
//		m_dist = new HashMap<String, GIScriptExpression>();
//
////common-----------------------------------------------------------------------------------------------------------------------
//		// ----------------- define --------------
//		m_dist.put("define", new GIScriptExpressionDefine("define"){});
//		// ----------------- if --------------
//		m_dist.put("if", new GIScriptExpressionOperation("if")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 3)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression condition = args.get(0).eval();
//				if(condition == Find("true"))
//				{
//					GIScriptExpression res = args.get(1).eval();
//					return res;
//				}
//				else if(condition == Find("false"))
//				{
//					GIScriptExpression res = args.get(2).eval();
//					return res;
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//		});
//		//------ Command Do ----------------
//		m_dist.put("do", new GIScriptExpressionOperation("do")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				GIScriptExpression res = null;
//				for(int i = 0; i < args.size(); i++)
//				{
//					res = args.get(i).eval();
//				}
//				return res;
//			}
//		});
//		//------ operator numeral = numeral ----------------
//		m_dist.put("=", new GIScriptExpressionOperation("=")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() < 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				if(args.size() == 1)
//				{
//					return Find("true");
//				}
//				for(int i = 1; i < args.size(); i++)
//				{
//					GIScriptExpression leftexp = args.get(i-1).eval();
//					GIScriptExpression rightexp = args.get(i).eval();
//					if(leftexp.Type() != GIScriptExpression.TYPE.numeral)
//					{
//						return new GIScriptExpressionError("Wrong arg type", this);
//					}
//					if(rightexp.Type() != GIScriptExpression.TYPE.numeral)
//					{
//						return new GIScriptExpressionError("Wrong arg type", this);
//					}
//					double left = ((GIScriptExpressionNumeral)leftexp).getValue();
//					double right = ((GIScriptExpressionNumeral)rightexp).getValue();
//					if(!(left == right))
//					{
//						return Find("false");
//					}
//				}
//				return Find("true");
//			}
//		});
////boolean-----------------------------------------------------------------------------------------------------------------------------
//		m_dist.put("nil", new GIScriptExpressionConstant("nil", null){});
//		//m_dist.put("void", new GIScriptExpressionConstant("void", null){});
//		m_dist.put("true", new GIScriptExpressionConstant("true", true){});
//		m_dist.put("false", new GIScriptExpressionConstant("false", false){});
//		// ----------------- and --------------
//		m_dist.put("and", new GIScriptExpressionOperation("and")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() < 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//
//				for(int i = 0; i < args.size(); i++)
//				{
//					GIScriptExpression res = args.get(i).eval();
//					if(res == Find("false"))
//					{
//						return res;
//					}
//				}
//				return Find("true");
//			}
//		});
//		// ----------------- or --------------
//		m_dist.put("or", new GIScriptExpressionOperation("or")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() < 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				for(int i = 0; i < args.size(); i++)
//				{
//					GIScriptExpression res = args.get(i).eval();
//					if(res == Find("true"))
//					{
//						return res;//true;
//					}
//				}
//				return Find("false");
//			}
//		});
//		// ----------------- not --------------
//		m_dist.put("not", new GIScriptExpressionOperation("not")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args) {
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression res = args.get(0).eval();
//				if(res == Find("true"))
//				{
//					return Find("false");
//				}
//				else if (res == Find("false"))
//				{
//					return Find("true");
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//		});
////STRING-------------------------------------------------------------------------------------------------------------------------------
//		//----------- string-empty? -----------
//		m_dist.put("string-empty?", new GIScriptExpressionOperation("string-empty?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression append = (GIScriptExpression) args.get(0).eval();
//				if(append.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) append;
//				String val = str.getValue();
//				if(val.length() == 0)
//				{
//					return Find("true");
//				}
//				else
//				{
//					return Find("false");
//				}
//			}
//		});
//		// ----------------- string-equals? --------------
//		m_dist.put("string-equals?", new GIScriptExpressionOperation("string-equals?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression append = (GIScriptExpression) args.get(0).eval();
//				if(append.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral compare_to = (GIScriptExpressionLiteral) append;
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) args.get(1).eval();
//				if(str.Equal(compare_to))
//				{
//					return Find("true");
//				}
//				else
//				{
//					return Find("false");
//				}
//			}
//		});
//		// ----------------- string-append --------------
//		m_dist.put("string-append", new GIScriptExpressionOperation("string-append")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				GIScriptExpression append = (GIScriptExpression) args.get(0).eval();
//				if(append.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral append_to = (GIScriptExpressionLiteral) append;
//				String res = append_to.ToString();
//				for(int i = 1; i < args.size(); i++)
//				{
//					GIScriptExpression app = args.get(i).eval();
//
//					String app_str = app.ToString();
//
//					res = res + app_str;
//				}
//				return new GIScriptExpressionLiteral(res);
//			}
//		});
//		// ----------------- string-empty? --------------
//		m_dist.put("string-empty?", new GIScriptExpressionOperation("string-empty?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression stro = (GIScriptExpression) args.get(0).eval();
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				if(str.getValue().length() == 0)
//				{
//					return Find("true");
//				}
//				else
//				{
//					return Find("false");
//				}
//			}
//		});
//		// ----------------- string-to-lower --------------
//		m_dist.put("string-to-lower", new GIScriptExpressionOperation("string-to-lower")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression stro = (GIScriptExpression) args.get(0).eval();
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				GIScriptExpressionLiteral res = new GIScriptExpressionLiteral(str._literal.toLowerCase(Locale.ENGLISH));
//				return res;
//			}
//		});
//
////SET----------------------------------------------------------------------------------------------------------------------------
//		// ----------------- command SET --------------------
//		m_dist.put("set", new GIScriptExpressionSet("set"){});
//		// ----------------- set-any? --------------
//		m_dist.put("set-any?", new GIScriptExpressionOperation("set-any?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression array_expression = args.get(0).eval();
//				if(array_expression.Type() != GIScriptExpression.TYPE.set)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionSet array_exp = (GIScriptExpressionSet)array_expression;
//				ArrayList<GIScriptExpression> arr = array_exp.getValue();
//				GIScriptExpression func_expression = args.get(1).eval();
//				if(func_expression.Type() != GIScriptExpression.TYPE.function && func_expression.Type() != GIScriptExpression.TYPE.operation)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpression func = (GIScriptExpression)func_expression;
//				for(int i = 0; i < arr.size(); i++)
//				{
//					ArrayList<GIScriptExpression> arg = new ArrayList<GIScriptExpression>();
//					arg.add(arr.get(i));
//					GIScriptExpression res = func.apply(arg);
//					if(res == Find("true"))
//					{
//						return Find("true");
//					}
//				}
//				return Find("false");
//			}
//		});
//		// ----------------- CommandSetAggregate --------------
//		m_dist.put("set-aggregate", new GIScriptExpressionOperation("set-aggregate")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 3)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpressionSet array_exp = (GIScriptExpressionSet)args.get(0).eval();
//				ArrayList<GIScriptExpression> arr = array_exp.getValue();
//				GIScriptExpression func = args.get(2).eval();
//				GIScriptExpression grain = args.get(1).eval();
//				for(int i = 0; i < arr.size(); i++)
//				{
//					GIScriptExpression param = arr.get(i).eval();
//					ArrayList<GIScriptExpression> params = new ArrayList<GIScriptExpression>();
//					params.add(grain);
//					params.add(param);
//					grain = func.apply(params);
//				}
//				return grain;
//			}
//		});
//		// ----------------- CommandSetAnything --------------
//		m_dist.put("set-anything", new GIScriptExpressionOperation("set-anything")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpressionSet set = (GIScriptExpressionSet)args.get(0).eval();
//				ArrayList<GIScriptExpression> arr = set.getValue();
//				if(arr.size() < 1)
//				{
//					return new GIScriptExpressionError("set empty", this);
//				}
//				return arr.get(0);
//			}
//		});
//		// ----------------- set-empty --------------
//		m_dist.put("set-empty", new GIScriptExpressionOperation("set-empty")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() > 0)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				return new GIScriptExpressionSet("set");
//			}
//		});
//		// ----------------- CommandSetEmpty? --------------
//		m_dist.put("set-empty?", new GIScriptExpressionOperation("set-empty?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression param = args.get(0).eval();
//				if(param.Type() != GIScriptExpression.TYPE.set)
//				{
//					return new GIScriptExpressionError("Wrong argument type", this);
//				}
//				GIScriptExpressionSet set = (GIScriptExpressionSet) param;
//				ArrayList<GIScriptExpression> arr =  set.getValue();
//				if(arr.size() == 0)
//				{
//					return Find("true");
//				}
//				else
//				{
//					return Find("false");
//				}
//			}
//		});
//		// ----------------- set-append-elem --------------
//		m_dist.put("set-append-elem", new GIScriptExpressionOperation("set-append-elem")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression param = args.get(0).eval();
//				if(param.Type() != GIScriptExpression.TYPE.set)
//				{
//					return new GIScriptExpressionError("Wrong argument type", this);
//				}
//				ArrayList<GIScriptExpression> arr = ((GIScriptExpressionSet)param).getValue();
//				arr.add(args.get(1).eval());
//				return param;
//			}
//		});
//		// ----------------- set-count --------------
//		m_dist.put("set-count", new GIScriptExpressionOperation("set-count")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression param = args.get(0).eval();
//				if(param.Type() != GIScriptExpression.TYPE.set)
//				{
//					return new GIScriptExpressionError("Wrong argument type", this);
//				}
//				ArrayList<GIScriptExpression> arr = ((GIScriptExpressionSet)param).getValue();
//				GIScriptExpressionNumeral res = new GIScriptExpressionNumeral((double)arr.size());
//				return res;
//			}
//		});
//		// ----------------- set-contains --------------
//		m_dist.put("set-contains?", new GIScriptExpressionOperation("set-contains?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression param = args.get(0).eval();
//				GIScriptExpressionSet set = (GIScriptExpressionSet)param;
//				ArrayList<GIScriptExpression> arr = set.getValue();
//				GIScriptExpression compare_to = args.get(1).eval();
//				for(int i = 0; i < arr.size(); i++)
//				{
//					GIScriptExpression curr =  arr.get(i).eval();
//					if(curr.Type() != compare_to.Type())
//					{
//						return Find("false");
//					}
//					if(curr.Equal(compare_to))
//					{
//						return Find("true");
//					}
//				}
//				return Find("false");
//			}
//		});
////Globals----------------------------------------------------------------------------------------------------------------------------------
//		// ----------------- bubble-pin --------------
//		m_dist.put("bubble-pin", new GIScriptExpressionOperation("bubble-pin")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression caption_exp =  args.get(0).eval();
//				if(caption_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					caption_exp = new GIScriptExpressionLiteral(caption_exp.ToString());
//				}
//				GIScriptExpressionLiteral caption = (GIScriptExpressionLiteral) caption_exp;
//
//				GIScriptExpression body_exp =  args.get(1).eval();
//				if(body_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					body_exp = new GIScriptExpressionLiteral(body_exp.ToString());
//				}
//				GIScriptExpressionLiteral body = (GIScriptExpressionLiteral) body_exp;
//				//ToDo add "body" and "Caption"
//				if(m_requestor != null)
//				{
//					m_requestor.ShowControl(body.getValue(), caption.getValue());
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//		});
//		// ----------------- bubble-image --------------
//		m_dist.put("bubble-image", new GIScriptExpressionOperation("bubble-image")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 3)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression caption_exp =  args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(caption_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					caption_exp = new GIScriptExpressionLiteral(caption_exp.ToString());
//				}
//				GIScriptExpressionLiteral caption = (GIScriptExpressionLiteral) caption_exp;
//				GIScriptExpression body_exp =  args.get(1).eval();//(GIScriptExpressionLiteral)
//				if(body_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					body_exp = new GIScriptExpressionLiteral(body_exp.ToString());
//				}
//				//GIScriptExpressionLiteral body = (GIScriptExpressionLiteral) body_exp;
//				GIScriptExpression contetn_exp =  args.get(2).eval();//(GIScriptExpressionLiteral)
//				if(contetn_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					contetn_exp = new GIScriptExpressionLiteral(contetn_exp.ToString());
//				}
//				//GIScriptExpressionLiteral content = (GIScriptExpressionLiteral) contetn_exp;
//				//ToDo add "body" and "Caption"
//				if(m_requestor != null)
//				{
//					m_requestor.ShowControl( "", caption.getValue());
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//		});
//		// ----------------- bubble-movie --------------
//		m_dist.put("bubble-movie", new GIScriptExpressionOperation("bubble-movie")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 3)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression caption_exp =  args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(caption_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					caption_exp = new GIScriptExpressionLiteral(caption_exp.ToString());
//				}
//				GIScriptExpressionLiteral caption = (GIScriptExpressionLiteral) caption_exp;
//				GIScriptExpression body_exp =  args.get(1).eval();//(GIScriptExpressionLiteral)
//				if(body_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					body_exp = new GIScriptExpressionLiteral(body_exp.ToString());
//				}
//				//GIScriptExpressionLiteral body =  (GIScriptExpressionLiteral)body_exp;
//				GIScriptExpression contetn_exp = args.get(2).eval();//(GIScriptExpressionLiteral)
//				if(contetn_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					//return null;
//					contetn_exp = new GIScriptExpressionLiteral(contetn_exp.ToString());
//				}
//				//GIScriptExpressionLiteral content = (GIScriptExpressionLiteral) contetn_exp;
//				//ToDo add "body" and "Caption"
//				if(m_requestor != null)
//				{
//					m_requestor.ShowControl("", caption.getValue());
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//		});
//		// ----------------- file-local --------------
//		/*tested*/
//		m_dist.put("file-local", new GIScriptExpressionOperation("file-local")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				File dir = (Environment.getExternalStorageDirectory());
//				GIScriptExpression stro =  args.get(0).eval(); //(GIScriptExpressionLiteral)
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				return new GIScriptExpressionLiteral(dir.getAbsolutePath() + "/" + str.getValue());
//			}
//		});
//		// ----------------- path-extension --------------
//		/*tested*/
//		m_dist.put("path-extension", new GIScriptExpressionOperation("path-extension")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				//File dir = (Environment.getExternalStorageDirectory());
//				GIScriptExpression stro =  args.get(0).eval(); //(GIScriptExpressionLiteral)
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				String left = str.getValue();
//				int last_dot = left.lastIndexOf('.');
//
//				if(last_dot > 0 && last_dot < (left.length() - 2))
//				{
//					return new GIScriptExpressionLiteral(left.substring(last_dot + 1));
//				}
//				return new GIScriptExpressionLiteral("");
//			}
//		});
////Globals----------------------------------------------------------------------------------------------------------------------------------
//		// ----------------- request-location --------------
//		/*tested*/
//		m_dist.put("request-location", new GIScriptExpressionConstant("request-location", null)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() > 0)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				if(m_requestor != null)
//				{
//					_value = m_requestor.getRequestPoint();
//				}
//				return this;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.lonlat;
//			}
//			@Override
//			public GIScriptExpression eval()
//			{
//				if(m_requestor != null)
//				{
//					_value = m_requestor.getRequestPoint();
//				}
//				else
//				{
//					return new GIScriptExpressionError("Uninisiliased m_requestor", this);
//				}
//				return this;
//			}
//			@Override
//			public String ToString()
//			{
//				return m_requestor.getRequestPoint().toString();
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//		// ----------------- layer-present? --------------
//		/*tested*/
//		//m_dist.put("layer-present?", new GIScriptExpressionOperation("layer-present?"){});
//		m_dist.put("layer-present?", new GIScriptExpressionOperation("layer-present?")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				//
//				GIScriptExpression stro =  args.get(0).eval(); //(GIScriptExpressionLiteral)
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				String layer_name = str.getValue();
//
//				if(m_requestor == null)
//				{
//					return new GIScriptExpressionError("Uninisilised requestor argument", this);
//				}
//				if(m_requestor.isLayerPresent(layer_name))
//				{
//					Log.d(LOG_TAG, "layer-present? operation for " + layer_name + " True");
//					return Find("true");
//				}
//				else
//				{
//					Log.d(LOG_TAG, "layer-present? operation for " + layer_name + " False");
//					return Find("false");
//				}
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.operation;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public String ToString()
//			{
//				return toString();
//			}
//			@Override
//			public GIScriptExpressionOperation Clone() {
//				return this;
//			}
//		});
//		//m_dist.put("layer-named", new GIScriptExpressionOperation("layer-named"){});
//		/*tested*/
//		// ----------------- layer-named --------------
//		m_dist.put("layer-named", new GIScriptExpressionConstant("layer-named", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression stro =  args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(stro.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral str = (GIScriptExpressionLiteral) stro;
//				String layer_name = str.getValue();
//				//_name = layer_name;
//				//
//				if(m_requestor != null)
//				{
//					_value = m_requestor.LayerByName(layer_name);
//				}
//				else
//				{
//					return new GIScriptExpressionError("Uninisilised requestor argument", this);
//				}
//				Log.d(LOG_TAG, "layer-named const for " + layer_name);
//				return this;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.layer;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public String ToString()
//			{
//				return ((GILayer)_value).getName();//(String)_name;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//		//m_dist.put("layer-name", new GIScriptExpressionOperation("layer-name"){});
//		// ----------------- layer-named --------------
//		/*tested*/
//		m_dist.put("layer-name", new GIScriptExpressionConstant("layer-name", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression layer_exp =  args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(layer_exp.Type() != GIScriptExpression.TYPE.layer)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant layer = (GIScriptExpressionConstant)layer_exp;
//				GIDataRequestorImp.Layer lay = (GIDataRequestorImp.Layer) layer.getValue();
//				//
//				_value = lay.m_layer.getName();
//				Log.d(LOG_TAG, "layer-name const returned  " + _value);
//				return new GIScriptExpressionLiteral(lay.m_layer.getName());
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.layer;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public String ToString()
//			{
//				return  (String) _value;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//
//		//m_dist.put("layer-objects", new GIScriptExpressionOperation("layer-objects"){});
//		// ----------------- layer-objects --------------
//		/*tested*/
//		m_dist.put("layer-objects", new GIScriptExpressionConstant("layer-objects", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression layer_exp =  args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(layer_exp.Type() != GIScriptExpression.TYPE.layer)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant layer = (GIScriptExpressionConstant)layer_exp;
//				GIDataRequestorImp.Layer lay = (GIDataRequestorImp.Layer)layer.getValue();
//
//				ArrayList<GIScriptExpression> array = new ArrayList<GIScriptExpression>();
//				for(int i = 0; i < lay.m_geometries.size(); i++)
//				{
//					GIScriptExpressionConstant obj = new GIScriptExpressionConstant("geometry", lay.m_geometries.get(i));
//					array.add(obj);
//				}
//				GIScriptExpressionSet res = new GIScriptExpressionSet("objects");
//				res.m_args = array;
//				//Log.d(LOG_TAG, "layer-objects const returned  " + array.size() + " items array" );
//				return res;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.objects;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			/*@Override
//			public String ToString()
//			{
//				return  (String) _value;
//			}*/
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//		// ----------------- object-semantic --------------
//		/*tested*/
//		//m_dist.put("object-semantic", new GIScriptExpressionOperation("object-semantic"){});
//		m_dist.put("object-semantic", new GIScriptExpressionOperation("object-semantic")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression figure_exp = args.get(0).eval();//(GIScriptExpressionLiteral)
//				if(figure_exp.Type() != GIScriptExpression.TYPE.constant)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant figure = (GIScriptExpressionConstant)figure_exp;
//				GIDataRequestorImp.Feature feature = (GIDataRequestorImp.Feature)figure.getValue();
//				/**/
//
//				GIScriptExpression field_exp =  args.get(1).eval();//(GIScriptExpressionLiteral)
//				if(field_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral field = (GIScriptExpressionLiteral) field_exp;
//				String field_name = field.getValue();
//
//				for(int i = 0; i < feature.m_pairs.size(); i++)
//				{
//					if(feature.m_pairs.get(i).m_name.equalsIgnoreCase(field_name))
//					{
//						Log.d(LOG_TAG, "object-semantic const for field " + field_name + " returned " + feature.m_pairs.get(i).m_value);
//						return new GIScriptExpressionConstant(field_name, feature.m_pairs.get(i).m_value);
//					}
//				}
//				return new GIScriptExpressionError("Unknown error", this);
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.semantic;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//
//			@Override
//			public GIScriptExpressionOperation Clone() {
//				return this;
//			}
//		});
//		//m_dist.put("semantic-string", new GIScriptExpressionOperation("semantic-string"){});
//		// ----------------- object-semantic --------------
//		m_dist.put("semantic-string", new GIScriptExpressionOperation("semantic-string")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				/**/
//				GIScriptExpression field_exp =  args.get(0).eval();
//				if(field_exp.Type() != GIScriptExpression.TYPE.constant)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant field = (GIScriptExpressionConstant) field_exp;
//				String semantic = (String) field.getValue();
//				Log.d(LOG_TAG, "semantic-string const for " + semantic);
//				return new GIScriptExpressionLiteral(semantic);
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.semantic;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//
//			@Override
//			public GIScriptExpressionOperation Clone() {
//				return this;
//			}
//		});
//		//m_dist.put("location->string", new GIScriptExpressionOperation("location->string"){});
//		// ----------------- location->string --------------
//		m_dist.put("location->string", new GIScriptExpressionOperation("location->string")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//
//				GIScriptExpression lonlat_exp =  args.get(0).eval();
//				if(lonlat_exp.Type() != GIScriptExpression.TYPE.lonlat)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant lonlat = (GIScriptExpressionConstant) lonlat_exp;
//				GILonLat point = (GILonLat) lonlat.getValue();
//
//				String res = String.format(Locale.ENGLISH, "(%.0f : %.0f)", point.lon(), point.lat());
//				Log.d(LOG_TAG, "location->string const returned " + res);
//				return new GIScriptExpressionLiteral(res);
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.semantic;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//
//			@Override
//			public GIScriptExpressionOperation Clone() {
//				return this;
//			}
//		});
//		// ----------------- log --------------
//		m_dist.put("log", new GIScriptExpressionOperation("log")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression text_exp =  args.get(0).eval();
//				if(text_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					text_exp = new GIScriptExpressionLiteral(text_exp.ToString());
//				}
//				GIScriptExpressionLiteral text = (GIScriptExpressionLiteral) text_exp;
//				String out_text = text._literal;
//				Log.v(LOG_TAG, out_text);
//				Log.d(LOG_TAG, "log operation with " + out_text + " arg");
//				return null;
//			}
//		});
//		// ----------------- alert --------------
//		m_dist.put("alert", new GIScriptExpressionOperation("alert")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() != 2)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression caption_exp =  args.get(0).eval();
//				if(caption_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					caption_exp = new GIScriptExpressionLiteral(caption_exp.ToString());
//				}
//				GIScriptExpressionLiteral caption = (GIScriptExpressionLiteral) caption_exp;
//				/**/
//				GIScriptExpression text_exp =  args.get(0).eval();
//				if(text_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					text_exp = new GIScriptExpressionLiteral(text_exp.ToString());
//				}
//				GIScriptExpressionLiteral text = (GIScriptExpressionLiteral) text_exp;
//
//				String out_text = text._literal;
//				String out_caption = caption._literal;
//				Toast.makeText(m_requestor.m_map.getContext(), out_caption + System.getProperty("line.separator") + out_text, Toast.LENGTH_LONG).show();
//				Log.d(LOG_TAG, "alert operation with " + out_text + " arg");
//				return null;
//			}
//		});
//
////SQL----------------------------------------------------------------------------------------------------------------------------------
//		// ----------------- enlist --------------
//		m_dist.put("enlist", new GIScriptExpressionOperation("enlist")
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression set_exp =  args.get(0).eval();
//				if(set_exp.Type() != GIScriptExpression.TYPE.set)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionSet set_of_markers = (GIScriptExpressionSet)set_exp;
//				if(m_sql != null)
//				{
//					for(int i = 0; i < set_of_markers.m_args.size(); i++)
//					{
//						GIScriptExpressionConstant item = (GIScriptExpressionConstant)set_of_markers.m_args.get(i).eval();
//						m_sql.m_array.add((AddressSearchAdapterItem) item._value);
//					}
//				}
//				else
//				{
//					return new GIScriptExpressionError("Uninitiliased m_sql", this);
//				}
//				return this;
//			}
//		});
//
//		// ----------------- request --------------
//
//		m_dist.put("request", new GIScriptExpressionConstant("request", null)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size() > 0)
//				{
//					return new GIScriptExpressionError("Unexcepted argument", this);
//				}
//				if(m_sql != null)
//				{
//					_value = new GISQLRequest(m_sql.m_text, m_sql.m_mode, m_sql.m_path, null);
//				}
//				else
//				{
//					return new GIScriptExpressionError("Uninitiliased m_sql", this);
//				}
//				return this;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.request;
//			}
//			@Override
//			public GIScriptExpression eval()
//			{
//				return this;
//			}
//			@Override
//			public String ToString()
//			{
//				return ((GISQLRequest)_value).m_text;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//			public Object getValue()
//			{
//				if(m_sql != null)
//				{
//					return _value = new GISQLRequest(m_sql.m_text, m_sql.m_mode, m_sql.m_path, null);
//				}
//				else
//				{
//					return new GIScriptExpressionError("Uninitiliased m_sql", this);
//				}
//			}
//		});
//		// ----------------- request->string --------------
//		m_dist.put("request-string", new GIScriptExpressionConstant("request-string", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression request_exp =  args.get(0).eval();
//				if(request_exp.Type() != GIScriptExpression.TYPE.request)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant request = (GIScriptExpressionConstant) request_exp;
//				GISQLRequest sql_req = (GISQLRequest) request.getValue();
//
//				return new GIScriptExpressionLiteral(sql_req.m_text);
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.literal;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//		// ----------------- request->mode --------------
//		m_dist.put("request->mode", new GIScriptExpressionConstant("request->mode", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression request_exp =  args.get(0).eval();
//				if(request_exp.Type() != GIScriptExpression.TYPE.request)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionConstant request = (GIScriptExpressionConstant) request_exp;
//				GISQLRequest sql_req = (GISQLRequest) request.getValue();
//
//				return new GIScriptExpressionLiteral(sql_req.m_mode);
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.literal;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//		// ----------------- marker-ellipsis --------------		marker-ellipsis
//		m_dist.put("marker-ellipsis", new GIScriptExpressionConstant("marker-ellipsis", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  > 0)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				_value = new AddressSearchAdapterItem("...", 0, 0, 0);
//				return this;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.marker;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				_value = new AddressSearchAdapterItem("...", 0, 0, 0);
//				return this;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//
//		// ----------------- database-query --------------
//		m_dist.put("database-query", new GIScriptExpressionConstant("database-query", this)
//		{
//			@Override
//			public GIScriptExpression apply(ArrayList<GIScriptExpression> args)
//			{
//				if(GIScriptExpressionError.append(this, args) != null)
//				{
//					return GIScriptExpressionError.append(this, args);
//				}
//				if(args.size()  != 1)
//				{
//					return new GIScriptExpressionError("Unexcepted/missed argument", this);
//				}
//				GIScriptExpression sql_exp =  args.get(0).eval();
//				if(sql_exp.Type() != GIScriptExpression.TYPE.literal)
//				{
//					return new GIScriptExpressionError("Wrong arg type", this);
//				}
//				GIScriptExpressionLiteral sql = (GIScriptExpressionLiteral)sql_exp;
//				String sql_string = sql.getValue();
//				//String b = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_sql.m_path;
//				SQLiteDatabase db;
//				Cursor c;
//				GIScriptExpressionSet res = new GIScriptExpressionSet("set of markers");
//				try
//				{
//					db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_sql.m_path, null, SQLiteDatabase.OPEN_READONLY);
//					c = db.rawQuery(sql_string, null);
//				    if (c.moveToFirst())
//				    {
//				        while ( !c.isAfterLast() )
//				        {
//				           String tab_name = c.getString(0);
//				           double lon = c.getDouble(1);
//				           double lat = c.getDouble(2);
//				           double diag = c.getDouble(3);
//				           AddressSearchAdapterItem item = new AddressSearchAdapterItem(tab_name, lon, lat, diag);
//				           GIScriptExpressionConstant marker_exp = new GIScriptExpressionConstant("marker", (Object)item)
//				           {
//					   			@Override
//								public TYPE Type()
//					   			{
//									return GIScriptExpression.TYPE.marker;
//								}
//				           };
//				           res.m_args.add(marker_exp);
//				           c.moveToNext();
//				        }
//				    }
//				}
//				catch(Exception e)
//				{
//					return new GIScriptExpressionError("Exception" + e.toString(), this);
//				}
//		        c.close();
//		        db.close();
//
//				Log.d(LOG_TAG, "database-query const returned  " + res.m_args.size() + " items array" );
//				return res;
//			}
//			@Override
//			public TYPE Type() {
//
//				return GIScriptExpression.TYPE.markers;
//			}
//			@Override
//			public GIScriptExpression eval() {
//
//				return this;
//			}
//			@Override
//			public GIScriptExpression Clone() {
//				return this;
//			}
//		});
//
//	}
////-----------------------------------------------------------------------------------------------------------------------------------
//}
