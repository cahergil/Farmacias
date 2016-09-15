package com.chernandezgil.farmacias.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.activity.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Carlos on 15/09/2016.
 */

public class TrackFragment extends Fragment implements LocationListener{
    private LocationRequest mLocationRequest;
    private static final String LOG_TAG = TrackFragment.class.getSimpleName();

    public TrackFragment() {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
    }

    public void startTracking(){
        startLocationUpdates( ((MainActivity)getActivity()).getLocationApiClient());

    }

    public void stopTracking(){
        stopLocationUpdates( ((MainActivity)getActivity()).getLocationApiClient());
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //min x secs x millisec
                .setFastestInterval(10000)
                .setInterval(10000);
    }
    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates(GoogleApiClient googleApiClient) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates(GoogleApiClient googleApiClient) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        Util.logD(LOG_TAG,"onLocationChanged");
    }
}
