package com.chernandezgil.farmacias.view;

import android.location.Location;
import android.view.MotionEvent;

import com.chernandezgil.farmacias.model.CustomMarker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by Carlos on 03/08/2016.
 */
public interface MapContract {

    public interface View {
        public void addMarkerToMap(CustomMarker marker);
        public void moveCamera(CameraUpdate cameraUpdate);
        public boolean hideBottomSheet();
        public void handleDispatchTouchEvent(MotionEvent event);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onAddMarkerToHash(Marker marker,CustomMarker object);
        public HashMap onGetHashMap();
        public void setLocation(Location location);
        public void onSetLastMarkerClick(CustomMarker customMarker);

    }
}
