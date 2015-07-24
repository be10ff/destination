package ru.tcgeo.application.local_project_management;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;

import ru.tcgeo.application.R;
import ru.tcgeo.application.home_screen.LayersAdapterItem;

/**
 * Created by a_belov on 22.07.15.
 */
public class SettingsFragment extends Fragment {

    @Bind(R.id.layer_name_edit)
    EditText mName;

    @Bind(R.id.layer_type_edit)
    Spinner mType;

    @Bind(R.id.location_type_edit)
    Spinner mLocationType;

    @Bind(R.id.layer_location_edit)
    EditText mLocation;

    @Bind(R.id.zoom_type_edit)
    Spinner mZoomType;

    @Bind(R.id.zoom_min_edit)
    Spinner mZoomMin;

    @Bind(R.id.zoom_max_edit)
    Spinner mZoomMax;

    @Bind(R.id.layer_range_min)
    EditText mRangeMin;

    @Bind(R.id.layer_range_max)
    EditText mRangeMax;



    LayersAdapterItem mItem;
    public SettingsFragment(){}

    public SettingsFragment(LayersAdapterItem item){
        mItem = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layer_settings, container, false);
        ButterKnife.bind(this, view);

        mName.setText(mItem.m_tuple.layer.getName());
        mLocation.setText(mItem.m_tuple.layer.m_layer_properties.m_source.m_location);
        mRangeMin.setText(String.valueOf(mItem.m_tuple.scale_range.getMin()));
        mRangeMax.setText(String.valueOf(mItem.m_tuple.scale_range.getMax()));

        return view;
    }
}
