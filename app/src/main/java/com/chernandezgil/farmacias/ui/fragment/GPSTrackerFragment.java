package com.chernandezgil.farmacias.ui.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.activity.MainActivity;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.concurrent.TimeUnit;

/**
 * Created by Carlos on 15/09/2016.
 */

public class GPSTrackerFragment extends Fragment implements LocationListener {
    public static final int REQUEST_CHECK_SETTINGS = 1338;
    private LocationRequest mLocationRequest;
    private static final String LOG_TAG = GPSTrackerFragment.class.getSimpleName();
    private static long GPS_FATEST_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private static long GPS_INTERVAL = GPS_FATEST_INTERVAL;
    private static long FRAG_MAP_REFRESH_INTERVAL = TimeUnit.MINUTES.toSeconds(4);
    private static boolean mFirstRun = false;
    public static final String BROADCAST = "broadcast";
    public static final String ACTION = "launch_fragment_0";
    private long mElapsedTime;
    private long mStartTime;
    private static boolean mColdStart;
    private PreferencesManager mSharedPreferences;

    public GPSTrackerFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Util.logD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        createLocationRequest();
        mStartTime = System.currentTimeMillis();
        mFirstRun = true;
        //if change to getActivity() I get memmory leak
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        requestLocationSettings();
    }

    @Override
    public void onPause() {
        Util.logD(LOG_TAG, "onPause");
        stopTracking();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void requestLocationSettings(){
        Util.logD(LOG_TAG, "requestLocationSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(getGoogleApiClient(),
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Util.logD(LOG_TAG, "settingsResult:SUCCESS");
                        startTracking();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Util.logD(LOG_TAG, "settingsResult:RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Util.logD(LOG_TAG, "error insideRESOLUTION_REQUIRED:" + e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Util.logD(LOG_TAG, "settingsResult:SETTINGS_CHANGE_UNAVAILABLE");
                }
            }
        });

    }

    public void startTracking() {
        startLocationUpdates(getGoogleApiClient());
        Util.logD(LOG_TAG, "startLocationUpdates");

    }

    public void stopTracking() {
        stopLocationUpdates(getGoogleApiClient());
        Util.logD(LOG_TAG, "stopLocationUpdates");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private GoogleApiClient getGoogleApiClient() {
        return ((MainActivity) getActivity()).getLocationApiClient();
    }

    private void getCurrentFragmentInDrawer() {
        ((MainActivity) getActivity()).getCurrentFragment();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(GPS_FATEST_INTERVAL)
                .setInterval(GPS_INTERVAL)
                .setSmallestDisplacement(100f);

    }

    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates(GoogleApiClient googleApiClient) {
        PendingResult<Status> result = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(!status.isSuccess()) {

                    Util.logD(LOG_TAG, String.format("requestLocationUpdates returned an error:code:%s ," +
                            "message: %s",status.getStatusCode(),status.getStatusMessage()));

                    getActivity().finish();
                }
            }
        },2,TimeUnit.SECONDS);

    }

    protected void stopLocationUpdates(GoogleApiClient googleApiClient) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onLocationChanged(Location location) {
        Location lastLocation = location;
        Util.logD(LOG_TAG, "onLocationChanged");
        if (lastLocation == null) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(getGoogleApiClient());

        }
        mElapsedTime = (int) (System.currentTimeMillis() - mStartTime) / TimeUnit.SECONDS.toMillis(1);
        if (mFirstRun) {
            mFirstRun = false;
            mSharedPreferences.saveLocation(lastLocation);
            Util.logD(LOG_TAG, "locationSaved");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(createBroadcastIntent());
        } else if (mElapsedTime > FRAG_MAP_REFRESH_INTERVAL) {
            Util.logD(LOG_TAG, "locationSaved");
            mSharedPreferences.saveLocation(lastLocation);
            restartTimeCounter();
        }
    }

    private Intent createBroadcastIntent() {
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(ACTION, 0);


        return intent;

    }

    public void restartTimeCounter() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroyView() {
        Util.logD(LOG_TAG, "onDestroy");
   //     mSharedPreferences = null;
        super.onDestroyView();
    }


}
