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


    public MainActivityPresenter(Geocoder geocoder){
        mGeocoder=geocoder;
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

    @Override
    public String onGetAddressFromLocation(Location location) {
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioe) {

        }

        if (addresses == null || addresses.size()  == 0) {
            Util.LOGD(LOG_TAG,"no address found");
            return null;
        }else {
            Address address = addresses.get(0);
            StringBuilder stringBuilder=new StringBuilder();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                stringBuilder.append(address.getAddressLine(i));
                if(i!=address.getMaxAddressLineIndex()-1) {
                    stringBuilder.append(Constants.COMMA);
                }
            }
            Util.LOGD(LOG_TAG,"address found");
            return stringBuilder.toString();
        }

    }
}
