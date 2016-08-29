package com.chernandezgil.farmacias.view;

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
        public void addMarkerToMap(PharmacyObjectMap marker,boolean flagUpdateFavorite);
        public void moveCamera(CustomCameraUpdate cameraUpdate);
        public boolean collapseBottomSheet();
        public void handleDispatchTouchEvent(MotionEvent event);
        public void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSorter, PharmacyObjectMap lastClicked);
        public void refreshMap(PharmacyObjectMap updatedPharmacy);
        public void removeMarker(Marker marker);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onAddMarkerToHash(Marker marker,PharmacyObjectMap object);
        public void removeMarkerInHashFromMap(PharmacyObjectMap pharmacy);
        public void removeMarkerInHashFromList(String phone);
        public HashMap onGetHashMap();
        public void setLocation(Location location);
        public void onSetLastMarkerClick(PharmacyObjectMap pharmacyObjectMap);
        public LatLng onGetDestinationLocale();
        public String onGetDestinationAddress();
        public String onGetDestinationPhoneNumber();
        public String onGetAddressFromLocation(Location location);
        public void   onSetAddress(String address);
        public Bitmap onRequestCustomBitmap(String order,boolean isOpen);
        public void onSetMarkerBitMap(Bitmap bitmap);
        public CustomCameraUpdate getCameraUpdate();
        public void updateFavoriteFlag(String phone);



    }
}
