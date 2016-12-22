package com.chernandezgil.farmacias.presenter;


import android.content.Intent;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.PharmacyObjectMap;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.fragment.MapTabFragment;
import com.chernandezgil.farmacias.view.MapTabContract;
import com.github.davidmoten.rx.Transformers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.maps.android.SphericalUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Carlos on 03/08/2016.
 */
public class MapTabPresenter implements MapTabContract.Presenter<MapTabContract.View>, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MapTabPresenter.class.getSimpleName();
    private MapTabContract.View mView;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;
    private Geocoder mGeocoder;
    private static final int FARMACIAS_LOADER = 1;
    private HashMap mMarkersHashMap;
    private Location mLocation;
    private String mAddress;
    private PharmacyObjectMap mLastMarkerClicked;
    private PharmacyObjectMap mUserUbicationMarker;
    private List<PharmacyObjectMap> mFarmaciasList;
    private PharmacyObjectMap mFirstSortedPharmacy = null;
    private Bitmap markerBitmap;
    private CustomCameraUpdate mCameraUpdate;
    private PreferencesManager preferencesManager;
    private int mRadio;
    private boolean mUserPressedFavorite;
    private String mPhoneUpdatedFavorite;
    private Handler mainHandler;

    @Inject
    public MapTabPresenter(@NonNull LoaderProvider loaderProvider,
                           @NonNull LoaderManager loaderManager,
                           @NonNull Geocoder geocoder, PreferencesManager preferencesManager) {
        mLoaderProvider = checkNotNull(loaderProvider, "loader provider cannot be null");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mGeocoder = checkNotNull(geocoder, "geocoder cannot be null");
        this.preferencesManager = preferencesManager;
        mRadio = this.preferencesManager.retrieveRadioBusquedaFromSp() * 1000;
        //    mCameraUpdate = new CustomCameraUpdate();
        mainHandler = new Handler(Looper.getMainLooper());


    }

    @Override
    public void setView(MapTabContract.View view) {
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
    public void updateFavoriteFlag(String phone) {
        mUserPressedFavorite = true;
        mPhoneUpdatedFavorite = phone;
    }


    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onStartLoader() {
        Utils.logD(LOG_TAG, "onStartLoader");
        mLoaderManager.initLoader(FARMACIAS_LOADER, null, this);

    }


    @Override
    public void handleClickGo() {
        LatLng destinationLatLng = new LatLng(mLastMarkerClicked.getLat(), mLastMarkerClicked.getLon());
        String destinationAddress = mLastMarkerClicked.getAddressFormatted();

        Intent intent = Utils.getGoodleDirectionsIntent(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())
                , mAddress,
                destinationLatLng
                , destinationAddress);
        mView.launchActivity(intent);
    }

    @Override
    public void handleClickCall() {
        final String uri = "tel:" + mLastMarkerClicked.getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        mView.launchActivity(intent);

    }

    @Override
    public void handleClickShare() {
        //http://stackoverflow.com/questions/26149422/android-sharing-formatted-data-using-intent
        final String name = mLastMarkerClicked.getName();
        double distance = mLastMarkerClicked.getDistance();
        final String address = mLastMarkerClicked.getAddressFormatted();
        final String phone = mLastMarkerClicked.getPhone();
        final Intent intent = Utils.getShareIntent(name, distance, address, phone);
        mView.launchActivity(intent);
    }

    @Override
    public void handleClickFavorite() {

        String phone = mLastMarkerClicked.getPhone();
        updateFavoriteFlag(phone);
        phone = phone.replaceAll(" ", "");
        //putting this line here is to assume that it will always change the favorite value in db
        //else would be better perhaps to put after cha
        removeMarkerInHashMapAndMapFromMapFragment(mLastMarkerClicked);

        final String snackMessage = Utils.changeFavoriteInDb(
                mLastMarkerClicked.isFavorite(), phone);
        //if no updated row
        if (snackMessage == null) {
            return;
        }
        // por el momento no lo pongo
        //   mView.showSnackBar(snackMessage);


    }


    @Override
    public void handleClickOpeningHours() {
        final String hours = mLastMarkerClicked.getHours();
        final int layoutId = Utils.is24HoursPharmacy(hours)? R.layout.dialog_opening_hours_24_hours
                :R.layout.dialog_opening_hours_normal;
        final int titleBackgroundColor=Utils.getStatusPharmacyColor(mLastMarkerClicked.isOpen());
        mView.showOpeningHours(layoutId,titleBackgroundColor);

    }


    @Override
    public void handleOnMarkerClick(Marker marker) {

        PharmacyObjectMap pharmacy = (PharmacyObjectMap) mMarkersHashMap.get(marker);
        if (!pharmacy.getName().equals(MapTabFragment.USER_LOCATION)) {
            if (!mView.isBottomSheetExpanded()) {
                mView.setStateBottomSheet(3);
            }
            mView.showPharmacyInBottomSheet(pharmacy);
        }

    }

    @Override
    public void onAddMarkerToHash(Marker marker, PharmacyObjectMap pharmacy) {
        setUpMarkersHashMap();
        mMarkersHashMap.put(marker, pharmacy);
    }

    @Override
    public void removeMarkerInHashMapAndMapFromMapFragment(PharmacyObjectMap pharmacy) {
        //for testing logHashMap();

        //remove the marker from map
        removeMarkerFromMap(pharmacy);
        //remove marker from HashMap
        removeMarkerFromHashMap(pharmacy);
        //for testing logHashMap();

    }

    private void removeMarkerFromHashMap(PharmacyObjectMap pharmacy) {
        mMarkersHashMap.values().remove(pharmacy);

    }

    private void removeMarkerFromMap(PharmacyObjectMap pharmacy) {

        Marker marker = getKeyFromValue(pharmacy);
        if (marker != null) {
            marker.remove();
        }
    }

    @Override
    public void removeMarkerInHashMapAndMapFromList(String phone) {
        PharmacyObjectMap pharmacy = new PharmacyObjectMap();
        pharmacy.setPhone(phone);
        removeMarkerInHashMapAndMapFromMapFragment(pharmacy);
    }


    /**
     * get a key(a Marker) knowing the value(PharmacyObjectMap)
     *
     * @param pharmacy
     * @return
     */
    private Marker getKeyFromValue(PharmacyObjectMap pharmacy) {
        Iterator<Map.Entry> iter = mMarkersHashMap.entrySet().iterator();

        while (iter.hasNext()) {

            Map.Entry mEntry = (Map.Entry) iter.next();
            Marker key = (Marker) mEntry.getKey();
            PharmacyObjectMap c = (PharmacyObjectMap) mMarkersHashMap.get(key);
            //objects are equal if they have the same phone number
            if (c.equals(pharmacy)) {
                Utils.logD(LOG_TAG, "key:" + key + ",object:" + c.toString());
                //return what we want, the key of the HashMap
                return key;
            }
        }

        return null;
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
    public void onSetLastMarkerClick(PharmacyObjectMap pharmacyObjectMap) {
        mLastMarkerClicked = pharmacyObjectMap;
    }

    @Override
    public String onGetAddressFromLocation(Location currentLocation) {


        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);
        } catch (IOException ioe) {

        }

        if (addresses == null || addresses.size() == 0) {
            Utils.logD(LOG_TAG, "no address found");
            return null;
        } else {
            Address address = addresses.get(0);
            StringBuilder stringBuilder = new StringBuilder();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                if (i == 0) {
                    preferencesManager.saveStreet(address.getAddressLine(i));
                }
                stringBuilder.append(address.getAddressLine(i));
                if (i != address.getMaxAddressLineIndex() - 1) {
                    stringBuilder.append(Constants.COMMA);
                }
            }
            Utils.logD(LOG_TAG, "address found");
            mAddress = stringBuilder.toString();
            return mAddress;
        }


    }

    @Override
    public void onSetAddress(String address) {
        mAddress = address;
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


        Bitmap bmp = Bitmap.createBitmap(markerBitmap.getWidth(),
                markerBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(Utils.getDefaultBitmapTextSize());
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.WHITE);
        paint.setColorFilter(paintColorFilter);

        canvas.drawBitmap(markerBitmap, 0, 0, paint);

        //make  drawing text independent from pixels
        float factor = 0.80f;
        switch (order.length()) {
            case 1:
                break;
            case 2:
                //70% of original size and reduce the margin with the bottom
                paint.setTextSize(Utils.getDefaultBitmapTextSize() * 0.7f);
                factor = 0.75f;
                break;
            case 3:
                //50% of original size and reduce the margin with the bottom
                paint.setTextSize(Utils.getDefaultBitmapTextSize() * 0.5f);
                factor = 0.60f;
                break;
            default:
                factor = 0.60f;
        }
        Rect boundsText = new Rect();
        paint.getTextBounds(order, 0, order.length(), boundsText);
        int x = (bmp.getWidth() - boundsText.width()) / 2;


        canvas.drawText(order, x, factor * markerBitmap.getHeight(), paint);

        return bmp;
    }


    private void bindView(Cursor data) {

        Utils.logD(LOG_TAG, "onBindView");
        mFarmaciasList = new ArrayList<>();
        mFirstSortedPharmacy = null;
        if (data.isClosed()) return;
        if (data.moveToFirst()) {
            do {

                PharmacyObjectMap farmacia = new PharmacyObjectMap();


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
                //float dist=calculateDistance(latDest,lonDest,mLocation);
                //Utils.meterDistanceBetweenPoints(latDest, lonDest, mLocation.getLatitude(), mLocation.getLongitude());
                double distance = SphericalUtil.computeDistanceBetween(new LatLng(latDest, lonDest),
                        new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));


                farmacia.setLat(latDest);
                farmacia.setLon(lonDest);
                farmacia.setDistance(distance);
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

                mFarmaciasList.add(farmacia);

            } while (data.moveToNext());
        }
        mFarmaciasList = toFilteredSortedOrderedList(mFarmaciasList);
        PharmacyObjectMap updatedPharmacy;
        if (mUserPressedFavorite) {
            PharmacyObjectMap objComp = new PharmacyObjectMap();
            objComp.setPhone(mPhoneUpdatedFavorite);
            //could have used this sentence  mFarmaciasList.indexOf() instead of guava
            if (mFarmaciasList.contains(objComp)) {
                // mFarmaciasList.get
                updatedPharmacy = Iterables.find(mFarmaciasList, new Predicate<PharmacyObjectMap>() {
                    @Override
                    public boolean apply(@Nullable PharmacyObjectMap input) {

                        return input.getPhone().equals(objComp.getPhone());

                    }
                });

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //add it again(now with the correct favorite  value
                        // because the previous marker with that pharmacy has been deleted from the map(and from HashMap)
                        mView.addMarkerToMap(updatedPharmacy);
                        mView.refreshMapIfNecesary(updatedPharmacy);
                    } // This is your code
                });


                mUserPressedFavorite = false;

                return;

            }
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                int size = mFarmaciasList.size();
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
                        mFirstSortedPharmacy = mFarmaciasList.get(i);
                    }
                    mView.addMarkerToMap(mFarmaciasList.get(i));
                    // we are in the last element, and have added it to the map
                    // now need to add to maker as last one(so as it appears with info window over the rest)
                    if (i == mFarmaciasList.size() - 1) {
                        mUserUbicationMarker = getUserLocationMarker();
                        if (mFirstSortedPharmacy != null) {
                            mView.addMarkerToMap(mUserUbicationMarker);
                            mView.preShowPharmacyInBottomSheet(mFirstSortedPharmacy, mLastMarkerClicked);
                        }
                        zoomAnimateLevelToFitMarkers(120);

                    }

                }
                if (size == 0) {
                    mUserUbicationMarker = getUserLocationMarker();
                    mView.addMarkerToMap(mUserUbicationMarker);
                    zoomAnimateLevelToFitMarkers(120);
                }

            }
        });


    }

    private void logHashMap() {
        Iterator<Map.Entry> iter = mMarkersHashMap.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            count++;
            Map.Entry mEntry = (Map.Entry) iter.next();
            Marker key = (Marker) mEntry.getKey();
            PharmacyObjectMap c = (PharmacyObjectMap) mMarkersHashMap.get(key);
            Utils.logD(LOG_TAG, "key:" + key + ",object:" + c.toString());
//            if(c.equals(pharmacy)) {
//                return key;
//            }
        }
        Utils.logD(LOG_TAG, "count" + count);
    }

    private PharmacyObjectMap getUserLocationMarker() {
        PharmacyObjectMap userLocation = new PharmacyObjectMap();
        userLocation.setName("userLocation");
        userLocation.setLat(mLocation.getLatitude());
        userLocation.setLon(mLocation.getLongitude());
        userLocation.setAddressFormatted(mAddress);
        //for hascode and equals
        userLocation.setOrder("A");
        userLocation.setDistance(0d);
        userLocation.setPhone("0");
        return userLocation;
    }

    /**
     * Transformers.mapWithIndex https://github.com/ReactiveX/RxJava/issues/3602
     * rxjava-extras
     *
     * @param list
     * @return
     */
    public List<PharmacyObjectMap> toFilteredSortedOrderedList(List<PharmacyObjectMap> list) {
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
                    t.value().setOrder(Utils.characterFromInteger((int) t.index()));
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
        int markerCounter = 0;
        while (iter.hasNext()) {
            markerCounter++;
            Map.Entry mEntry = (Map.Entry) iter.next();
            Marker key = (Marker) mEntry.getKey();

            PharmacyObjectMap c = (PharmacyObjectMap) mMarkersHashMap.get(key);
            LatLng ll = new LatLng(c.getLat(), c.getLon());
            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        mCameraUpdate = new CustomCameraUpdate();
        if (markerCounter == 1) {
//            mCameraUpdate.setmCameraUpdate(CameraUpdateFactory.newLatLng(new LatLng(mLocation.getLatitude(),
//                    mLocation.getLongitude())));
            mCameraUpdate.setmCameraUpdate(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            mCameraUpdate.setNoResultsPosition(true);


            //     mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 600,600,25);
//            mCameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),
//                    mLocation.getLongitude()),15);
        } else {
            mCameraUpdate.setmCameraUpdate(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            mCameraUpdate.setNoResultsPosition(false);

        }


    }

    public void setUpMarkersHashMap() {
        if (mMarkersHashMap == null) {
            mMarkersHashMap = new HashMap();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FARMACIAS_LOADER) {
            return mLoaderProvider.getPharmacies();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Utils.logD(LOG_TAG, "onLoadFinished");
        if (loader.getId() == FARMACIAS_LOADER) {
            //delay of 50 milliseconds, waiting to mapready <-not sure if this still apply
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread() {
                        @Override
                        public void run() {
                            bindView(data);
                        }
                    }.start();

                }
            }, 150);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
