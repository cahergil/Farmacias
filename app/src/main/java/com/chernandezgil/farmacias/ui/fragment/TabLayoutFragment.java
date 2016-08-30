package com.chernandezgil.farmacias.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
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
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.adapter.ViewPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

/**
 * Created by Carlos on 06/08/2016.
 */
public class TabLayoutFragment extends Fragment implements TabLayout.OnTabSelectedListener{

    private static final String LOG_TAG=TabLayoutFragment.class.getSimpleName();
    private static final String TAG_FRAGMENT = "TAB_FRAGMENT";

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;


    private  Location mLocation;
    private PagerAdapter pagerAdapter=null;
    private PreferencesManager mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mSharedPreferences=new AndroidPrefsManager(getContext());
        if(savedInstanceState==null) {
            Bundle bundle=getArguments();
            if(bundle!=null) {
                mLocation=bundle.getParcelable("location_key");
            }
        } else {
           mLocation=savedInstanceState.getParcelable("location_key");
           mSharedPreferences.setCurrentItemTabLayout(savedInstanceState.getInt("current_item_key"));
        }
        Util.LOGD(LOG_TAG,"****main thread?:"+(Looper.myLooper() == Looper.getMainLooper()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Util.LOGD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
        outState.putInt("current_item_key",mViewPager.getCurrentItem());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView");
        View view=inflater.inflate(R.layout.fragment_tablayout,container,false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Util.LOGD(LOG_TAG, "onActivityCreated");
        setUpViewPager();
        setUpTabLayout();
        mViewPager.setCurrentItem(mSharedPreferences.getCurrentItemTabLayout());
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

        super.onStop();
    }

    private void setUpViewPager(){
        pagerAdapter=new Adapter(getActivity(),mLocation,getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);


    }
    private void setUpTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager);
     //   mTabLayout.setOnTabSelectedListener(this);
        mTabLayout.addOnTabSelectedListener(this); //24.0.0
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position=tab.getPosition();
        mViewPager.setCurrentItem(position);
        mSharedPreferences.setCurrentItemTabLayout(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    public SparseArray<Fragment> getFragments(){
        return Adapter.registeredFragments;
    }
    //to remember: https://code.google.com/p/android/issues/detail?id=69586
    public static class Adapter extends FragmentPagerAdapter {
        public static SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        Location location;


        private Context context;
        public Adapter(Context context,Location location,FragmentManager fm) {

            super(fm);
            this.context=context;
            this.location=location;

        }



        @Override
        public Fragment getItem(int position) {
            Bundle bundle=new Bundle();
            bundle.putParcelable("location_key",location);
            switch (position) {
                case 0:
                    ListTabFragment listTabFragment=new ListTabFragment();
                    listTabFragment.setArguments(bundle);
                    return listTabFragment;
                case 1:

                    MapTabFragment mapTabFragment =new MapTabFragment();
                    mapTabFragment.setArguments(bundle);
                    return mapTabFragment;

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
                    return context.getString(R.string.tlf_tab_list_string);
                case 1:
                    return context.getString(R.string.tlf_tab_map_string);
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
