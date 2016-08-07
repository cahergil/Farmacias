package com.chernandezgil.farmacias.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.MotionEvent;

import com.chernandezgil.farmacias.model.CustomMarker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Carlos on 03/08/2016.
 */
public interface MapContract {

    public interface View {
        public void addMarkerToMap(CustomMarker marker);
        public void moveCamera(CameraUpdate cameraUpdate);
        public boolean collapseBottomSheet();
        public void handleDispatchTouchEvent(MotionEvent event);
        public void displayPharmacyInBottomSheet(CustomMarker firstSorter,CustomMarker lastClicked);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onAddMarkerToHash(Marker marker,CustomMarker object);
        public HashMap onGetHashMap();
        public void setLocation(Location location);
        public void onSetLastMarkerClick(CustomMarker customMarker);
        public LatLng onGetDestinationLocale();
        public String onGetDestinationAddress();
        public String onGetDestinationPhoneNumber();
        public String onGetAddressFromLocation(Location location);
        public Bitmap onRequestCustomBitmap(String order,boolean isOpen);
        public void onSetMarkerBitMap(Bitmap bitmap);
        public CameraUpdate getCameraUpdate();


    }
}
