package com.chernandezgil.farmacias;

import android.app.Application;


import com.chernandezgil.farmacias.presenter.DaggerMapComponent;
import com.chernandezgil.farmacias.presenter.MapComponent;
import com.chernandezgil.farmacias.presenter.MapModule;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MyApplication extends Application{

    private MapComponent mMapComponent;
    @Override
    public void onCreate() {
        super.onCreate();

          mMapComponent= DaggerMapComponent.builder()
                    .mapModule(new MapModule())
                    .applicationModule(new ApplicationModule(this))
                    .build();


    }
    public MapComponent getMainActivityComponent(){
        return mMapComponent;
    }
}

