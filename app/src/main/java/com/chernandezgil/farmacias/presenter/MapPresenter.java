package com.chernandezgil.farmacias.presenter;



import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.telephony.PhoneNumberUtils;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.view.MapContract;
import com.github.davidmoten.rx.Transformers;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Carlos on 03/08/2016.
 */
public class MapPresenter implements MapContract.Presenter<MapContract.View>,LoaderManager.LoaderCallbacks<Cursor> {
    private MapContract.View mMapView;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;
    private static final int FARMACIAS_LOADER = 1;
    private HashMap mMarkersHashMap;
    private Location mLocation;
    private CustomMarker mLastClickMarker;
    private CustomMarker mUserUbicationMarker;

    public MapPresenter(@NonNull LoaderProvider loaderProvider,@NonNull LoaderManager loaderManager){
        mLoaderProvider=checkNotNull(loaderProvider,"loader provider cannot be null");
        mLoaderManager=checkNotNull(loaderManager,"loader manager cannot be null");
    }
    @Override
    public void setView(MapContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mMapView =view;
    }

    @Override
    public void detachView() {
        mMapView =null;
    }

    @Override
    public void onStartLoader() {
        mLoaderManager.initLoader(FARMACIAS_LOADER, null, this);
    }

    @Override
    public void onAddMarkerToHash(Marker marker,CustomMarker pharmacy) {
        setUpMarkersHashMap();
        mMarkersHashMap.put(marker,pharmacy);
    }

    @Override
    public HashMap onGetHashMap() {
        return mMarkersHashMap;
    }

    @Override
    public void setLocation(Location location) {
        mLocation=location;
    }

    @Override
    public void onSetLastMarkerClick(CustomMarker customMarker) {
        mLastClickMarker =customMarker;
    }

    @Override
    public LatLng onGetDestinationLocale() {
        return new LatLng(mLastClickMarker.getLat(),mLastClickMarker.getLon());

    }

    @Override
    public String onGetDestinationAddress() {
        return mLastClickMarker.getAddressFormatted();
    }

    @Override
    public String onGetDestinationPhoneNumber() {
        return mLastClickMarker.getPhone();
    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

    public Calendar buildCalendar(Date date,int hour,int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        return calendar;
    }

    public boolean isPharmacyOpen(String hours){
        Date now=new Date();
        Calendar date= Calendar.getInstance();
        date.setTime(now);
        int day=date.get(Calendar.DAY_OF_WEEK);
        if(hours.equals("24H")) {
            return true;
        } else if(Calendar.SATURDAY==day) {
            Calendar morningOpen = buildCalendar(now, 9, 0);
            Calendar morningClose = buildCalendar(now, 14, 0);
            if(date.after(morningOpen) && date.before(morningClose) ) {
                return true;
            }
            return false;

        } else if(Calendar.SUNDAY==day) {
            return false;
        } else {
            Calendar morningOpen = buildCalendar(now, 9, 0);
            Calendar morningClose = buildCalendar(now, 14, 0);
            Calendar eveningOpen = buildCalendar(now, 16, 30);
            Calendar eveningClose = buildCalendar(now, 20, 30);
            if ( (date.after(morningOpen) && date.before(morningClose))
                    || ( date.after(eveningOpen) && date.before(eveningClose)) ) {
                    return true;
            }
            return false;

        }

    }

    private void bindView(Cursor data){

        List<CustomMarker> farmaciasList=new ArrayList<>();
        if(data.isClosed()) return;

        while (data.moveToNext()) {

            CustomMarker farmacia=new CustomMarker();

            double latDest=data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT));
            double lonDest=data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON));
            //float dist=calculateDistance(latDest,lonDest,mLocation);
            double distance=meterDistanceBetweenPoints(latDest,lonDest,mLocation.getLatitude(),mLocation.getLongitude());
            farmacia.setName(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
            farmacia.setAddress(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.ADDRESS)));
            farmacia.setLocality(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.LOCALITY)));
            farmacia.setProvince(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PROVINCE)));
            farmacia.setPostal_code(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.POSTAL_CODE)));
            String phone=data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PHONE));
            farmacia.setPhone(Util.formatPhoneNumber(phone));
            farmacia.setLat(latDest);
            farmacia.setLon(lonDest);
            farmacia.setDistance(distance);
            String hours = data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS));
            farmacia.setHours(hours);
            farmacia.setOpen(isPharmacyOpen(hours));
            String addressFormatted=Util.formatAddress(farmacia.getAddress(),
                                                       farmacia.getPostal_code(),
                                                       farmacia.getLocality(),
                                                       farmacia.getProvince());
            farmacia.setAddressFormatted(addressFormatted);
            farmaciasList.add(farmacia);

        }

        farmaciasList= toFilteredSortedOrderedList(farmaciasList);
        for (int i = 0;i < farmaciasList.size();i++) {
            mMapView.addMarkerToMap(farmaciasList.get(i));

        }

        CustomMarker userLocation=new CustomMarker();
        userLocation.setName("userLocation");
        userLocation.setLat(mLocation.getLatitude());
        userLocation.setLon(mLocation.getLongitude());
        //for hascode and equals
        userLocation.setOrder("A");
        userLocation.setDistance(0d);
        mUserUbicationMarker=userLocation;
   //     mUserUbicationMarker.setAddress(mUserAddress);
        mMapView.addMarkerToMap(userLocation);
        //user location by defaul in bottom sheet
        //tvName.setText();

        zoomAnimateLevelToFitMarkers(120);
    }

    /**
     * Transformers.mapWithIndex https://github.com/ReactiveX/RxJava/issues/3602
     *  rxjava-extras
     * @param list
     * @return
     */
    public List<CustomMarker> toFilteredSortedOrderedList(List<CustomMarker> list){
        String str;

        return Observable.from(list)
                .filter(f->{
                    if(f.getDistance()<4000) {
                        return true;
                    }
                    return false;
                })
                .toSortedList()
                .flatMap(s->Observable.from(s))
                .compose(Transformers.mapWithIndex())
                .map(t->{t.value().setOrder(Util.characterFromInteger((int)t.index()));
                    return t.value();
                }).toList()
                .toBlocking().first();

    }



    public void zoomAnimateLevelToFitMarkers(int padding) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        Iterator<Map.Entry> iter = mMarkersHashMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry mEntry = (Map.Entry) iter.next();
            Marker key = (Marker) mEntry.getKey();
            CustomMarker c= (CustomMarker) mMarkersHashMap.get(key);
            LatLng ll = new LatLng(c.getLat(), c.getLon());
            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMapView.moveCamera(cu);

    }

    public void setUpMarkersHashMap() {
        if (mMarkersHashMap == null) {
            mMarkersHashMap = new HashMap();
        }
    }

    //this is method to help us add a Marker into the hashmap that stores the Markers
    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        mMarkersHashMap.put(marker,customMarker);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==FARMACIAS_LOADER) {
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
