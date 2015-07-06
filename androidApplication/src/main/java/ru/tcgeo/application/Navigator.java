package ru.tcgeo.application;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.gilib.gps.GICompassView;


/**
 * Created by a_belov on 02.07.15.
 */
public class Navigator extends Activity {


    @Bind(R.id.compass_surface_button)GICompassView mCompass;
    @Bind(R.id.navigation_panel)View mNavigationPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.compass_surface_button)
    public void Show(View v){
        if(mNavigationPanel.getVisibility() == View.GONE) {
            mNavigationPanel.setVisibility(View.VISIBLE);
        }else{
            mNavigationPanel.setVisibility(View.GONE);
        }
    }


}
