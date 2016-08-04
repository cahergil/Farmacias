package com.chernandezgil.farmacias.view;

import android.location.Location;

import com.chernandezgil.farmacias.model.CustomMarker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by Carlos on 01/08/2016.
 */
public interface MainActivityContract {

    public interface View {

    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public String onGetAddressFromLocation(Location location);

    }
}
