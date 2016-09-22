package com.chernandezgil.farmacias.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.MotionEvent;

import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.PharmacyObjectMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by Carlos on 03/08/2016.
 */
public interface MapTabContract {

    public interface View {
        public void addMarkerToMap(PharmacyObjectMap marker);
        public void moveCamera(CustomCameraUpdate cameraUpdate);
        public boolean collapseBottomSheet();
  //      public void handleDispatchTouchEvent(MotionEvent event);
        public void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSorter, PharmacyObjectMap lastClicked);
        public void refreshMapIfNecesary(PharmacyObjectMap updatedPharmacy);
        public void removeMarker(Marker marker);
        public void showSnackBar(String message);
        public void launchActivity(Intent intent);
        public void showPharmacyInBottomSheet(PharmacyObjectMap pharmacy);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onAddMarkerToHash(Marker marker,PharmacyObjectMap object);
        public void removeMarkerInHashMapAndMapFromMapFragment(PharmacyObjectMap pharmacy);
        public void removeMarkerInHashMapAndMapFromList(String phone);
        public HashMap onGetHashMap();
        public void setLocation(Location location);
        public void onSetLastMarkerClick(PharmacyObjectMap pharmacyObjectMap);
        public String onGetAddressFromLocation(Location currentLocation);
        public void   onSetAddress(String address);
        public Bitmap onRequestCustomBitmap(String order,boolean isOpen);
        public void onSetMarkerBitMap(Bitmap bitmap);
        public CustomCameraUpdate getCameraUpdate();
        public void updateFavoriteFlag(String phone);
        public void handleClickGo();
        public void handleClickCall();
        public void handleClickShare();
        public void handleClickFavorite();
        public void handleOnMarkerClick(Marker marker);




    }
}
