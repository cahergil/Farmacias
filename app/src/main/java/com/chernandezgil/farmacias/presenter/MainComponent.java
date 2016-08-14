package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.ApplicationModule;
import com.chernandezgil.farmacias.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Carlos on 14/08/2016.
 */
@Singleton
@Component(modules ={ MainModule.class,
        ApplicationModule.class})
public interface MainComponent {

    void inject(MainActivity main);


}