package com.chernandezgil.farmacias.presenter;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.view.ListContract;
import com.github.davidmoten.rx.Transformers;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListPresenter implements ListContract.Presenter<ListContract.View>,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final LoaderManager mLoaderManager;
    private ListContract.View mView;
    private LoaderProvider mLoaderProvider;

    private Location mLocation;

    private int FARMACIAS_LOADER=1;

    public ListPresenter(Location location, LoaderProvider loaderProvider,LoaderManager loadermanager) {
        mLocation=location;
        mLoaderProvider=loaderProvider;
        mLoaderManager=loadermanager;
    }
    @Override
    public void setView(ListContract.View view) {
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

    private void bindView(Cursor data) {
        List<Pharmacy> farmaciasList = new ArrayList<>();

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
                farmacia.setPhone(Util.formatPhoneNumber(phone));
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


                farmaciasList.add(farmacia);

            } while (data.moveToNext());
        }
        farmaciasList = toFilteredSortedOrderedList(farmaciasList);
        mView.hideLoading();
        if(farmaciasList.size()>0) {
            mView.showResults(farmaciasList);
        } else {
            mView.showNoResults();
        }



    }
    /**
     * Transformers.mapWithIndex https://github.com/ReactiveX/RxJava/issues/3602
     * rxjava-extras
     *
     * @param list
     * @return
     */
    public List<Pharmacy> toFilteredSortedOrderedList(List<Pharmacy> list) {
        String str;

        return Observable.from(list)
                .filter(f -> {
                    if (f.getDistance() < 10000) {
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
            bindView(data);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
