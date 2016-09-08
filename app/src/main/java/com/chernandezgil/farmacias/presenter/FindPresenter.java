package com.chernandezgil.farmacias.presenter;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.view.FindContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 05/09/2016.
 */

public class FindPresenter implements FindContract.Presenter<FindContract.View>,LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = FindPresenter.class.getSimpleName();
    private FindContract.View mView;
    private LoaderManager mLoaderManager;
    private LoaderProvider mLoaderProvider;
    private static final int LOADER =3;
    private Handler mainHandler;
    private List<Pharmacy> mList;
    boolean flag=true;

    public FindPresenter(LoaderManager loaderManager, LoaderProvider loaderProvider) {
        mLoaderManager = loaderManager;
        mLoaderProvider = loaderProvider;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void setView(FindContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onStartLoader() {
        Util.logD(LOG_TAG,"onStartLoader");
        mView.showLoading();
//        Loader<Cursor> loader= mLoaderManager.getLoader(LOADER);
//        if(loader!=null) {
//            if(loader.isStarted()) loader.forceLoad();
//        } else {
            mLoaderManager.restartLoader(LOADER, null, this);
//        }
    }

    @Override
    public void onRestartLoader(String newText) {
        mView.showLoading();
        final Bundle bundle = new Bundle();
        bundle.putString("new_text",newText);
        mLoaderManager.restartLoader(LOADER,bundle,this);
    }


    private void bindView(Cursor data) {
        Util.logD(LOG_TAG,"bindView");
        mList =new ArrayList<>();

        if (data.isClosed()) return;

        if (data.moveToFirst()) {
            do {

                Pharmacy farmacia = new Pharmacy();

                double latDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
                double lonDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));


                farmacia.setName(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
                farmacia.setAddress(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.ADDRESS)));
                farmacia.setLocality(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.LOCALITY)));
                farmacia.setProvince(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PROVINCE)));
                farmacia.setPostal_code(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.POSTAL_CODE)));
                String phone = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PHONE));
                farmacia.setPhone(phone);
                farmacia.setPhoneFormatted(Util.formatPhoneNumber(phone));
                farmacia.setLat(latDest);
                farmacia.setLon(lonDest);

                String hours = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS));
                farmacia.setHours(hours);
                farmacia.setOpen(Util.isPharmacyOpen(hours));
                String addressFormatted = Util.formatAddress(farmacia.getAddress(),
                        farmacia.getPostal_code(),
                        farmacia.getLocality(),
                        farmacia.getProvince());
                farmacia.setAddressFormatted(addressFormatted);


                boolean favorite;
                int j=data.getInt(data.getColumnIndex(DbContract.FarmaciasEntity.FAVORITE));
                if (j==0){
                    favorite=false;
                } else {
                    favorite=true;
                }

                farmacia.setFavorite(favorite);
                mList.add(farmacia);

            } while (data.moveToNext());
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.hideLoading();
                if(mList !=null && mList.size()>0) {
                    mView.hideNoResults();
                    mView.showResults(mList);
                } else {
                    mView.showNoResults();
                }
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (id == LOADER) {
            Util.logD(LOG_TAG, "onCreateLoader");
            if (args != null) {
                return mLoaderProvider.getPharmaciesByName(args.getString("new_text"));
            }
//            } else {
//                return  mLoaderProvider.getPharmacies();
//            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Util.logD(LOG_TAG,"onLoadFinished");
        if(loader.getId() == LOADER) {
            bindView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
