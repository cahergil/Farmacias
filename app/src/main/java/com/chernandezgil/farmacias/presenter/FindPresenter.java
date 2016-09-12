package com.chernandezgil.farmacias.presenter;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


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
    private List<Pharmacy> mList;
    boolean flag=true;
    private List<SuggestionsBean> mListQuickSearch;
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
    public void onStartLoaderQuickSearch(String newText) {
        mLoaderManager.restartLoader(LOADER_QUICK_SEARCH,createBundle(newText),this);
    }

    private Bundle createBundle(String newText) {
        final Bundle bundle = new Bundle();
        bundle.putString("new_text",newText);
        return bundle;
    }

    private void bindView(Cursor data) {
        Util.logD(LOG_TAG,"bindView");
        mList =new ArrayList<>();

        if (data.isClosed()) return;

//        Util.logD(LOG_TAG,"pre_position:"+data.getPosition());
//        data.moveToPosition(-1);
//        Util.logD(LOG_TAG,"post_position:"+data.getPosition());
////        if(data.isLast()) {
////            while(data.moveToPrevious()) {
////                //do nothing
////            }
////        }
//        Util.logD(LOG_TAG,"count:"+data.getCount());
        if(data.moveToFirst()) {
        Util.logD(LOG_TAG,"moveToFirst:");
            do {
                try {
                    handleCursor(data);
                }catch (Exception ignored){}
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

    private void handleCursor(Cursor data){
        Util.logD(LOG_TAG,"cursor position:"+data.getPosition());
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
        mList.add(farmacia);
    }

    private void bindViewQuickSearch(Cursor data) {
        Util.logD(LOG_TAG,"bindViewQuickSearch");
        if(data == null) return;
        if (data.isClosed()) return;
        mListQuickSearch = new ArrayList<>();

        if (data.moveToFirst()) {
            do {

                SuggestionsBean suggestionsBean = new SuggestionsBean();
                suggestionsBean.setImageId(data.getInt(0));
                suggestionsBean.setName(data.getString(1));
                mListQuickSearch.add(suggestionsBean);
            } while (data.moveToNext());
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
               // mView.hideLoading();
                if(mListQuickSearch !=null && mListQuickSearch.size()>0) {
                  //  mView.hideNoResults();
                    mView.showResultsQuickSearch(mListQuickSearch);
                } else {
                    mView.showNoResultsQuickSearch();
                }
            }
        });

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (id == LOADER_RESULT) {
            Util.logD(LOG_TAG, "onCreateLoader:LOADER_RESULT");
            if (args != null) {
                return mLoaderProvider.getPharmaciesByName1(args.getString("new_text"));
            } else {
                return  mLoaderProvider.getPharmacies1();
            }
        }
        if (id == LOADER_QUICK_SEARCH) {
            Util.logD(LOG_TAG, "onCreateLoader:LOADER_QUICK_SEARCH");
            if (args !=null) {
                return mLoaderProvider.getPharmaciesByNameQuickSearch1(args.getString("new_text"));
            }
        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if(loader.getId() == LOADER_RESULT) {
            Util.logD(LOG_TAG,"onLoadFinished_LOADER_RESULT");
            Util.logD(LOG_TAG,data.toString());

//            if(data.moveToFirst()) {
//
//            }
     //       new Thread() {
     //           @Override
     //           public void run() {
                //    bindView(data);

     //           }
     //       }.start();
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
