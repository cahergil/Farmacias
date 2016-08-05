package com.chernandezgil.farmacias.presenter;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;

import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.view.MainActivityContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MainActivityPresenter implements MainActivityContract.Presenter<MainActivityContract.View> {

    private static final String LOG_TAG=MainActivityPresenter.class.getSimpleName();
    private MainActivityContract.View mMainActivityView;
    private Geocoder mGeocoder;


    public MainActivityPresenter(){
    }


    @Override
    public void setView(MainActivityContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mMainActivityView =view;
    }

    @Override
    public void detachView() {

        mMainActivityView =null;

    }


}
