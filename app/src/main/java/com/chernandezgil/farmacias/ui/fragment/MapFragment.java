package com.chernandezgil.farmacias.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;
import com.bettervectordrawable.utils.BitmapUtil;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.presenter.MapPresenter;
import com.chernandezgil.farmacias.view.MapContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindBitmap;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 10/07/2016.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,MapContract.View {


    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Location mLocation;
    TimeMeasure mTm;
    private boolean mBottomsheetLoaded = false;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SupportMapFragment mMapFragment;
    private boolean mRotation=false;
    private MapPresenter mMapPresenter;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;
    private String mAddress;
    @BindView(R.id.ivOrder)
    ImageView ivOrder;

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



    @BindView(R.id.llUpper)
    LinearLayout llUper;
    @BindView(R.id.ivCall)
    ImageView ivCall;
    @BindView(R.id.txtCall)
    TextView tvCall;
    @BindView(R.id.ivGo)
    ImageView ivGo;
    @BindView(R.id.txtGo)
    TextView tvGo;
    @BindView(R.id.txtShare)
    TextView tvShare;

    @BindView(R.id.ivDistance)
    ImageView ivDistance;
    @BindView(R.id.ivMarker)
    ImageView ivMarker;
    @BindView(R.id.ivPhone)
    ImageView ivPhone;
    @BindView(R.id.ivClock)
    ImageView ivClock;
    @BindView(R.id.ivShare)
    ImageView ivShare;
  //  FloatingActionButton fab;
    @BindColor(R.color.pharmacy_close)
    int color_pharmacy_close;
    @BindColor(R.color.pharmacy_open)
    int color_pharmacy_open;
   // @BindBitmap(R.drawable.ic_maps_position)
   //Bitmap markerBitmap;


    private Geocoder mGeocoder;
    private CustomMarker mLastMarkerClicked;
    private Bitmap  markerBitmap;
    private boolean mIsVisible;
    public MapFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTm= new TimeMeasure(LOG_TAG);
        Util.LOGD(LOG_TAG, "onCreate:"+this.toString());
      //  mTm.log("onCreate:"+this.toString());
        Bundle bundle=getArguments();
        if(bundle!=null) {
            mLocation=bundle.getParcelable("location_key");
        }
        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
        mLoaderProvider=new LoaderProvider(getActivity());
        mLoaderManager=getLoaderManager();
        mMapPresenter=new MapPresenter(mLoaderProvider,mLoaderManager,mGeocoder,mTm);
        mMapPresenter.setLocation(mLocation);
        mAddress = mMapPresenter.onGetAddressFromLocation(mLocation);
        mMapPresenter.onStartLoader();



    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView:"+this.toString());
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this,view);
        setUpBotomSheet();
        setUpTvPhone();
        setUpIvCall();
        setUpIvGo();
        setUpIvShare();



        SupportMapFragment    mapFragment = Util.handleMapFragmentRecreation(getChildFragmentManager(),
                R.id.mapFragmentContainer, "mapFragment");
        mapFragment.getMapAsync(this);

        mMapFragment=mapFragment;

        if(savedInstanceState!=null){
            mRotation=true;
            mLocation = savedInstanceState.getParcelable("location_key");
            mLastMarkerClicked=savedInstanceState.getParcelable("lastMarkerClicked_key");
            mMapPresenter.onSetLastMarkerClick(mLastMarkerClicked);
        }

        mMapPresenter.setView(this);
       // markerBitmap=getBitmap(R.drawable.pharmacy1);
        markerBitmap=getBM();
        mMapPresenter.onSetMarkerBitMap(markerBitmap);
        return view;
    }

    private Bitmap getBM(){

        ;
        Drawable drawable =VectorDrawableCompat.create(getResources(),R.drawable.pharmacy,null);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Bitmap bitmap = BitmapUtil.toBitmap(drawable, metrics, 48f, 0);
        return bitmap;
    }
    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private  Bitmap getBitmap(int drawableResId) {
      //  VectorDrawableCompat vectorDrawable=VectorDrawableCompat.create(getResources(),drawableResId,null);
      //  Drawable vectorDrawable = AppCompatDrawableManager.get().getDrawable(getActivity(), drawableResId);
        Drawable vectorDrawable= ContextCompat.getDrawable(getActivity(),drawableResId);
       // vectorDrawable.setColorFilter(color_pharmacy_open, PorterDuff.Mode.SRC_ATOP);
        vectorDrawable.setTint(color_pharmacy_open);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
        outState.putParcelable("lastMarkerClicked_key",mLastMarkerClicked);
    }
    //this callback executes after onstart
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
   //     Util.LOGD(LOG_TAG, "onMapsReady");
        mTm.log("onMapsReady:"+this.toString());
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
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(LOG_TAG,"onmapLoaded");
                CameraUpdate cu=mMapPresenter.getCameraUpdate();
                moveCamera(cu);
            }
        });

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
            Bitmap bitmap=mMapPresenter.onRequestCustomBitmap(customMarker.getOrder(),customMarker.isOpen());
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title(customMarker.getName())
                    .snippet(getString(R.string.format_distance,customMarker.getDistance()/1000));
        }
        markerOption.anchor(0.5f, 0.5f);

        Marker newMark = mMap.addMarker(markerOption);
        newMark.showInfoWindow();//only last infowindow shows up
        mMapPresenter.onAddMarkerToHash(newMark, customMarker);

    }

    public void setUpTvPhone(){

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhoneCall();
            }
        });
    }
    @Override
    public void moveCamera(CameraUpdate cameraUpdate) {


        mMap.animateCamera(cameraUpdate);
    }

    private void handlePhoneCall(){
        String uri="tel:" + mMapPresenter.onGetDestinationPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
    @Override
    public boolean collapseBottomSheet() {
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


    @Override
    public void displayPharmacyInBottomSheet(CustomMarker firstSortedPharmacy,CustomMarker lastClicked) {
        if(mRotation) {
            addMarkerToMap(lastClicked);
            firstSortedPharmacy=mLastMarkerClicked;
            mRotation=false;
        }


        showPharmacyInBottomSheet(firstSortedPharmacy);
        //http://stackoverflow.com/questions/37822264/android-bottom-sheet-behavior-not-working-properly-views-not-show-on-first-run
        mBottomSheetBehavior.setPeekHeight(llUper.getHeight());
        //a trick to show expanded, else doesn't show
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);



    }

    private void setUpIvGo(){
        ivGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng destinationLatLng=mMapPresenter.onGetDestinationLocale();
                String destinationAddress=mMapPresenter.onGetDestinationAddress();
                //mMapPresenter.getLocales()
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                        mLocation.getLatitude(),mLocation.getLongitude() , mAddress,
                        destinationLatLng.latitude, destinationLatLng.longitude,destinationAddress);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }

    private void setUpIvCall(){
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhoneCall();
            }
        });

    }

    private void setUpIvShare(){
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //http://stackoverflow.com/questions/26149422/android-sharing-formatted-data-using-intent
                String nombre="Farmacia:";
                String distancia="distancia:";
                String direccion="direccion:";
                String telefono="tef:";

                String textToShare=nombre +  mLastMarkerClicked.getName() + Constants.CR
                        + distancia + getString(R.string.format_distance,mLastMarkerClicked.getDistance()/1000) + Constants.CR
                        + direccion + mLastMarkerClicked.getAddressFormatted() + Constants.CR
                        + telefono  + mLastMarkerClicked.getPhone();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    private void setUpBotomSheet(){
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //initially set hidden(in case there are no pharmacies around).Not working
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_COLLAPSED) {

                } else {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
              //  Util.LOGD(LOG_TAG,""+slideOffset);
              //  fab.setAlpha(slideOffset);

            }
        });
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
            HashMap hashMap=mMapPresenter.onGetHashMap();
            CustomMarker customMarker= (CustomMarker)hashMap.get(marker);
            showPharmacyInBottomSheet(customMarker);
            if( mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return false;
    }

    private void showPharmacyInBottomSheet(CustomMarker marker){

            int color = marker.isOpen() ? color_pharmacy_open : color_pharmacy_close;
            llUper.setBackgroundColor(color);


            setTintedVectorDrawable(ivCall, R.drawable.phone, color);
            setTintedVectorDrawable(ivGo, R.drawable.directions, color);
            setTintedVectorDrawable(ivDistance, R.drawable.distance, color);
            setTintedVectorDrawable(ivMarker, R.drawable.map_marker, color);
            setTintedVectorDrawable(ivPhone, R.drawable.phone, color);
            setTintedVectorDrawable(ivClock, R.drawable.clock, color);
            setTintedVectorDrawable(ivShare,R.drawable.share,color);

            tvName.setText(marker.getName());
            ivOrder.setImageBitmap(marker.getMarkerImage());

            tvCall.setTextColor(color);
            tvGo.setTextColor(color);
            tvShare.setTextColor(color);


            tvDistance.setText(getString(R.string.format_distance, marker.getDistance() / 1000));
            tvAdress.setText(marker.getAddressFormatted());
            tvHours.setText(marker.getHours());
            tvPhone.setText(marker.getPhone());
            mMapPresenter.onSetLastMarkerClick(marker);
            mLastMarkerClicked=marker;


    }
//    public void setTintedDrawable(ImageView imageView,@DrawableRes int drawableResId,int color ){
//
//        SVG svg=new SVGBuilder().readFromResource(getResources(),R.raw.distance4)
//                .build();
//
//        Drawable drawable=svg.getDrawable();
//
//     //   Drawable drawable= ContextCompat.getDrawable(getActivity(),drawableResId);
//        drawable.setColorFilter(color, PorterDuff.Mode.DST_ATOP);
//        imageView.setImageDrawable(drawable);
//
//    }
    public void setTintedVectorDrawable(ImageView imageView, @DrawableRes int drawableResId, int color) {

        VectorDrawableCompat drawable=VectorDrawableCompat.create(getResources(),drawableResId,null);
        if(drawable==null) return;
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);


    }


    @Override
    public void onStart() {
        Util.LOGD(LOG_TAG, "onStart:"+this.toString());
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.LOGD(LOG_TAG, "onResume:"+this.toString());

    }

    @Override
    public void onStop() {
        Util.LOGD(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


}
