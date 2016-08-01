package com.chernandezgil.farmacias.ui.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.database.DbContract;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.github.davidmoten.rx.Transformers;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import rx.Observable;

/**
 * Created by Carlos on 10/07/2016.
 */
public class FragmentMain extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>,
        GoogleMap.OnMarkerClickListener{
    private static final String LOG_TAG = FragmentMain.class.getSimpleName();
    private GoogleMap mMap;
    private static final int FARMACIAS_LOADER = 1;
    private HashMap mMarkersHashMap;
    private Location mLocation;
    TimeMeasure mTm = new TimeMeasure(LOG_TAG);
    private boolean mBottomsheetLoaded = false;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SupportMapFragment mMapFragment;
    private boolean mRotation=false;
    @BindView(R.id.adress)
    TextView tvAdress;
    @BindView(R.id.phone)
    TextView tvPhone;
    @BindView(R.id.hours)
    TextView tvHours;
    @BindView(R.id.bottom_sheet)
    View bottomSheet;
    @BindView(R.id.distance)
    TextView tvDistance;
    @BindView(R.id.name)
    TextView tvName;

    public FragmentMain() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        //to avoid: error duplicate http://stackoverflow.com/questions/14083950/duplicate-id-tag-null-or-parent-id-with-another-fragment-for-com-google-androi
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();

        }
        mapFragment.getMapAsync(this);
        mMapFragment=mapFragment;

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(300);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        MapFragment mapFragment= (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapa);
//        mapFragment.getMapAsync(this);
        if(savedInstanceState==null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mLocation = bundle.getParcelable("location_key");

            }
        }else {
            mRotation=true;
            mLocation = savedInstanceState.getParcelable("location_key");
        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Util.LOGD(LOG_TAG, "onStart");
        mTm.log("onStart");

        // getActivity().getSupportLoaderManager().initLoader(FARMACIAS_LOADER,null,this);
        getLoaderManager().initLoader(FARMACIAS_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //this callback executes after onstart
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Util.LOGD(LOG_TAG, "onMapsReady");
        mTm.log("onMapsReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //reposition zoom control to upper right side of the map
        //http://stackoverflow.com/questions/14071230/android-maps-library-v2-zoom-controls-custom-position
        View mZoomControls = mMapFragment.getView().findViewById(0x1);
        if (mZoomControls != null && mZoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mZoomControls.getLayoutParams();

            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);
        }
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
    }

    private void gotoToLatLong(Location location) {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng, 25);
        mMap.moveCamera(cameraUpdate);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FARMACIAS_LOADER) {
            return new CursorLoader(getActivity(),
                    DbContract.FarmaciasEntity.CONTENT_URI,
                    null,
                    DbContract.FarmaciasEntity.LOCALITY + " LIKE '%" + "Villanueva de la Serena" + "%'",
                    null,
                    null
            );
        }
        return null;
    }

    private float calculateDistance(double lat,double lon,Location origen){
        Location destination=new Location("destination");
        destination.setLatitude(lat);
        destination.setLongitude(lon);
        return origen.distanceTo(destination);

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
    private void bindView(Cursor data) {

        List<CustomMarker> farmaciasList=new ArrayList<>();

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
            farmacia.setPhone(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.PHONE)));
            farmacia.setLat(latDest);
            farmacia.setLon(lonDest);
            farmacia.setDistance(distance);
            farmacia.setHours(data.getString(data.getColumnIndex(DbContract.FarmaciasEntity.HOURS)));
            farmacia.setOpen(false);

            farmaciasList.add(farmacia);

        }

        farmaciasList= toFilteredSortedOrderedList(farmaciasList);
        for (int i = 0;i < farmaciasList.size();i++) {
                addMarker(farmaciasList.get(i));
        }

        CustomMarker userLocation=new CustomMarker();
        userLocation.setName("userLocation");
        userLocation.setLat(mLocation.getLatitude());
        userLocation.setLon(mLocation.getLongitude());
        //for hascode and equals
        userLocation.setOrder("A");
        userLocation.setDistance(0d);
        addMarker(userLocation);
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


    public Bitmap getCustomBitmap(String order){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(144, 144, conf);
        //Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_maps_position);
        Canvas canvas = new Canvas(bmp);
        Paint color = new Paint();
        color.setTextSize(60);
        color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        color.setColor(Color.BLACK);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_maps_position), 0,0, color);
        canvas.drawText(order, 50, 67, color);

        return bmp;


           }
    //this is method to help us add a Marker to the map
    //BitmapDescriptorFactory.defaultMarker()
    //itmapDescriptorFactory.fromResource(R.drawable.ic_maps_position)
    private void addMarker(CustomMarker customMarker) {
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(customMarker.getLat(), customMarker.getLon())
        );
        if(customMarker.getName().equals("userLocation")) {
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Tu ubicación");

         //   markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position));
        } else {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(getCustomBitmap(customMarker.getOrder())))
                    .title(customMarker.getName())
                    .snippet(getString(R.string.format_distance,customMarker.getDistance()/1000));
        }


        Marker newMark = mMap.addMarker(markerOption);

        newMark.showInfoWindow();//only last infowindow shows up
        addMarkerToHashMap(customMarker, newMark);
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

        // Change the padding as per needed

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        if(mRotation) {
            mMap.moveCamera(cu);
            mRotation = false;
        } else {
            mMap.animateCamera(cu);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
            CustomMarker customMarker= (CustomMarker) mMarkersHashMap.get(marker);
            tvName.setText(customMarker.getName());
            tvDistance.setText(getString(R.string.format_distance,customMarker.getDistance()));
            tvAdress.setText(customMarker.getAddress());
            tvHours.setText(customMarker.getHours());
            tvPhone.setText(customMarker.getPhone());

            if( mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return false;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTm.log("insideOnloadFinished");

        if (loader.getId() == FARMACIAS_LOADER) {


            //delay of 50 seconds, waiting to mapready
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

    @Override
    public void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


}
