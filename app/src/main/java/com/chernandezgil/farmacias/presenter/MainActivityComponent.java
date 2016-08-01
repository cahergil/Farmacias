package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.ApplicationModule;
import com.chernandezgil.farmacias.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Carlos on 01/08/2016.
 */
@Singleton
@Component(modules ={ MainActivityModule.class,
        ApplicationModule.class})
public interface MainActivityComponent {

    void inject(MainActivity activity);
}
