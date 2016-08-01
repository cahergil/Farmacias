package com.chernandezgil.farmacias;

import android.app.Application;

import com.chernandezgil.farmacias.presenter.DaggerMainActivityComponent;
import com.chernandezgil.farmacias.presenter.MainActivityComponent;
import com.chernandezgil.farmacias.presenter.MainActivityModule;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MyApplication extends Application{

    private MainActivityComponent mMainComponent;
    @Override
    public void onCreate() {
        super.onCreate();
     //   mMainComponent=DaggerMainActivityComponent.builder()
          mMainComponent=DaggerMainActivityComponent.builder()
                    .mainActivityModule(new MainActivityModule())
                    .applicationModule(new ApplicationModule(this))
                    .build();


    }
    public MainActivityComponent getMainActivityComponent(){
        return mMainComponent;
    }
}

