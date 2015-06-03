package ru.tcgeo.gilib.gps;

import ru.tcgeo.gilib.R;
import ru.tcgeo.wkt.GI_WktGeometry;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class GILocatorFragment extends Fragment
{
	GI_WktGeometry m_poi;
	public GILocatorFragment(){}
	public GILocatorFragment(GI_WktGeometry poi) 
	{
		m_poi = poi;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.locator_view, null);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		v.setLayoutParams(param);
		((GILocatorView)v.findViewById(R.id.DrawViewLocator)).setTarget(m_poi);
		return v;
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
