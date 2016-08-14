package com.chernandezgil.farmacias.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeActivity;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.fragment.FragmentFind;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.services.DownloadFarmacias;
import com.chernandezgil.farmacias.ui.fragment.TabLayoutFragment;
import com.chernandezgil.farmacias.view.MainActivityContract;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AllowMeActivity implements
                 MainActivityContract.View,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {


    @BindView(R.id.navigation_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindDrawable(R.drawable.ic_menu_white_24dp)
    Drawable menuDrawable;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private  Location mLocation;
    private ActionBar actionBar;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG_FRAGMENT = "TAB_FRAGMENT";

    private MainActivityPresenter mMainActivityPresenter;





//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }

    private TabLayoutFragment mtabFragment;
    private static boolean mRotation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PreferencesManager preferencesManager=new AndroidPrefsManager(this);
        mMainActivityPresenter = new MainActivityPresenter(preferencesManager);
        mMainActivityPresenter.setView(this);

        setUpToolBar();


        if (savedInstanceState == null) {

            mLocation = new Location("hola");
            mLocation.setLatitude(38.9766f);
            mLocation.setLongitude(-5.79881);
            checkGooglePlayServicesAvailability();
            Stetho.initializeWithDefaults(this);



        } else {
            mRotation=true;
            mLocation=savedInstanceState.getParcelable("location_key");

        }
        ((MyApplication) getApplication()).getComponent().inject(this);
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
        mGoogleApiClient.connect();

        setupNavigationDrawerContent(navigationView);




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);

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
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
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
            Intent intent=new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        switch (position) {
            case 0:
                Bundle bundle=new Bundle();
                bundle.putParcelable("location_key",mLocation);
                fragmentManager = getSupportFragmentManager();
                mtabFragment = new TabLayoutFragment();
                mtabFragment.setArguments(bundle);
                FragmentTransaction ft=fragmentManager.beginTransaction();
                ft.replace(R.id.fragment, mtabFragment)
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



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.LOGD(LOG_TAG,"ondispatchTouchEvent");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
           MapTabFragment mapTabFragment =getFragmentPressed();
           if(mapTabFragment !=null) {
               mapTabFragment.handleDispatchTouchEvent(ev);
           }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onBackPressed() {

        Util.LOGD(LOG_TAG,"onBackPressed");
        MapTabFragment mapTabFragment =getFragmentPressed();
        if(mapTabFragment !=null) {
            if (!mapTabFragment.collapseBottomSheet()) {
                super.onBackPressed();
            } else {
                return;
            }
        }

        super.onBackPressed();

    }

    private MapTabFragment getFragmentPressed(){
        List<Fragment> list=getSupportFragmentManager().getFragments();

        if(list!=null && list.size()>0) {
            Fragment tabs=list.get(0);
            if(tabs instanceof TabLayoutFragment) {
                if(((TabLayoutFragment) tabs).getCurrentItem()==0) {
                    SparseArray<Fragment> registeredFragments=((TabLayoutFragment) tabs).getFragments();
                    MapTabFragment mapTabFragment = (MapTabFragment) registeredFragments.get(0);
                    return mapTabFragment;
                }
//                MapTabFragment mapTabFragment = (MapTabFragment) tabs.getChildFragmentManager().findFragmentByTag("fragment:0");
//                if(mapTabFragment !=null && ((TabLayoutFragment)tabs).getCurrentItem()==0) {
//                    return mapTabFragment;
//                }
            }

        }
        return null;
    }

    private void checkGooglePlayServicesAvailability(){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        Integer resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            //Do what you want
        } else {
            if(googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }


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


        onLocationChanged(mLocation);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation=location;
        if(!mRotation) {
            setFragment(0);

        } else {
            mRotation = false;
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        Util.LOGD(LOG_TAG,"onDestroy");
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }

}
