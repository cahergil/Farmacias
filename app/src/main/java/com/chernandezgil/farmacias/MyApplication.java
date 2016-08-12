package com.chernandezgil.farmacias;

import android.app.Application;
import android.content.Context;


import com.chernandezgil.farmacias.presenter.DaggerMapComponent;
import com.chernandezgil.farmacias.presenter.MapComponent;
import com.chernandezgil.farmacias.presenter.MapModule;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MyApplication extends Application{

    private MapComponent mMapComponent;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
          this.context=getApplicationContext();
          mMapComponent= DaggerMapComponent.builder()
                    .mapModule(new MapModule())
                    .applicationModule(new ApplicationModule(this))
                    .build();


    }
    public static Context getContext(){
        return context;
    }
    public MapComponent getComponent(){
        return mMapComponent;
    }
}

