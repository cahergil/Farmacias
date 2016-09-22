package com.chernandezgil.farmacias.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.chernandezgil.farmacias.BuildConfig;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.fragment.FindFragment;
import com.chernandezgil.farmacias.ui.fragment.ListTabFragment;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.fragment.TabLayoutFragment;
import com.chernandezgil.farmacias.ui.fragment.GPSTrackerFragment;
import com.chernandezgil.farmacias.view.MainActivityContract;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity implements
        MainActivityContract.View, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ListTabFragment.UpdateFavorite
{
// TouchableWrapper.UpdateMapUserClick

    private static final String DIALOG_ERROR = "dialog_error";
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
    private static final int REQUEST_RESOLVE_CONNECTION_ERROR = 1001;
    private static final int REQUEST_CODE_SETTINGS = 2000;
    private static final String GPS_FRAG = "gps_frag";
    private MainActivityPresenter mMainActivityPresenter;
    private TabLayoutFragment mtabFragment;



    private int option = 0;
    private static final String[] PERMS=
            {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_PERMISSION = 61125;
    //IcePick variables
    @State
    boolean isInPermission = false;
    @State
    int mCurrentFragment =0;
    @State
    boolean mResolvingConnectionError;


    private PreferencesManager mSharedPreferences;
    private Unbinder mUnbinder;
    ;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BroadcastReceiver launcherBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int  action = bundle.getInt(GPSTrackerFragment.ACTION);
            if(action == 0) {
                launchFragment(0);
            }
        }
    };
    private int mRadio;
    private boolean mRadioChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.logD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP) {
//            TransitionInflater inflater = TransitionInflater.from(this);
//            Transition transition = inflater.inflateTransition(R.transition.exit_from_main);
//            getWindow().setExitTransition(transition);
        }
        mUnbinder=ButterKnife.bind(this);
        mSharedPreferences = new PreferencesManagerImp(getApplicationContext());
        mMainActivityPresenter = new MainActivityPresenter(mSharedPreferences);
        mMainActivityPresenter.setView(this);
        mMainActivityPresenter.onStart();
        setUpToolBar();

        // enableStrictModeForDebug();
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState == null) {

         //   checkGooglePlayServicesAvailability();
         //   initilizeStetho();

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
    public int getCurrentFragment() {
        return mCurrentFragment;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.logD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);

    }


    @Override
    protected void onStart() {
        Util.logD(LOG_TAG, "onStart");
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mResolvingConnectionError) {
            mGoogleApiClient.connect();
            Util.logD(LOG_TAG, "mGoogleApiClient.connect()");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GPSTrackerFragment.BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(launcherBroadcast,filter);
        if(mRadioChanged) {
            mRadioChanged = false;
            launchFragment(0);
            getTrackFragment().restartTimeCounter();
        }
    }

    @Override
    protected void onPause() {
        Util.logD(LOG_TAG, "onPause");
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(launcherBroadcast);
    }


    @Override
    protected void onStop() {
        Util.logD(LOG_TAG, "onStop");
        if(mGoogleApiClient != null ) {
            mGoogleApiClient.disconnect();
            Util.logD(LOG_TAG, "mGoogleApiClient.disconnect()");
        }
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
            mRadio = mSharedPreferences.getRadio();
            Intent intent = new Intent(this, SettingsActivity.class);
//            ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(this,null);
//            startActivityForResult(intent,REQUEST_CODE_SETTINGS,options.toBundle());
            startActivityForResult(intent,REQUEST_CODE_SETTINGS);
            overridePendingTransition(
                    R.anim.slide_in,R.anim.stay_exit);
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
                        launchFragment(option);
                    }
                }, 300);
                mCurrentFragment = option;
                return true;
            }
        });
    }


    private void launchFragment(int position) {
        Util.logD(LOG_TAG, "launchFragment");
        FragmentManager fragmentManager;

        switch (position) {
            case 0:
                try {

                    fragmentManager = getSupportFragmentManager();
                    mtabFragment = new TabLayoutFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment, mtabFragment)
                            .commit();

                } catch (IllegalStateException ignored) {
                    Util.logD(LOG_TAG, "IllegalStateException:" + ignored.getMessage());
                }

                break;
            case 1:

                fragmentManager = getSupportFragmentManager();
                FindFragment findFragment = new FindFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, findFragment)
                        .commit();


                break;
        }
    }







    public GoogleApiClient getLocationApiClient() {
        return mGoogleApiClient;
    }



    private GPSTrackerFragment getTrackFragment() {
        return (GPSTrackerFragment) getSupportFragmentManager().findFragmentByTag(GPS_FRAG);


    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Util.logD(LOG_TAG, "onConnected");
        GPSTrackerFragment trackerFragment = getTrackFragment();
        if (trackerFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new GPSTrackerFragment(), GPS_FRAG)
                    .commit();
        } else {
            trackerFragment.startTracking();
        }





    }


    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionError) {
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingConnectionError = true;
                connectionResult.startResolutionForResult(this,REQUEST_RESOLVE_CONNECTION_ERROR);
            } catch(IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingConnectionError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingConnectionError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_CONNECTION_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
//
//    @Override
//    public void onClickMap(MotionEvent event) {
//        Util.logD(LOG_TAG, "onClickMap");
//        MapTabFragment mapTabFragment = getMapTabFragment();
//        if (mapTabFragment != null) {
//            mapTabFragment.handleDispatchTouchEvent(event);
//        }
//    }

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
            mapTabFragment.updateClickedPhoneToPresenter(phone);
            mapTabFragment.removeMarkerInPresenter(phone);
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
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            } else {
              //  handlePermissionDenied();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPSTrackerFragment.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            } else {
                GPSTrackerFragment tracker =getTrackFragment();
                tracker.startTracking();
            }
        } else if( requestCode == REQUEST_RESOLVE_CONNECTION_ERROR) {
            mResolvingConnectionError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        } else if( requestCode == REQUEST_CODE_SETTINGS) {
            if (resultCode == RESULT_OK) {

                if(mRadio != mSharedPreferences.getRadio() && mCurrentFragment==0) {
                    launchFragment(0);
                    mRadioChanged =true;
                }
            }
        }


    }

    @Override
    protected void onDestroy() {
        Util.logD(LOG_TAG, "onDestroy");
        mUnbinder.unbind();
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }
}
