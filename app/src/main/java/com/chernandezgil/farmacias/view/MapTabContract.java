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

    interface View {
        void addMarkerToMap(PharmacyObjectMap marker);
        void moveCamera(CustomCameraUpdate cameraUpdate);
        boolean collapseBottomSheet();
        void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSorter, PharmacyObjectMap lastClicked);
        void refreshMapIfNecesary(PharmacyObjectMap updatedPharmacy);
        void removeMarker(Marker marker);
        void showSnackBar(String message);
        void launchActivity(Intent intent);
        void showPharmacyInBottomSheet(PharmacyObjectMap pharmacy);
        boolean isBottomSheetExpanded();
        void setStateBottomSheet(int state);
        void showOpeningHours(int layoutId,int backgroundColor);
    }
    public interface Presenter<V> {

        void setView(V view);
        void detachView();
        void onStartLoader();
        void onAddMarkerToHash(Marker marker,PharmacyObjectMap object);
        void removeMarkerInHashMapAndMapFromMapFragment(PharmacyObjectMap pharmacy);
        void removeMarkerInHashMapAndMapFromList(String phone);
        HashMap onGetHashMap();
        void setLocation(Location location);
        void onSetLastMarkerClick(PharmacyObjectMap pharmacyObjectMap);
        String onGetAddressFromLocation(Location currentLocation);
        void   onSetAddress(String address);
        Bitmap onRequestCustomBitmap(String order,boolean isOpen);
        void onSetMarkerBitMap(Bitmap bitmap);
        CustomCameraUpdate getCameraUpdate();
        void updateFavoriteFlag(String phone);
        void handleClickGo();
        void handleClickCall();
        void handleClickShare();
        void handleClickFavorite();
        void handleOnMarkerClick(Marker marker);
        void handleClickOpeningHours();
    }
}
