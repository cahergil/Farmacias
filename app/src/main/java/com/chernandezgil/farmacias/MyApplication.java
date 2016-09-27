package com.chernandezgil.farmacias;

import android.app.Application;
import android.content.Context;


import com.chernandezgil.farmacias.presenter.MainComponent;


import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Carlos on 01/08/2016.
 */
public class MyApplication extends Application{

    private MainComponent mMapComponent;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

          this.context=getApplicationContext();
//          mMapComponent= DaggerMainComponent.builder()
//                    .mainModule(new MainModule())
//                    .applicationModule(new ApplicationModule(this))
//                    .build();


    }
    public static Context getContext(){
        return context;
    }
//    public MainComponent getComponent(){
//        return mMapComponent;
//    }
}

