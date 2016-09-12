package com.chernandezgil.farmacias.presenter;




import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;


import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;
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
    private static final int LOADER_RESULT =3;
    private static final int LOADER_QUICK_SEARCH = 4;
    private Handler mainHandler;
    private Location mLocation;

    public FindPresenter(Location location, LoaderManager loaderManager, LoaderProvider loaderProvider) {
        mLocation = location;
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

    /**
     * call this on orientation changes, as there is already a Loader, the system calls onLoadFinished() automatically
     * instead of going first to onCreateLoader and after to the ContentProvider
     */
    @Override
    public void onInitLoader() {
        Util.logD(LOG_TAG,"onInitLoader");
        mView.showLoading();
        mLoaderManager.initLoader(LOADER_RESULT, null, this);
    }

    /**
     *  Here, after restartLoader(), the system calls always onCreateLoader and after the ContentProvider
     * @param newText
     */
    @Override
    public void onRestartLoader(String newText) {
        mView.showLoading();
        mLoaderManager.restartLoader(LOADER_RESULT,createBundle(newText),this);
    }

    @Override
    public void onInitLoaderQuickSearch() {
        mLoaderManager.restartLoader(LOADER_QUICK_SEARCH,null,this);
    }

    @Override
    public void onRestartLoaderQuickSearch(String newText) {
        mLoaderManager.restartLoader(LOADER_QUICK_SEARCH,createBundle(newText),this);
    }

    private Bundle createBundle(String newText) {
        final Bundle bundle = new Bundle();
        bundle.putString("new_text",newText);
        return bundle;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (id == LOADER_RESULT) {
            Util.logD(LOG_TAG, "onCreateLoader:LOADER_RESULT");
            if (args != null) {
                return mLoaderProvider.getPharmaciesByName(args.getString("new_text"));
            } else {
                return  mLoaderProvider.getPharmacies();
            }
        }
        if (id == LOADER_QUICK_SEARCH) {
            Util.logD(LOG_TAG, "onCreateLoader:LOADER_QUICK_SEARCH");
            if (args !=null) {
                return mLoaderProvider.getPharmaciesByNameQuickSearch(args.getString("new_text"));
            }
        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if(loader.getId() == LOADER_RESULT) {
            Util.logD(LOG_TAG,"onLoadFinished_LOADER_RESULT");
            Util.logD(LOG_TAG,data.toString());


            new Thread() {
                @Override
                public void run() {
                    bindView(data);

                }
            }.start();
        }
        if(loader.getId() == LOADER_QUICK_SEARCH) {
            Util.logD(LOG_TAG,"onLoadFinished_LOADER_QUICK_SEARCH");
            new Thread() {
                @Override
                public void run() {
                    bindViewQuickSearch(data);

                }
            }.start();

        }
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
                    mView.hideNoResults();
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
                farmacia.setPhoneFormatted(Util.formatPhoneNumber(phone));
                double latDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
                double lonDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));
                double distance = Util.meterDistanceBetweenPoints(latDest, lonDest, mLocation.getLatitude(), mLocation.getLongitude());
                farmacia.setLat(latDest);
                farmacia.setLon(lonDest);
                farmacia.setDistance(distance / 1000);

                String hours = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS));
                farmacia.setHours(hours);
                farmacia.setOpen(Util.isPharmacyOpen(hours));
                String addressFormatted = Util.formatAddress(farmacia.getAddress(),
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

    private void bindViewQuickSearch(Cursor data) {
        Util.logD(LOG_TAG,"bindViewQuickSearch");
        if(data == null) return;
        if (data.isClosed()) return;

        final List<SuggestionsBean> list = transformQuickSearchCursor(data);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                if(list !=null && list.size()>0) {

                    mView.showResultsQuickSearch(list);
                } else {
                    mView.showNoResultsQuickSearch();
                }
            }
        });

    }

    private List<SuggestionsBean> transformQuickSearchCursor(Cursor data) {
        List<SuggestionsBean> list = new ArrayList<>(data.getCount());
        if (data.moveToFirst()) {
            do {

                SuggestionsBean suggestionsBean = new SuggestionsBean();
                suggestionsBean.setImageId(data.getInt(0));
                suggestionsBean.setName(data.getString(1));
                list.add(suggestionsBean);
            } while (data.moveToNext());
        }
        return list;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
