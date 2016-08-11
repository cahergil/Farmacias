package com.chernandezgil.farmacias.view;

import android.graphics.Bitmap;
import android.location.Location;
import android.view.MotionEvent;

import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 08/08/2016.
 */
public interface ListContract {

    public interface View {
        public void showResults(List<Pharmacy> pharmacyList);
        public void showNoResults();
        public void showLoading();
        public void hideLoading();
        public void setAddress(String address);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onGetAddressFromLocation(Location location);




    }
}
