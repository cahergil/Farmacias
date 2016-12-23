package com.chernandezgil.farmacias.ui.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.chernandezgil.farmacias.Utilities.Utils;
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
    private static long GPS_FASTEST_INTERVAL = TimeUnit.MINUTES.toMillis(6);
    private static long GPS_INTERVAL = GPS_FASTEST_INTERVAL;
    private static long FRAG_MAP_REFRESH_INTERVAL = TimeUnit.SECONDS.toSeconds(6);
    private static boolean mFirstRun = false;
    public static final String BROADCAST = "broadcast";
    public static final String ACTION = "launch_fragment_0";
    private static double MIN_DISTANCE = 25d;

    private boolean resurrected = false;
    private long mStartTime;

    private PreferencesManager mSharedPreferences;

    public GPSTrackerFragment() {

    }

    // Android kills this app soon after it has gone to the background(by for example pressing home button).
    // The app goes onPause(),onSaveInstanceState()
    // and after onStop() and stays in background.
    // Later on if I open other apps. and an app with higher priority needs memory, android kills our
    // app process. But our instance state is saved in each activity or fragment bundle.
    // If the user navigates   to the activity, then the system creates another process and for all activities
    // and fragments onCreate() is called with the bundle saved in onSaveInstanceState().
    // With this headless fragment, we must take care to save a bundle ourself.
    // to be able to recreate the fragments state(not the view state, since headless fragment dont have
    // UI). This is done with dummy data, so as when onCreate is called it has savedInstanceState!=null
    // and we can restore the instance state.
    // In case  I use resurrected== true to indicate that the sys has be false in onLocationChanged so as not to create new TabLayoutFragment and so on,
    // if it makes so, gives later on a npe.

    // A way to simulate the system killing a process in background(that is we for instance press the
    // home button) is pressing the red icon with an x in android monitor left panel.
    // After that tapping the app icon will start up calling onCreate() and so on.
    //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        createLocationRequest();
        mStartTime = System.currentTimeMillis();

        // if null the system is creating a new instance.
        if (savedInstanceState == null) {
            Utils.logD(LOG_TAG, "onCreate, savedIntanceState==null");
            mFirstRun = true;
        } else {
            resurrected = true;
            //in this retained fragment, can't never be here except if the app is resurrected
            Utils.logD(LOG_TAG, "onCreate, savedIntanceState!=null");
            Utils.logD(LOG_TAG, "onCreate, app resurrected");
        }
        //if change to getActivity() I get memory leak
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        requestLocationSettings();
    }


    @Override
    public void onStart() {
        Utils.logD(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.logD(LOG_TAG, "onPause");
        stopTracking();
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Utils.logD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        // creating this dummy data, will make that savedInstanceState be !=null in onCreate
        // after system kills the app, otherwise savedInstanceState is always null
        outState.putString("dummy_key", "dummy_data");

    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop");
        super.onStop();
    }

    private void requestLocationSettings() {
        if (getGoogleApiClient() == null) return;
        Utils.logD(LOG_TAG, "requestLocationSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(getGoogleApiClient(), builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Utils.logD(LOG_TAG, "settingsResult:SUCCESS");
                        startTracking();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Utils.logD(LOG_TAG, "settingsResult:RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Utils.logD(LOG_TAG, "error insideRESOLUTION_REQUIRED:" + e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Utils.logD(LOG_TAG, "settingsResult:SETTINGS_CHANGE_UNAVAILABLE");
                }
            }
        });

    }

    public void startTracking() {

        startLocationUpdates(getGoogleApiClient());
        Utils.logD(LOG_TAG, "startLocationUpdates");

    }

    public void stopTracking() {

        stopLocationUpdates(getGoogleApiClient());
        Utils.logD(LOG_TAG, "stopLocationUpdates");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private GoogleApiClient getGoogleApiClient() {
        return ((MainActivity) getActivity()).getLocationApiClient();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(GPS_FASTEST_INTERVAL)
                .setInterval(GPS_INTERVAL)
                .setSmallestDisplacement(100f);

    }

    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates(GoogleApiClient googleApiClient) {
        // particular case:hace un requestLocationSettings y casi al mismo tiempo  stop(),
        // ,se recibe settingsResult:SUCCESS y luego hace startLocationUpdates y como
        // no está conectado da error
        if (!googleApiClient.isConnected()) {
            return;
        }

        PendingResult<Status> result = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (!status.isSuccess()) {

                    Utils.logD(LOG_TAG, String.format("requestLocationUpdates returned an error:code:%s ," +
                            "message: %s", status.getStatusCode(), status.getStatusMessage()));

                    getActivity().finish();
                }
            }
        }, 2, TimeUnit.SECONDS);

    }

    protected void stopLocationUpdates(GoogleApiClient googleApiClient) {
        if (!getGoogleApiClient().isConnected()) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

    }


    // only if the location changes this callback will be called, and that will happen taking into
    // account the interval set in the location request and the minimun displacement.
    // There is a FRAG_MAP_REFRESH_INTERVAL in case the user leaves the app, when returning if the
    // elapsed time is greater than this interval, then it will cause the location to be saved
    // to preferences, which will trigger onPreferenceChange callback, but only
    // if the location saved is distinct from the previous stored.

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onLocationChanged(Location location) {
        Location lastLocation = location;
        Utils.logD(LOG_TAG, "onLocationChanged");
        if (lastLocation == null) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(getGoogleApiClient());

        }
        final long mElapsedTime = (int) (System.currentTimeMillis() - mStartTime) / TimeUnit.SECONDS.toMillis(1);
        if (mFirstRun) {
            mFirstRun = false;
            mSharedPreferences.saveLocation(lastLocation);
            Utils.logD(LOG_TAG, "locationSaved {mFirstRun}");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(createBroadcastIntent());
        } else if (mElapsedTime > FRAG_MAP_REFRESH_INTERVAL) {
            Utils.logD(LOG_TAG, "lat:" + lastLocation.getLatitude() + ",lon:" + lastLocation.getLongitude());
            Location lastStoredLocation = mSharedPreferences.getLocation();
            double distance=Utils.meterDistanceBetweenPoints(lastStoredLocation.getLatitude(),
                    lastStoredLocation.getLongitude(),
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            if(distance> MIN_DISTANCE) {
                mSharedPreferences.saveLocation(lastLocation);
                Utils.logD(LOG_TAG, "locationSaved {ElapsedTime}");
            }
            restartTimeCounter();
        } else if (resurrected) {
            resurrected = false;
            Utils.logD(LOG_TAG, "locationSaved {resurrected}");
            mSharedPreferences.saveLocation(lastLocation);
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
        Utils.logD(LOG_TAG, "onDestroyView");
        //     mSharedPreferences = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Utils.logD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
