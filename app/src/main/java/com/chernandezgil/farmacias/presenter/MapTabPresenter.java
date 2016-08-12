package com.chernandezgil.farmacias.presenter;


import android.content.SharedPreferences;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.MapContract;
import com.github.davidmoten.rx.Transformers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Carlos on 03/08/2016.
 */
public class MapTabPresenter implements MapContract.Presenter<MapContract.View>, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MapTabPresenter.class.getSimpleName();
    private MapContract.View mView;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;
    private Geocoder mGeocoder;
    private static final int FARMACIAS_LOADER = 1;
    private HashMap mMarkersHashMap;
    private Location mLocation;
    private String mAddress;
    private CustomMarker mLastClickMarker;
    private CustomMarker mUserUbicationMarker;

    private Bitmap markerBitmap;
    private CustomCameraUpdate mCameraUpdate;
    private PreferencesManager preferencesManager;
    private int mRadio;
    @Inject
    public MapTabPresenter(@NonNull LoaderProvider loaderProvider,
                           @NonNull LoaderManager loaderManager,
                           @NonNull Geocoder geocoder, PreferencesManager preferencesManager) {
        mLoaderProvider = checkNotNull(loaderProvider, "loader provider cannot be null");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mGeocoder = checkNotNull(geocoder, "geocoder cannot be null");
        this.preferencesManager = preferencesManager;
        mRadio=this.preferencesManager.retrieveRadioBusquedaFromSp()*1000;
        mCameraUpdate=new CustomCameraUpdate();


    }

    @Override
    public void setView(MapContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mView = view;


    }

    @Override
    public void onSetMarkerBitMap(Bitmap markerBitmap) {
        this.markerBitmap = markerBitmap;
    }

    @Override
    public CustomCameraUpdate getCameraUpdate() {
        return mCameraUpdate;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onStartLoader() {

        mLoaderManager.restartLoader(FARMACIAS_LOADER, null, this);
    }

    @Override
    public void onAddMarkerToHash(Marker marker, CustomMarker pharmacy) {
        setUpMarkersHashMap();
        mMarkersHashMap.put(marker, pharmacy);
    }

    @Override
    public HashMap onGetHashMap() {
        return mMarkersHashMap;
    }

    @Override
    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public void onSetLastMarkerClick(CustomMarker customMarker) {
        mLastClickMarker = customMarker;
    }

    @Override
    public LatLng onGetDestinationLocale() {
        return new LatLng(mLastClickMarker.getLat(), mLastClickMarker.getLon());

    }

    @Override
    public String onGetDestinationAddress() {
        return mLastClickMarker.getAddressFormatted();
    }

    @Override
    public String onGetDestinationPhoneNumber() {
        return mLastClickMarker.getPhone();
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

        if (addresses == null || addresses.size() == 0) {
            Util.LOGD(LOG_TAG, "no address found");
            return null;
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
            return stringBuilder.toString();
        }


    }

    @Override
    public void onSetAddress(String address) {
        mAddress=address;
    }

    @Override
    public Bitmap onRequestCustomBitmap(String order, boolean isOpen) {
        ColorMatrix matrix = new ColorMatrix();
        float saturation = 0.1f;
        if (isOpen) {
            saturation = 0.8f;
        }
        matrix.setSaturation(saturation);
        ColorFilter paintColorFilter = new ColorMatrixColorFilter(matrix);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(144, 144, conf);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setTextSize(38);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        paint.setColor(Color.parseColor("#FFCDD2"));
        paint.setColorFilter(paintColorFilter);

        Rect boundsText = new Rect();
        paint.getTextBounds(order, 0, order.length(), boundsText);
        if (boundsText.width() > 40) {
            paint.setTextSize(25);
        }
        paint.getTextBounds(order, 0, order.length(), boundsText);
        int x = (bmp.getWidth() - boundsText.width()) / 2;
        //    int y=(bmp.getHeight()- boundsText.height())/2;
        //    Log.d(LOG_TAG,"boundsText.width()"+boundsText.width()+",bmp.with()"+bmp.getWidth());
        //    Log.d(LOG_TAG,"letra:"+order);

        //  canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
        //          R.drawable.ic_maps_position), 0,0, paint);
        canvas.drawBitmap(markerBitmap, 0, 0, paint);
        //  canvas.drawText(order, x-4,69, paint);
        canvas.drawText(order, x, 115, paint);

        return bmp;
    }







    private void bindView(Cursor data) {

        List<CustomMarker> farmaciasList = new ArrayList<>();
        CustomMarker firstSortedPharmacy = null;
        if (data.isClosed()) return;
        if (data.moveToFirst()) {
            do {

                CustomMarker farmacia = new CustomMarker();

                double latDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
                double lonDest = data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));
                //float dist=calculateDistance(latDest,lonDest,mLocation);
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
                boolean favorite;
                int j=data.getInt(data.getColumnIndex(DbContract.FarmaciasEntity.FAVORITE));
                if (j==0){
                    favorite=false;
                } else {
                    favorite=true;
                }
                farmacia.setFavorite(favorite);

                farmaciasList.add(farmacia);

            } while (data.moveToNext());
        }
        farmaciasList = toFilteredSortedOrderedList(farmaciasList);
        for (int i = 0; i < farmaciasList.size(); i++) {
            if (i == 0) {
                firstSortedPharmacy = farmaciasList.get(i);
            }
            mView.addMarkerToMap(farmaciasList.get(i));

        }

        CustomMarker userLocation = getUserLocationMarker();
        mUserUbicationMarker = userLocation;
        mView.addMarkerToMap(userLocation);
        if (firstSortedPharmacy != null) {
            mView.displayPharmacyInBottomSheet(firstSortedPharmacy, mLastClickMarker);
        }

        zoomAnimateLevelToFitMarkers(120);
    }

    private CustomMarker getUserLocationMarker() {
        CustomMarker userLocation = new CustomMarker();
        userLocation.setName("userLocation");
        userLocation.setLat(mLocation.getLatitude());
        userLocation.setLon(mLocation.getLongitude());
        userLocation.setAddressFormatted(mAddress);
        //for hascode and equals
        userLocation.setOrder("A");
        userLocation.setDistance(0d);
        return userLocation;
    }

    /**
     * Transformers.mapWithIndex https://github.com/ReactiveX/RxJava/issues/3602
     * rxjava-extras
     *
     * @param list
     * @return
     */
    public List<CustomMarker> toFilteredSortedOrderedList(List<CustomMarker> list) {
        String str;

        return Observable.from(list)
                .filter(f -> {
                    if (f.getDistance() < mRadio) {
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
                }).map(f -> {
                    f.setMarkerImage(onRequestCustomBitmap(f.getOrder(), f.isOpen()));
                    return f;
                })
                .toList()
                .toBlocking().first();

    }


    public void zoomAnimateLevelToFitMarkers(int padding) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        Iterator<Map.Entry> iter = mMarkersHashMap.entrySet().iterator();
        int markerCounter=0;
        while (iter.hasNext()) {
            markerCounter++;
            Map.Entry mEntry = (Map.Entry) iter.next();
            Marker key = (Marker) mEntry.getKey();

            CustomMarker c = (CustomMarker) mMarkersHashMap.get(key);
            LatLng ll = new LatLng(c.getLat(), c.getLon());
            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        if(markerCounter==1) {
            mCameraUpdate.setmCameraUpdate(CameraUpdateFactory.newLatLng(new LatLng(mLocation.getLatitude(),
                 mLocation.getLongitude())));
            mCameraUpdate.setNoResultsPosition(true);


       //     mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 600,600,25);
//            mCameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),
//                    mLocation.getLongitude()),15);
        } else {
            mCameraUpdate.setmCameraUpdate(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            mCameraUpdate.setNoResultsPosition(false);

        }


        //   CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200,200,20);
        //Util.LOGD(LOG_TAG,"beforeMoveCamera");
//        mTm.log("beforeMoveCamera");
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mView.moveCamera(cu);
//            }
//        }, 1000);


    }

    public void setUpMarkersHashMap() {
        if (mMarkersHashMap == null) {
            mMarkersHashMap = new HashMap();
        }
    }

//    //this is method to help us add a Marker into the hashmap that stores the Markers
//    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
//        setUpMarkersHashMap();
//        mMarkersHashMap.put(marker,customMarker);
//    }


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
            //delay of 50 milliseconds, waiting to mapready
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindView(data);

                }
            }, 200);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
