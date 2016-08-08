package com.chernandezgil.farmacias.model;

import com.google.android.gms.maps.CameraUpdate;

/**
 * Created by Carlos on 08/08/2016.
 */
public class CustomCameraUpdate {

    private CameraUpdate mCameraUpdate;
    private boolean isNoResultsPosition;

    public CustomCameraUpdate() {

    }

    public CustomCameraUpdate(CameraUpdate mCameraUpdate, boolean isNoResultsPosition) {
        this.mCameraUpdate = mCameraUpdate;
        this.isNoResultsPosition = isNoResultsPosition;
    }

    public CameraUpdate getmCameraUpdate() {
        return mCameraUpdate;
    }

    public void setmCameraUpdate(CameraUpdate mCameraUpdate) {
        this.mCameraUpdate = mCameraUpdate;
    }

    public boolean isNoResultsPosition() {
        return isNoResultsPosition;
    }

    public void setNoResultsPosition(boolean noResultsPosition) {
        isNoResultsPosition = noResultsPosition;
    }
}
