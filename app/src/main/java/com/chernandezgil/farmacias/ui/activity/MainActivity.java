package com.chernandezgil.farmacias.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
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
import android.view.View;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeActivity;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.customwidget.TouchableWrapper;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.fragment.FragmentFind;
import com.chernandezgil.farmacias.ui.fragment.ListTabFragment;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
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
        MainActivityContract.View, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, TouchableWrapper.UpdateMapUserClick,
        ListTabFragment.UpdateFavorite {


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
    private Location mLocation;
    private ActionBar actionBar;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private MainActivityPresenter mMainActivityPresenter;
    private PreferencesManager mPreferencesManager;
    private int mCurrentFragment = 0;
    private Handler mHandler;
    private TabLayoutFragment mtabFragment;
    private static boolean mRotation;
    //  private static  boolean mActivityRestarted;

    private static int GPS_FATEST_INTERVAL = 10 * 60 * 1000;
    private static int GPS_INTERVAL = GPS_FATEST_INTERVAL;
    //60 sec * 7 minutes
    private static int FRAG_MAP_REFRESH_INTERVAL=60*7;
    private int option = 0;
    int mElapsedTime;
    long mStartTime;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static boolean firsRun=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPreferencesManager = new AndroidPrefsManager(this);
        mMainActivityPresenter = new MainActivityPresenter(mPreferencesManager);
        mMainActivityPresenter.setView(this);
        mHandler = createHandler();
        setUpToolBar();


        if (savedInstanceState == null) {

            checkGooglePlayServicesAvailability();
            Stetho.initializeWithDefaults(this);


        } else {
            mRotation = true;
            mLocation = savedInstanceState.getParcelable("location_key");
            mCurrentFragment = savedInstanceState.getInt("current_fragment_key");

        }
        ((MyApplication) getApplication()).getComponent().inject(this);
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);


        setupNavigationDrawerContent(navigationView);

        startCounter();



    }


    private Handler createHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mCurrentFragment == 0) {
                    setFragment(mCurrentFragment);
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key", mLocation);
        outState.putInt("current_fragment_key", mCurrentFragment);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.LOGD(LOG_TAG, "onStart");
    }

    @Override
    protected void onStart() {
        Util.LOGD(LOG_TAG, "onStart");
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.LOGD(LOG_TAG, "onResume");

    }

    @Override
    protected void onPause() {
        Util.LOGD(LOG_TAG, "onPause");
        super.onPause();

        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    protected void onStop() {
        Util.LOGD(LOG_TAG, "onStop");
//        don't know why but disconnecting causes onConnected be called twice
//        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Util.LOGD(LOG_TAG, "onCreateOptionsMeu");
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
            Intent intent = new Intent(this, SettingsActivity.class);
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
//        drawerLayout.addDrawerListener(mDrawerListener);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.item_navigation_localizador:
                        option = 0;
                        break;
                    case R.id.item_navigation_buscar:
                        option = 1;
                        break;
                    case R.id.item_navigation_favoritas:
                        option = 2;
                        break;
                    case R.id.item_navigation_opcion2:
                        option = 3;
                        break;
                    default:
                        return true;

                }
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setFragment(option);
                    }
                }, 300);
                mCurrentFragment = option;
                return true;
            }
        });
    }


    private void setFragment(int position) {
        Util.LOGD(LOG_TAG, "setFragment");
        FragmentManager fragmentManager;

        switch (position) {
            case 0:
                try {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("location_key", mLocation);
                    fragmentManager = getSupportFragmentManager();
                    mtabFragment = new TabLayoutFragment();
                    mtabFragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment, mtabFragment)
                            .commit();

                } catch (IllegalStateException ignored) {

                }

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


//    I've implemente ontouch event in map, there is no need for this now
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Util.LOGD(LOG_TAG,"ondispatchTouchEvent");
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//           MapTabFragment mapTabFragment =getMapTabFragment();
//           if(mapTabFragment !=null) {
//               mapTabFragment.handleDispatchTouchEvent(ev);
//           }
//        }
//        return super.dispatchTouchEvent(ev);
//    }


    @Override
    public void onBackPressed() {

        Util.LOGD(LOG_TAG, "onBackPressed");
        MapTabFragment mapTabFragment = getMapTabFragment();
        if (mapTabFragment != null) {
            if (!mapTabFragment.collapseBottomSheet()) {
                super.onBackPressed();
            } else {
                return;
            }
        }

        super.onBackPressed();

    }

    private MapTabFragment getMapTabFragment() {
        List<Fragment> list = getSupportFragmentManager().getFragments();

        if (list != null && list.size() > 0) {
            Fragment tabs = list.get(0);
            if (tabs instanceof TabLayoutFragment) {
                //  if(((TabLayoutFragment) tabs).getCurrentItem()==1) {
                SparseArray<Fragment> registeredFragments = ((TabLayoutFragment) tabs).getFragments();
                MapTabFragment mapTabFragment = (MapTabFragment) registeredFragments.get(1);
                return mapTabFragment;
                //  }
//                MapTabFragment mapTabFragment = (MapTabFragment) tabs.getChildFragmentManager().findFragmentByTag("fragment:0");
//                if(mapTabFragment !=null && ((TabLayoutFragment)tabs).getCurrentItem()==0) {
//                    return mapTabFragment;
//                }
            }

        }
        return null;
    }

    private void checkGooglePlayServicesAvailability() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        Integer resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            //Do what you want
        } else {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }


        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //min x secs x millisec
                .setFastestInterval(GPS_FATEST_INTERVAL)
                .setInterval(GPS_INTERVAL);
    }

    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, MainActivity.this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Util.LOGD(LOG_TAG, "onConnected");
        createLocationRequest();

        if (!AllowMe.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AllowMe.Builder()
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setRationale("Esta app necesita este permision para funcionar")
                    .setCallback(new AllowMeCallback() {
                        @Override
                        public void onPermissionResult(int i, PermissionResultSet permissionResultSet) {
                            if (permissionResultSet.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                startLocationUpdates();

                            }
                        }
                    }).request(1);

        } else {
            startLocationUpdates();
        }
        //once gotten the trace can remove this
        if (!AllowMe.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AllowMe.Builder()
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setRationale("Esta app necesita este permision para funcionar")
                    .setCallback(new AllowMeCallback() {
                        @Override
                        public void onPermissionResult(int requestCode, PermissionResultSet result) {
                            if (result.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                //... permission is granted, handle here
                            }
                        }
                    }).request(2);
        } else {
            //... handle permission already granted
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Util.LOGD(LOG_TAG, "onLocationChanged");
        mLocation = location;
        //prevent loading content. Only when fresh start or when location updates
        mElapsedTime = (int) (System.currentTimeMillis() - mStartTime) / 1000;
        if (!mRotation ) {
            if(firsRun) {
                mHandler.sendEmptyMessage(0);
                firsRun=false;
            } else if(mElapsedTime > 420) {
                mHandler.sendEmptyMessage(0);
                startCounter();
            }
        } else {
            mRotation = false;
        }


    }

    private void startCounter(){
        mStartTime=System.currentTimeMillis();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        //   drawerLayout.removeDrawerListener(mDrawerListener);
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onClickMap(MotionEvent event) {
        Util.LOGD(LOG_TAG, "onClickMap");
        MapTabFragment mapTabFragment = getMapTabFragment();
        if (mapTabFragment != null) {
            mapTabFragment.handleDispatchTouchEvent(event);
        }
    }

    @Override
    public void onUpdateFavorite(String phone, boolean flag) {
        MapTabFragment mapTabFragment = getMapTabFragment();
        if (mapTabFragment != null) {
            mapTabFragment.updateClickedPhoneToPresenter(phone, true);
            mapTabFragment.removeMarkerFromHashInPresenter(phone);
        }
    }
}
