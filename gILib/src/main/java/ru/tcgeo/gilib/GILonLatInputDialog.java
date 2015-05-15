package ru.tcgeo.gilib;


import ru.tcgeo.gilib.gps.GIYandexUtils;
import ru.tcgeo.wkt.GI_WktPoint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class GILonLatInputDialog extends DialogFragment 
{
	private EditText m_lon_dec;
	private EditText m_lat_dec;
	private TextView m_lon_can;
	private TextView m_lat_can;
	private TextView m_lon_grad_min;
	private TextView m_lat_grad_min;
	GI_WktPoint m_point;
	GIGeometryPointControl m_control;
	
	public GILonLatInputDialog(GIGeometryPointControl control) 
	{
		m_control = control;
		m_point = m_control.m_WKTPoint;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
		getDialog().setTitle("Dialog");
		View v = inflater.inflate(R.layout.lonlat_input_layout, null);
		m_lon_dec = (EditText) v.findViewById(R.id.lon_decimal);
		m_lat_dec = (EditText) v.findViewById(R.id.lat_decimal);
		m_lon_grad_min = (TextView) v.findViewById(R.id.lon_grad_min);
		m_lat_grad_min = (TextView) v.findViewById(R.id.lat_grad_min);		
		m_lon_can = (TextView) v.findViewById(R.id.lon_can);
		m_lat_can = (TextView) v.findViewById(R.id.lat_can);
		m_lon_dec.setText(String.valueOf(m_point.m_lon));
		m_lat_dec.setText(String.valueOf(m_point.m_lat));
		m_lon_can.setText(getCanonicalCoordString(m_point.m_lon));
		m_lat_can.setText(getCanonicalCoordString(m_point.m_lat));
		
		m_lon_dec.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void afterTextChanged(Editable s) 
			{
				try
				{
					int index = m_lon_dec.getSelectionEnd();
					m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(s.toString());
					//m_point.m_lon = Double.valueOf(s.toString());
					
					m_lon_can.setText(getCanonicalCoordString(m_point.m_lon));
					m_lon_grad_min.setText(getGradMinCoordString(m_point.m_lon));
					m_lon_dec.setSelection(index);
				}
				catch(NumberFormatException e){}
			}
		});
		m_lat_dec.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void afterTextChanged(Editable s) 
			{
				try
				{
					//m_point.m_lat = Double.valueOf(s.toString());
					m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(s.toString());
					m_lat_can.setText(getCanonicalCoordString(m_point.m_lat));
					m_lat_grad_min.setText(getGradMinCoordString(m_point.m_lat));
				}
				catch(NumberFormatException e){}
			}
		});
		m_lon_dec.setText(String.valueOf(m_point.m_lon));
		m_lat_dec.setText(String.valueOf(m_point.m_lat));
		this.getDialog().setCanceledOnTouchOutside(true);
		return v;
	}


	public void onCancel(DialogInterface dialog)
	{
		try
		{
			m_lon_dec.setTextColor(Color.RED);
			m_lon_dec.requestFocus();
			//m_point.m_lon = Double.valueOf(m_lon_dec.getText().toString());
			m_lon_dec.setTextColor(Color.BLACK);
			m_lat_dec.setTextColor(Color.RED);
			m_lat_dec.requestFocus();
			//m_point.m_lat = Double.valueOf(m_lat_dec.getText().toString());
			m_lat_dec.setTextColor(Color.BLACK);
			
//			m_control.m_WKTPoint.m_lon = Double.valueOf(m_lon_dec.getText().toString());
//			m_control.m_WKTPoint.m_lat = Double.valueOf(m_lat_dec.getText().toString());
			m_control.m_WKTPoint.m_lon = m_point.m_lon;
			m_control.m_WKTPoint.m_lat = m_point.m_lat;
			
			m_control.setWKTPoint(m_control.m_WKTPoint);
			//m_control.setWKTPoint(m_point);
			GIEditLayersKeeper.Instance().m_current_geometry_editing_control.invalidate();
			super.onCancel(dialog);
		}
		catch(NumberFormatException e)
		{}
	}
    public String getCanonicalCoordString(double coord)
    {
    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
    	int mins = (int)Math.floor((coord - degrees)*60);
    	double secs = ((coord - degrees)*60-mins)*60;
    	return String.format("%2d° %2d\' %2.4f\"", degrees, mins, secs);
    }
    public String getGradMinCoordString(double coord)
    {
    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
    	float mins = (float) ((coord - degrees)*60);
    	return String.format("%2d° %2.6f\'", degrees, mins);
    }
}
