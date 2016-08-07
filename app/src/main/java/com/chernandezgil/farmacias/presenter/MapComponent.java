package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.ApplicationModule;
import com.chernandezgil.farmacias.ui.activity.MainActivity;
import com.chernandezgil.farmacias.ui.fragment.MapFragment;
import com.chernandezgil.farmacias.ui.fragment.TabLayoutFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Carlos on 01/08/2016.
 */
@Singleton
@Component(modules ={ MapModule.class,
        ApplicationModule.class})
public interface MapComponent {

    void inject(TabLayoutFragment fragment);
    void inject(MapPresenter presenter);
}
