package com.chernandezgil.farmacias.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.ui.activity.MainActivity;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 06/08/2016.
 */
public class TabLayoutFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final String LOG_TAG = TabLayoutFragment.class.getSimpleName();
    private static final String TAG_FRAGMENT = "TAB_FRAGMENT";
    private int mOffset;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;


    private Adapter pagerAdapter = null;
    private PreferencesManager mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        if (savedInstanceState != null) {
            mSharedPreferences.setCurrentItemTabLayout(savedInstanceState.getInt("current_item_key"));
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Utils.logD(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("current_item_key", mViewPager.getCurrentItem());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_tablayout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOffset =(int) Utils.convertDpToPixel(112,getActivity());
        Utils.logD(LOG_TAG, "onActivityCreated");
        setUpViewPager();
        setUpTabLayout();
        mViewPager.setCurrentItem(mSharedPreferences.getCurrentItemTabLayout());
        //place this exactly here, otherwise onSelectedTag gets called on launch.
        mTabLayout.addOnTabSelectedListener(this); //24.0.0
    }

    public int getCurrentItem() {
        return mTabLayout.getSelectedTabPosition();
    }


    @Override
    public void onStart() {
        super.onStart();
        Utils.logD(LOG_TAG, "onStart");
    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.logD(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop");

        super.onStop();
    }

    private void setUpViewPager() {
        pagerAdapter = new Adapter(getContext(), getChildFragmentManager());

        mViewPager.setAdapter(pagerAdapter);


    }

    private void setUpTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
        //   mTabLayout.setOnTabSelectedListener(this);
        //mTabLayout.addOnTabSelectedListener(this); //24.0.0
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (position == 1) {
            Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
            if (toolbar != null) {

                int[] toolbarLocation= new int[2];
                toolbar.getLocationInWindow(toolbarLocation);
//                int[] location1=new int[2];
//                toolbar.getLocationOnScreen(location1);
//
//                Utils.logD(LOG_TAG,"window[x]:"+toolbarLocation[0]+",screen[x]:"+location1[0]);
//                Utils.logD(LOG_TAG,"window[y]:"+toolbarLocation[1]+",screen[y]:"+location1[1]);

//                Rect rectGlobal =new Rect();
//                Rect rectLocal =new Rect();
//                toolbar.getGlobalVisibleRect(rectGlobal);
//                toolbar.getLocalVisibleRect(rectLocal);
//                Utils.logD(LOG_TAG,"Globl:left="+rectGlobal.left+",top="+rectGlobal.top);
//                Utils.logD(LOG_TAG,"Globl:left="+rectLocal.left+",top="+rectLocal.top);
                List<Fragment> lista =getChildFragmentManager().getFragments();
                MapTabFragment f=null;
                for (int i = 0; i <lista.size() ; i++) {
                    Fragment fragment= lista.get(i);
                    if(fragment instanceof MapTabFragment) {
                        f =(MapTabFragment) fragment;
                        break;
                    }
                }
                //toolbar is off the screen
                if (toolbarLocation[1] <= 0 && f!=null) {
                    f.translateYBottomSheet(mOffset);

                //toolbar is visible
                } else if(toolbarLocation[1] > 0 && f!=null) {
                    f.setBottomSheetPosition();

                }



            }
        }
        mViewPager.setCurrentItem(position);
        mSharedPreferences.setCurrentItemTabLayout(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public SparseArray<Fragment> getFragments() {
        return pagerAdapter.getRegisteredFragment();
    }

    //to remember: https://code.google.com/p/android/issues/detail?id=69586
    public static class Adapter extends FragmentPagerAdapter {
        public SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private Context context;

        public Adapter(Context ctxt, FragmentManager fm) {

            super(fm);
            // Utils.logD(LOG_TAG,"Adapter");

            context = ctxt;

        }

        public SparseArray<Fragment> getRegisteredFragment() {
            SparseArray<Fragment> copy = new SparseArray<>();
            for (int i = 0; i < registeredFragments.size(); i++) {
                copy.put(i, registeredFragments.get(i));
            }
            return copy;

        }


        @Override
        public Fragment getItem(int position) {
            //Utils.logD(LOG_TAG,"getItem:"+position);
            switch (position) {
                case 0:
                    return new ListTabFragment();

                case 1:
                    return new MapTabFragment();
                default:
                    return null;

            }


        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            //Utils.logD(LOG_TAG,"instantiateItem:"+position);
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragment.setUserVisibleHint(true);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //Utils.logD(LOG_TAG,"destroyItem:"+position);
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            //Utils.logD(LOG_TAG,"getCount");
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Utils.logD(LOG_TAG,"getPageTitle:"+position);
            switch (position) {
                case 0:
                    return context.getString(R.string.tlf_tab_list_string);

                case 1:
                    return context.getString(R.string.tlf_tab_map_string);

                default:
                    return null;

            }
        }


    }

    @Override
    public void onDestroy() {
        Utils.logD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
