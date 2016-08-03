package com.chernandezgil.farmacias.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.presenter.MapPresenter;
import com.chernandezgil.farmacias.view.MapContract;
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
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by Carlos on 10/07/2016.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,MapContract.View {
    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;


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
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private MapPresenter mMapPresenter;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.LOGD(LOG_TAG, "onCreate");
        mLoaderProvider=new LoaderProvider(getActivity());
        mLoaderManager=getLoaderManager();
        mMapPresenter=new MapPresenter(mLoaderProvider,mLoaderManager);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        setUpBotomSheet();
        setUpFav();
        SupportMapFragment mapFragment=Util.handleMapFragmentRecreation(getChildFragmentManager(),
                R.id.mapFragmentContainer,"mapFragment");
        mapFragment.getMapAsync(this);
        mMapFragment=mapFragment;

        if(savedInstanceState==null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mLocation = bundle.getParcelable("location_key");


            }
        }else {
            mRotation=true;
            mLocation = savedInstanceState.getParcelable("location_key");

        }
        mMapPresenter.setView(this);
        mMapPresenter.setLocation(mLocation);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Util.LOGD(LOG_TAG, "onStart");
        mTm.log("onStart");
        mMapPresenter.onStartLoader();
        // getActivity().getSupportLoaderManager().initLoader(FARMACIAS_LOADER,null,this);

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
    //this is method to help us add a Marker to the map
    //BitmapDescriptorFactory.defaultMarker()
    //itmapDescriptorFactory.fromResource(R.drawable.ic_maps_position)
    @Override
    public void addMarkerToMap(CustomMarker customMarker) {
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(customMarker.getLat(), customMarker.getLon())
        );
        if(customMarker.getName().equals("userLocation")) {
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Tu ubicación");

            //   markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position));
        } else {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(getCustomBitmap(customMarker.getOrder(),
                    customMarker.isOpen())))
                    .title(customMarker.getName())
                    .snippet(getString(R.string.format_distance,customMarker.getDistance()/1000));
        }


        Marker newMark = mMap.addMarker(markerOption);
        newMark.showInfoWindow();//only last infowindow shows up
        mMapPresenter.onAddMarkerToHash(newMark, customMarker);

    }

    @Override
    public void moveCamera(CameraUpdate cameraUpdate) {
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public boolean hideBottomSheet() {
       if(mBottomSheetBehavior.STATE_EXPANDED==mBottomSheetBehavior.getState()) {
           mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
           return true;
       }
       return false;
    }

    @Override
    public void handleDispatchTouchEvent(MotionEvent event) {
        if(mBottomSheetBehavior.getState()==mBottomSheetBehavior.STATE_EXPANDED){
            Rect rect=new Rect();
            bottomSheet.getGlobalVisibleRect(rect);
            if(!rect.contains((int)event.getRawX(),(int)event.getRawY())){
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }


    public Bitmap getCustomBitmap(String order,boolean isOpen){
        ColorMatrix matrix=new ColorMatrix();
        float saturation=0.1f;
        if(isOpen) {
            saturation=10f;
        }
        matrix.setSaturation(saturation);
        ColorFilter paintColorFilter = new ColorMatrixColorFilter(matrix);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(144, 144, conf);
        //Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_maps_position);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setTextSize(60);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.BLACK);
        paint.setColorFilter(paintColorFilter);

        Rect boundsText=new Rect();
        paint.getTextBounds(order,0,order.length(),boundsText);
        int x=(bmp.getWidth()- boundsText.width())/2;
    //    int y=(bmp.getHeight()- boundsText.height())/2;
        Log.d(LOG_TAG,"boundsText.width()"+boundsText.width()+",boundsText.height()"+boundsText.height());
        Log.d(LOG_TAG,"letra:"+order);

        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_maps_position), 0,0, paint);
        canvas.drawText(order, x-4,69, paint);

        return bmp;


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
    public void setUpFav(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mMapPresenter.getLocales()
             //   String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Home Sweet Home", destinationLatitude, destinationLongitude, "Where the party is at");
             //   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
             //   intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
              //  startActivity(intent);
            }
        });
    }
    public void setUpBotomSheet(){
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(300);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }



    private float calculateDistance(double lat,double lon,Location origen){
        Location destination=new Location("destination");
        destination.setLatitude(lat);
        destination.setLongitude(lon);
        return origen.distanceTo(destination);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
            HashMap hashMap=mMapPresenter.onGetHashMap();
            CustomMarker customMarker= (CustomMarker)hashMap.get(marker);
            tvName.setText(customMarker.getName());
            tvDistance.setText(getString(R.string.format_distance,customMarker.getDistance()/1000));
            tvAdress.setText(customMarker.getAddress());
            tvHours.setText(customMarker.getHours());
            tvPhone.setText(customMarker.getPhone());

            if( mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            mMapPresenter.onSetLastMarkerClick(customMarker);
            return false;
    }





    @Override
    public void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


}
