package com.chernandezgil.farmacias.ui.adapter;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.chernandezgil.farmacias.ui.fragment.ListTabFragment;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;

/**
 * Created by Carlos on 07/08/2016.
 */
public class ViewPagerAdapter extends PagerAdapter {
    FragmentManager fragmentManager;
    Fragment[] fragments;
    Location location;

    public ViewPagerAdapter(FragmentManager fm, Location location) {
        fragmentManager = fm;
        fragments = new Fragment[2];
        this.location = location;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        assert (0 <= position && position < fragments.length);
//        FragmentTransaction trans = fragmentManager.beginTransaction();
//        trans.remove(fragments[position]);
//
//            trans.commit();
//        fragments[position] = null;
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = getItem(position);
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.add(container.getId(), fragment, "fragment:" + position);
        trans.commit();
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object fragment) {
        return ((Fragment) fragment).getView() == view;
    }

    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("location_key", location);
        if (fragments[position] == null && position == 0) {

            MapTabFragment mapTabFragment = new MapTabFragment();
            mapTabFragment.setArguments(bundle);
            fragments[position] = mapTabFragment; //make your fragment here

        } else if (fragments[position] == null && position == 1) {

            ListTabFragment listTabFragment = new ListTabFragment();
            listTabFragment.setArguments(bundle);
            fragments[position] = listTabFragment;

        }

        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "mapa";
            case 1:
                return "lista";
            default:
                return null;

        }
    }
}