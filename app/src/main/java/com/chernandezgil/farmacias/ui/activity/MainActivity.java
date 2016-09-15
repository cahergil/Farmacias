package com.chernandezgil.farmacias.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.chernandezgil.farmacias.BuildConfig;
import com.chernandezgil.farmacias.customwidget.TouchableWrapper;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.fragment.FindFragment;
import com.chernandezgil.farmacias.ui.fragment.ListTabFragment;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.fragment.TabLayoutFragment;
import com.chernandezgil.farmacias.ui.fragment.TrackFragment;
import com.chernandezgil.farmacias.view.MainActivityContract;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity implements
        MainActivityContract.View, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,TouchableWrapper.UpdateMapUserClick,
        ListTabFragment.UpdateFavorite
{
//,
    //

    @BindView(R.id.navigation_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindDrawable(R.drawable.ic_menu_white_24dp)
    Drawable menuDrawable;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private ActionBar actionBar;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private MainActivityPresenter mMainActivityPresenter;
    private PreferencesManager mPreferencesManager;

    private HandlerActivity mHandler;
    private TabLayoutFragment mtabFragment;

    //static so that the value can be read in the onLocationChanged callback
    // , else won't be read properly it value. There is no sense of activity in the callback
    //http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    //Avoid performing transactions inside asynchronous callback methods.
    private static boolean mConfChange;
    private static boolean mFromSettings;



//    private static long GPS_FATEST_INTERVAL = TimeUnit.MINUTES.toMillis(10);
//    private static long GPS_INTERVAL = GPS_FATEST_INTERVAL;
//    private static long FRAG_MAP_REFRESH_INTERVAL = TimeUnit.MINUTES.toSeconds(7);
    private static long GPS_FATEST_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private static long GPS_INTERVAL = GPS_FATEST_INTERVAL;
    private static long FRAG_MAP_REFRESH_INTERVAL = TimeUnit.SECONDS.toSeconds(12);
    private int option = 0;
    private long mElapsedTime;
    private long mStartTime;
    private static boolean mColdStart;
    private static final String[] PERMS=
            {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_PERMISSION = 61125;
    //IcePick variables
    @State
    boolean isInPermission = false;
    @State
    Location mLocation;
    @State
    int mCurrentFragment = 0;

    private PreferencesManager mSharedPreferences;
    private Unbinder mUnbinder;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.logD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null) {
            TrackFragment trackFragment = new TrackFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(trackFragment, "frag").commit();
        }
        mUnbinder=ButterKnife.bind(this);
        mPreferencesManager = new PreferencesManagerImp(this);
        mMainActivityPresenter = new MainActivityPresenter(mPreferencesManager);
        mMainActivityPresenter.setView(this);
        mMainActivityPresenter.onStart();
        mSharedPreferences = new PreferencesManagerImp(this);
        mHandler = getHandler();
        setUpToolBar();
        // enableStrictModeForDebug();
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState == null) {

         //   checkGooglePlayServicesAvailability();
        //    initilizeStetho();

        } else {
            mConfChange = true;

        }
        if (hasAllPermissions(getDesiredPermissions())) {
            buildGoogleApiClient();
        } else if(!isInPermission) {
            isInPermission = true;
            ActivityCompat
                    .requestPermissions(this,
                            netPermissions(getDesiredPermissions()),
                            REQUEST_PERMISSION);
        }
        setupNavigationDrawerContent(navigationView);
        startTimeCounter();
        //remember MainActivity is in singleTop launch mode
        mColdStart = true;



    }

    private void initilizeStetho() {
        Stetho.initializeWithDefaults(this);

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void unregisterLocationCallbacks() {
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            Util.logD(LOG_TAG,"unregisterConnectionCallbacks");
        }

    }
    private void registerLocationCallbacks(){

            mGoogleApiClient.registerConnectionCallbacks(this);
            mGoogleApiClient.registerConnectionFailedListener(this);

    }
    private void enableStrictModeForDebug() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }


    /**
     * used in HandlerActivity
     */
    private int getCurrentFragment() {
        return mCurrentFragment;
    }
    /**
     * used in HandlerActivity
     * @return
     */
    private Location getCurrentLocation(){
        return mLocation;
    }

    /**
     * used in HandlerActivity
     * @return
     */
    private PreferencesManager getSharedPreferencesManager(){
        return mSharedPreferences;
    }

    //Avoid performing transactions inside asynchronous callback methods.
    //http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html
    //in this case static and inner is not enought:
    // don't forget to call   mHandler.removeCallbacksAndMessages(null);
    // see: https://techblog.badoo.com/blog/2014/08/28/android-handler-memory-leaks/
    private static class HandlerActivity extends Handler {
        private final WeakReference<MainActivity> activityWeakReference;

        HandlerActivity(MainActivity mainActivity) {
            activityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity activity = activityWeakReference.get();
            activity.getSharedPreferencesManager().saveLocation(activity.getCurrentLocation());
            switch (msg.what) {
                case 1:
                    if (activity.getCurrentFragment() == 0) {
                        activity.setFragment(0);
                    }
                    break;
                case 3:
                    break;
            }


        }
    }

    private HandlerActivity getHandler() {
        return new HandlerActivity(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.logD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
       // Icepick.saveInstanceState(this, outState);

    }


    @Override
    protected void onStart() {
        Util.logD(LOG_TAG, "onStart");
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {

            mGoogleApiClient.connect();
            Util.logD(LOG_TAG, "mGoogleApiClient.connect()");

        }

    }


    @Override
    protected void onPause() {
        Util.logD(LOG_TAG, "onPause");
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            TrackFragment trackFragment = getTrackFragment();
            if(trackFragment!=null) {
                trackFragment.stopTracking();

            }

            Util.logD(LOG_TAG, "stopLocationUpdates()");
        }


    }

    @Override
    protected void onStop() {
        Util.logD(LOG_TAG, "onStop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Util.logD(LOG_TAG, "onCreateOptionsMeu");
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
            mFromSettings = true;
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
        Util.logD(LOG_TAG, "setFragment");
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
                    Util.logD(LOG_TAG, "IllegalStateException:" + ignored.getMessage());
                }

                break;
            case 1:
                Bundle bundle = new Bundle();
                bundle.putParcelable("location_key", mLocation);
                fragmentManager = getSupportFragmentManager();
                FindFragment findFragment = new FindFragment();
                findFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, findFragment)
                        .commit();


                break;
        }
    }


    private void checkGooglePlayServicesAvailability() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        Integer resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {

        } else {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }


        }
    }

    public GoogleApiClient getLocationApiClient() {
        return mGoogleApiClient;
    }
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //min x secs x millisec
                .setFastestInterval(10000)
                .setInterval(10000);
    }

    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private TrackFragment getTrackFragment() {
        return (TrackFragment) getSupportFragmentManager().findFragmentByTag("frag");


    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
       TrackFragment trackFragment= getTrackFragment();
        if(trackFragment!=null) {
            trackFragment.startTracking();
        }
        Util.logD(LOG_TAG, "onConnected");



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Util.logD(LOG_TAG, "onLocationChanged");
//        mLocation = location;
//        //prevent loading content. Only when fresh start or when location updates
//        mElapsedTime = (int) (System.currentTimeMillis() - mStartTime) / 1000;
//
//        //on configuration changes not saving coordinates
//        if (!mConfChange) {
//            if (mColdStart) {
//
//      //          mHandler.sendMessage(createMessage(1));
//                mColdStart = false;
//            } else if (mFromSettings) { //we are in SingleTop mode-> firstRun=false
//        //        mHandler.sendMessage(createMessage(1));
//                mFromSettings = false;
//            } else if (mElapsedTime > FRAG_MAP_REFRESH_INTERVAL) {
//          //      mHandler.sendMessage(createMessage(3));
//                startTimeCounter();
//            }
//        } else {
//            mConfChange = false;
//        }


    }

    private Message createMessage(int what) {
        Message message = new Message();
        message.setTarget(mHandler);
        message.what=what;
        return message;
    }
    private void startTimeCounter() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//
    @Override
    public void onClickMap(MotionEvent event) {
        Util.logD(LOG_TAG, "onClickMap");
        MapTabFragment mapTabFragment = getMapTabFragment();
        if (mapTabFragment != null) {
            mapTabFragment.handleDispatchTouchEvent(event);
        }
    }

    /**
     * Comunicates ListTabFragment with MapTabFragment
     *
    // * @param phone
    // * @param flag
     */
    @Override
    public void onUpdateFavorite(String phone, boolean flag) {
        MapTabFragment mapTabFragment = getMapTabFragment();
        if (mapTabFragment != null) {
            mapTabFragment.updateClickedPhoneToPresenter(phone, true);
            mapTabFragment.removeMarkerFromHashInPresenter(phone);
        }
    }

    private MapTabFragment getMapTabFragment() {
        List<Fragment> list = getSupportFragmentManager().getFragments();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Fragment tabs = list.get(i);
                if (tabs instanceof TabLayoutFragment) {

                    SparseArray<Fragment> registeredFragments = ((TabLayoutFragment) tabs).getFragments();
                    MapTabFragment mapTabFragment = (MapTabFragment) registeredFragments.get(1);
                    return mapTabFragment;

                }
            }

        }
        return null;
    }

    private int getCurrentFragmentInTab() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Fragment tabs = list.get(i);
                if (tabs instanceof TabLayoutFragment) {

                    return ((TabLayoutFragment) tabs).getCurrentItem();

                }
            }

        }
        return 0;
    }

    @Override
    public void onBackPressed() {

        Util.logD(LOG_TAG, "onBackPressed");
        int currentItem = getCurrentFragmentInTab();
        if (currentItem != 1) super.onBackPressed();
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

    /**
     * Permissions related methods
     * @param perms
     * @return
     */
    private boolean hasAllPermissions(String[] perms) {
        for (String perm : perms) {
            if (!hasPermission(perm)) {
                return (false);
            }
        }

        return (true);
    }

    private boolean hasPermission(String perm) {
        return (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED);
    }

    protected String[] getDesiredPermissions() {
        return(PERMS);
    }

    private String[] netPermissions(String[] wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return (result.toArray(new String[result.size()]));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isInPermission = false;
        if (requestCode == REQUEST_PERMISSION) {
            if (hasAllPermissions(getDesiredPermissions())) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            } else {
              //  handlePermissionDenied();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Util.logD(LOG_TAG, "onDestroy");
        mUnbinder.unbind();
//        mGoogleApiClient = null;

        mHandler.removeCallbacksAndMessages(null);
//        mHandler = null;
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }
}
