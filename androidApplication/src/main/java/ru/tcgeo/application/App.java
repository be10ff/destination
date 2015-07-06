package ru.tcgeo.application;

import android.app.Application;

/**
 * Created by a_belov on 03.07.15.
 */
public class App extends Application {
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App getInstance(){
        return sInstance;
    }

}
