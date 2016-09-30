package com.chernandezgil.farmacias.presenter;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.view.FavoriteContract;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 28/09/2016.
 */

public class FavoritePresenter implements FavoriteContract.Presenter<FavoriteContract.View>,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = FavoritePresenter.class.getSimpleName();
    private FavoriteContract.View mView;
    private Location mLocation;
    private LoaderManager mLoaderManager;
    private LoaderProvider mLoaderProvider;
    private static final int LOADER = 6;
    private Handler mainHandler;

    public FavoritePresenter(Location location, LoaderManager loaderManager, LoaderProvider loaderProvider) {
        mLocation = location;
        mLoaderManager = loaderManager;
        mLoaderProvider = loaderProvider;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void setView(FavoriteContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onInitLoader() {
        Utils.logD(LOG_TAG,"onInitLoader");
        mLoaderManager.initLoader(LOADER,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER) {
            return mLoaderProvider.getFavoritesPharmacies();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER) {
            bindView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void bindView(Cursor data) {
        if(data == null) return;
        if (data.isClosed()) return;

        final List<Pharmacy> list= transformSearchCursor(data);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.hideLoading();
                if(list !=null && list.size()>0) {

                    mView.showResults(list);
                } else {
                    mView.showNoResults();
                }
            }
        });
    }

    private List<Pharmacy> transformSearchCursor(Cursor data){
        List<Pharmacy> list = new ArrayList<Pharmacy>(data.getCount());
        if(data.moveToFirst()) {
            do {
                Pharmacy farmacia = new Pharmacy();
                farmacia.setName(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
                farmacia.setAddress(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.ADDRESS)));
                farmacia.setLocality(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.LOCALITY)));
                farmacia.setProvince(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PROVINCE)));
                farmacia.setPostal_code(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.POSTAL_CODE)));
                String phone = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PHONE));
                farmacia.setPhone(phone);
                farmacia.setPhoneFormatted(Utils.formatPhoneNumber(phone));
                double latDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
                double lonDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));
                double distance = SphericalUtil.computeDistanceBetween(new LatLng(latDest,lonDest),
                        new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                //double distance = Utils.meterDistanceBetweenPoints(latDest, lonDest, mLocation.getLatitude(), mLocation.getLongitude());
                farmacia.setLat(latDest);
                farmacia.setLon(lonDest);
                farmacia.setDistance(distance / 1000);

                String hours = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS));
                farmacia.setHours(hours);
                farmacia.setOpen(Utils.isPharmacyOpen(hours));
                String addressFormatted = Utils.formatAddress(farmacia.getAddress(),
                        farmacia.getPostal_code(),
                        farmacia.getLocality(),
                        farmacia.getProvince());
                farmacia.setAddressFormatted(addressFormatted);


                boolean favorite;
                int j = data.getInt(data.getColumnIndex(DbContract.FarmaciasEntity.FAVORITE));
                if (j == 0) {
                    favorite = false;
                } else {
                    favorite = true;
                }

                farmacia.setFavorite(favorite);
                list.add(farmacia);
            } while (data.moveToNext());
        }
        return list;

    }
}
