package com.chernandezgil.farmacias;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeActivity;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.chernandezgil.farmacias.Utils.Util;
import com.chernandezgil.farmacias.database.DbContract;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.services.DownloadFarmacias;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AllowMeActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener, OnMapReadyCallback,LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.coordinates)
    TextView mTvCoordinates;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<Place> mPlacesList = new ArrayList<>();
    private GoogleMap mMap;
    private static final int FARMACIAS_LOADER=1;
    private HashMap mMarkersHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ButterKnife.bind(this);



        Intent intent=new Intent(this, DownloadFarmacias.class);
        startService(intent);

        MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa));
        map.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        getSupportLoaderManager().initLoader(FARMACIAS_LOADER,null,this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Util.LOGD(LOG_TAG, "onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100000);

        if (!AllowMe.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AllowMe.Builder()
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setRationale("This app needs this permission to work")
                    .setCallback(new AllowMeCallback() {
                        @Override
                        public void onPermissionResult(int i, PermissionResultSet permissionResultSet) {
                            if (permissionResultSet.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
                            }
                        }
                    }).request(1);

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Util.LOGD(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Util.LOGD(LOG_TAG, "onLocationChanged");
        mTvCoordinates.setText(location.toString());
      //  gotoToLatLong(location);

    }
    private void gotoToLatLong(Location location) {
        LatLng newLatLng=new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(newLatLng,25);
        mMap.moveCamera(cameraUpdate);


    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap=map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
       // map.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void drawMarkerOnMap(Cursor data) {
        while(data.moveToNext()) {
            CustomMarker customMarker=new CustomMarker();
            customMarker.setCustomMarkerId(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
            customMarker.setCustomMarkerLatitude(data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LAT)));
            customMarker.setCustomMarkerLongitude(data.getDouble(data.getColumnIndex(DbContract.FarmaciasEntity.LON)));
            addMarker(customMarker);

        }
        zoomAnimateLevelToFitMarkers(120);
    }

    public void setUpMarkersHashMap() {
        if (mMarkersHashMap == null) {
            mMarkersHashMap = new HashMap();
        }
    }
    //this is method to help us add a Marker into the hashmap that stores the Markers
    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        mMarkersHashMap.put(customMarker, marker);
    }
    //this is method to help us add a Marker to the map
    private void addMarker(CustomMarker customMarker) {
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude())).icon(
                BitmapDescriptorFactory.defaultMarker());

        Marker newMark = mMap.addMarker(markerOption);
        addMarkerToHashMap(customMarker, newMark);
    }

    public void zoomAnimateLevelToFitMarkers(int padding) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        Iterator<Map.Entry> iter = mMarkersHashMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry mEntry = (Map.Entry) iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            LatLng ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());
            b.include(ll);
        }
        LatLngBounds bounds = b.build();

        // Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==FARMACIAS_LOADER){
            return new CursorLoader(this,
                    DbContract.FarmaciasEntity.CONTENT_URI,
                    null,
                    DbContract.FarmaciasEntity.LOCALITY + " LIKE '%"+ "Villanueva de la Serena" +"%'",
                    null,
                    null
                    );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==FARMACIAS_LOADER) {
            drawMarkerOnMap(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
