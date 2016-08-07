package com.chernandezgil.farmacias.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.adapter.ViewPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 06/08/2016.
 */
public class TabLayoutFragment extends Fragment implements TabLayout.OnTabSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private static final String LOG_TAG=TabLayoutFragment.class.getSimpleName();
    private static final String TAG_FRAGMENT = "TAB_FRAGMENT";

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @Inject
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private  Location mLocation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getMainActivityComponent().inject(this);
        if(savedInstanceState==null) {
            mGoogleApiClient.registerConnectionCallbacks(this);
            mGoogleApiClient.registerConnectionFailedListener(this);
            mGoogleApiClient.connect();
        } else {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView");
        View view=inflater.inflate(R.layout.fragment_tablayout,container,false);
        ButterKnife.bind(this,view);
        if(savedInstanceState!=null) {
            mLocation=savedInstanceState.getParcelable("location_key");
            setUpViewPager();
            setUpTabLayout();
        }

        return view;
    }

    public int getCurrentItem(){
        return mTabLayout.getSelectedTabPosition();
    }
    @Override
    public void onStart() {
        super.onStart();
        Util.LOGD(LOG_TAG, "onStart");
    }

    @Override
    public void onPause() {
        Util.LOGD(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Util.LOGD(LOG_TAG, "onStop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void setUpViewPager(){
    //    final PagerAdapter pagerAdapter=new Adapter(getActivity(),mLocation,getFragmentManager());
        final PagerAdapter pagerAdapter=new ViewPagerAdapter(getChildFragmentManager(),mLocation);
        mViewPager.setAdapter(pagerAdapter);
    }
    private void setUpTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position=tab.getPosition();
        switch(position){
            case 0:
                mViewPager.setCurrentItem(position);
                break;
            case 1:
                mViewPager.setCurrentItem(position);


        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, TabLayoutFragment.this);

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

    }

    @Override
    public void onLocationChanged(Location location) {
        Util.LOGD(LOG_TAG,"onLocationChanged");
        mLocation = location;
        setUpViewPager();
        setUpTabLayout();

    }

    public static class Adapter extends FragmentPagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        Location location;


        private Context context;
        public Adapter(Context context,Location location,FragmentManager fm) {

            super(fm);
            this.context=context;
            this.location=location;

        }



        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Bundle bundle=new Bundle();
                    bundle.putParcelable("location_key",location);
                    MapFragment mapFragment =new MapFragment();
                    mapFragment.setArguments(bundle);

                    return mapFragment;
                case 1:
                    return new ListFragment();


                default: return null;

            }

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "mapa";
                case 1:
                    return "lista";
                default: return null;

            }
        }
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
