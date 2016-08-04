package com.chernandezgil.farmacias.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeActivity;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
import com.chernandezgil.farmacias.ui.fragment.FragmentFind;
import com.chernandezgil.farmacias.ui.fragment.MapFragment;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.services.DownloadFarmacias;
import com.chernandezgil.farmacias.view.MainActivityContract;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AllowMeActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, MainActivityContract.View {


    @BindView(R.id.navigation_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindDrawable(R.drawable.ic_menu_white_24dp)
    Drawable menuDrawable;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG_FRAGMENT = "FRAG_MAP";
    private ActionBar actionBar;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String mAddress;
    private MainActivityPresenter mMainActivityPresenter;
    private Geocoder mGeocoder;

    @Inject
    GoogleApiClient mGoogleApiClient;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGeocoder = new Geocoder(this, Locale.getDefault());
        mMainActivityPresenter = new MainActivityPresenter(mGeocoder);
        mMainActivityPresenter.setView(this);
        setUpToolBar();
        Stetho.initializeWithDefaults(this);


        ((MyApplication) getApplication()).getMainActivityComponent().inject(this);

        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);

        if (savedInstanceState == null) {
            launchDownloadService();
        } else {

            mLocation = savedInstanceState.getParcelable("location_key");
        }
        setupNavigationDrawerContent(navigationView);

        mGoogleApiClient.connect();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key", mLocation);
    }

    @Override
    protected void onStart() {
        Util.LOGD(LOG_TAG,"onStart");
        super.onStart();


    }
    @Override
    protected void onPause() {
        Util.LOGD(LOG_TAG,"onPause");
        super.onPause();
    }
    @Override
    protected void onStop() {
        Util.LOGD(LOG_TAG,"onStop");
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void launchDownloadService() {
        Intent intent = new Intent(this, DownloadFarmacias.class);
        startService(intent);
    }

    private void setUpToolBar() {

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(menuDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.item_navigation_localizador:
                        item.setChecked(true);
                        setFragment(0);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.item_navigation_buscar:
                        item.setChecked(true);
                        setFragment(1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.item_navigation_favoritas:
                        item.setChecked(true);
                        setFragment(2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.item_navigation_opcion2:
                        item.setChecked(true);
                        setFragment(3);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                }
                return true;
            }
        });
    }




    private void setFragment(int position) {
        Util.LOGD(LOG_TAG,"setFragment");
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("address_key",mAddress);
                bundle.putParcelable("location_key", mLocation);
                mapFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, mapFragment, TAG_FRAGMENT)
                        .commit();

                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                FragmentFind starredFragment = new FragmentFind();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, starredFragment)
                        .commit();



                break;
        }
    }


    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Util.LOGD(LOG_TAG, "onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //min x secs x millisec
                .setFastestInterval(10 * 60 * 1000);

        if (!AllowMe.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION )) {
            new AllowMe.Builder()
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setRationale("Esta app necesita este permision para funcionar")
                    .setCallback(new AllowMeCallback() {
                        @Override
                        public void onPermissionResult(int i, PermissionResultSet permissionResultSet) {
                            if (permissionResultSet.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
                            }
                        }
                    }).request(1);

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Util.LOGD(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Util.LOGD(LOG_TAG, "onLocationChanged");
        mLocation = location;
        mAddress = mMainActivityPresenter.onGetAddressFromLocation(mLocation);
        //First fragment
        setFragment(0);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.LOGD(LOG_TAG,"ondispatchTouchEvent");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            if (mapFragment != null) {
                mapFragment.handleDispatchTouchEvent(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        Util.LOGD(LOG_TAG,"onBackPressed");
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (mapFragment != null) {
            if (!mapFragment.hideBottomSheet()) {
                super.onBackPressed();
            }
        }


    }

    @Override
    protected void onDestroy() {
        Util.LOGD(LOG_TAG,"onDestroy");
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }


}
