package com.chernandezgil.farmacias.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import com.aitorvs.android.allowme.AllowMeActivity;
import com.chernandezgil.farmacias.SettingsActivity;
import com.chernandezgil.farmacias.presenter.MainActivityPresenter;
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
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AllowMeActivity implements
                 MainActivityContract.View {


    @BindView(R.id.navigation_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindDrawable(R.drawable.ic_menu_white_24dp)
    Drawable menuDrawable;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private ActionBar actionBar;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG_FRAGMENT = "TAB_FRAGMENT";

    private MainActivityPresenter mMainActivityPresenter;





    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private TabLayoutFragment mtabFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainActivityPresenter = new MainActivityPresenter();
        mMainActivityPresenter.setView(this);
        setUpToolBar();


        if (savedInstanceState == null) {
            checkGooglePlayServicesAvailability();
            Stetho.initializeWithDefaults(this);
            launchDownloadService();
            setFragment(0);
        }
        setupNavigationDrawerContent(navigationView);




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);

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
                mtabFragment = new TabLayoutFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, mtabFragment)
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
                MapTabFragment mapTabFragment = (MapTabFragment) tabs.getChildFragmentManager().findFragmentByTag("fragment:0");
                if(mapTabFragment !=null && ((TabLayoutFragment)tabs).getCurrentItem()==0) {
                    return mapTabFragment;
                }
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
    @Override
    protected void onDestroy() {
        Util.LOGD(LOG_TAG,"onDestroy");
        mMainActivityPresenter.detachView();
        super.onDestroy();
    }


}
