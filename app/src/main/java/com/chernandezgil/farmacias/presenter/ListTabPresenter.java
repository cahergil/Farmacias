package com.chernandezgil.farmacias.presenter;


import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.ListTabContract;
import com.github.davidmoten.rx.Transformers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListTabPresenter implements ListTabContract.Presenter<ListTabContract.View>,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG=ListTabPresenter.class.getSimpleName();
    private final LoaderManager mLoaderManager;
    private ListTabContract.View mView;
    private LoaderProvider mLoaderProvider;
    private Geocoder mGeocoder;
    private Location mLocation;
    private List<Pharmacy> mFarmaciasList;
    private int FARMACIAS_LOADER=1;
    Handler mainHandler;

    private PreferencesManager preferencesManager;
    private int mRadio;
    public ListTabPresenter(Location location, LoaderProvider loaderProvider, LoaderManager loadermanager,
                            Geocoder geocoder,PreferencesManager preferencesManager
                            ) {
        mLocation=location;
        mLoaderProvider=loaderProvider;
        mLoaderManager=loadermanager;
        mGeocoder=geocoder;
        this.preferencesManager=preferencesManager;
        mRadio=this.preferencesManager.retrieveRadioBusquedaFromSp()*1000;
        mainHandler = new Handler(Looper.getMainLooper());


    }
    @Override
    public void setView(ListTabContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mView=view;
    }

    @Override
    public void detachView() {
        mView=null;
    }

    @Override
    public void onStartLoader() {
        mView.showLoading();
        mLoaderManager.restartLoader(FARMACIAS_LOADER,null,this);

    }

    @Override
    public void onGetAddressFromLocation(Location location) {
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioe) {

        }

        if (addresses == null || addresses.size() == 0) {
            Util.LOGD(LOG_TAG, "no address found");
            mView.setAddress(null);
        } else {
            Address address = addresses.get(0);
            StringBuilder stringBuilder = new StringBuilder();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                stringBuilder.append(address.getAddressLine(i));
                if (i != address.getMaxAddressLineIndex() - 1) {
                    stringBuilder.append(Constants.COMMA);
                }
            }
            Util.LOGD(LOG_TAG, "address found");

            mView.setAddress(stringBuilder.toString());
        }

    }

    private void bindView(Cursor data) {
        mFarmaciasList=new ArrayList<>();

        if (data.isClosed()) return;
        if (data.moveToFirst()) {
            do {

                Pharmacy farmacia = new Pharmacy();

                double latDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
                double lonDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));

                double distance = Util.meterDistanceBetweenPoints(latDest, lonDest, mLocation.getLatitude(), mLocation.getLongitude());
                farmacia.setName(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
                farmacia.setAddress(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.ADDRESS)));
                farmacia.setLocality(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.LOCALITY)));
                farmacia.setProvince(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PROVINCE)));
                farmacia.setPostal_code(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.POSTAL_CODE)));
                String phone = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PHONE));
                farmacia.setPhone(phone);
                farmacia.setLat(latDest);
                farmacia.setLon(lonDest);
                farmacia.setDistance(distance);
                String hours = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS));
                farmacia.setHours(hours);
                farmacia.setOpen(Util.isPharmacyOpen(hours));
                String addressFormatted = Util.formatAddress(farmacia.getAddress(),
                        farmacia.getPostal_code(),
                        farmacia.getLocality(),
                        farmacia.getProvince());
                farmacia.setAddressFormatted(addressFormatted);
                farmacia.setArrow_down(true);

                boolean favorite;
                int j=data.getInt(data.getColumnIndex(DbContract.FarmaciasEntity.FAVORITE));
                if (j==0){
                    favorite=false;
                } else {
                    favorite=true;
                }

                farmacia.setFavorite(favorite);
                mFarmaciasList.add(farmacia);

            } while (data.moveToNext());
        }
        mFarmaciasList = toFilteredSortedOrderedList(mFarmaciasList);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.hideLoading();
                if(mFarmaciasList !=null && mFarmaciasList.size()>0) {
                    mView.showResults(mFarmaciasList);
                } else {
                    mView.showNoResults();
                }
            } // This is your code
        });





    }
    /**
     * Transformers.mapWithIndex https://github.com/ReactiveX/RxJava/issues/3602
     * rxjava-extras
     *
     * @param list
     * @return
     */
    public List<Pharmacy> toFilteredSortedOrderedList(List<Pharmacy> list) {

        return Observable.from(list)
                .filter(f -> {
                    if (f.getDistance() <mRadio) {
                        return true;
                    }
                    return false;
                })
                .toSortedList()
                .flatMap(s -> Observable.from(s))
                .compose(Transformers.mapWithIndex())
                .map(t -> {
                    t.value().setOrder(Util.characterFromInteger((int) t.index()));
                    return t.value();
                })
                .toList()
                .toBlocking().first();

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FARMACIAS_LOADER) {
            return mLoaderProvider.getPharmaciesNearby();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == FARMACIAS_LOADER) {
            new Thread() {
                @Override
                public void run() {
                    bindView(data);

                }
            }.start();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
