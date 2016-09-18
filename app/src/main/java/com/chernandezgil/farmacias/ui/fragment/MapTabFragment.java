package com.chernandezgil.farmacias.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.customwidget.SnackBarWrapper;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.customwidget.CustomSupporMapFragment;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.PharmacyObjectMap;
import com.chernandezgil.farmacias.presenter.MapTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.MapTabContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.lang.reflect.Field;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Carlos on 10/07/2016.
 */
public class MapTabFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener,MapTabContract.View {


    private static final String LOG_TAG = MapTabFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Location mLocation;
    TimeMeasure mTm;



    private BottomSheetBehavior mBottomSheetBehavior;
    private CustomSupporMapFragment mMapFragment;
    private boolean mRotation=false;
    private MapTabPresenter mPresenter;
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

    @BindColor(R.color.pharmacy_close)
    int color_pharmacy_close;
    @BindColor(R.color.pharmacy_open)
    int color_pharmacy_open;
    @BindView(R.id.coordinator)
    CoordinatorLayout mRootView;
    @BindView(R.id.ivFavoriteMap)
    ImageView ivFavorite;


    private Geocoder mGeocoder;
    private PharmacyObjectMap mLastMarkerClicked;
    private Bitmap  markerBitmap;
    private Unbinder unbinder;
    private static final int STATE_COLLAPSED=0;
    private static final int STATE_EXPANDED=1;
    private int mBottomSheetState;
    private boolean mFromListTab;
    private CustomSupporMapFragment mapFragment;
    private PreferencesManager mSharedPreferences;
    private SnackBarWrapper mSnackBar;
    public static final String USER_LOCATION = "userLocation";


    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Util.logD(LOG_TAG,"onReceive");
            //update location variable
            mLocation = mSharedPreferences.getLocation();
            //get the new address
            mPresenter.onGetAddressFromLocation(mLocation);
            //set location to presenter
            mPresenter.setLocation(mLocation);
            mMap.clear();
            mPresenter.onStartLoader();
            //mLastLocation lo va a poner de nuevo, deberia hacer lo mismo que cuando pulsa favoritos,
            //bueno no exactamente lo mismo, por que si las coordenadas han variado mucho, el mClicklastlocation
            //pudiera ahora no tener sentido
        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        Projection projection = mMap.getProjection();
        Point point=projection.toScreenLocation(latLng);

        Rect rect=new Rect();
        bottomSheet.getGlobalVisibleRect(rect);
        if(!rect.contains(point.x,point.y)) {
            Util.logD(LOG_TAG,"outside bottom sheet");
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public MapTabFragment() {
    }

    

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTm= new TimeMeasure(LOG_TAG);
        Util.logD(LOG_TAG, "onCreate");
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        mLocation = mSharedPreferences.getLocation();
        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
        mLoaderProvider=new LoaderProvider(getActivity());
        mLoaderManager=getLoaderManager();
        mPresenter =new MapTabPresenter(mLoaderProvider,mLoaderManager,mGeocoder,mSharedPreferences);
        mPresenter.setView(this);
        mPresenter.setLocation(mLocation);
        mAddress = mPresenter.onGetAddressFromLocation(mLocation);




    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.logD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_tab_map, container, false);
        unbinder=ButterKnife.bind(this,view);
        setUpBotomSheet();
        setUpTvPhone();
        setUpIvCall();
        setUpIvGo();
        setUpIvShare();
        setUpIvFavorite();


        mapFragment = Util.handleMapFragmentRecreation(getChildFragmentManager(),
                R.id.mapFragmentContainer, "mapFragment");
        mapFragment.getMapAsync(this);



        mMapFragment=mapFragment;

        if(savedInstanceState!=null){
            mRotation=true;
            mLastMarkerClicked=savedInstanceState.getParcelable("lastMarkerClicked_key");
            mPresenter.onSetLastMarkerClick(mLastMarkerClicked);
            mBottomSheetState=savedInstanceState.getInt("bottom_sheet_state");
        }


        markerBitmap=Util.getBitmapFromVectorDrawable(getActivity().getApplicationContext(),R.drawable.hospital_pin_stroke);
        mPresenter.onSetMarkerBitMap(markerBitmap);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Util.logD(LOG_TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        //start the loader once the view is ready
        mPresenter.onStartLoader();
        //  setUserVisibleHint(true); setting it here doen't work
        // I opted for setting this value in the instantiation of
        //  the fragment in FragmentPagerAdapter. solution not valid after 24.0.0
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Util.logD(LOG_TAG,"setUserVisibleHint:"+isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

//    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
//        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            drawable = (DrawableCompat.wrap(drawable)).mutate();
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
//
//        return bitmap;
//    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private  Bitmap getBitmap(int drawableResId) {
//      //  VectorDrawableCompat vectorDrawable=VectorDrawableCompat.create(getResources(),drawableResId,null);
//      //  Drawable vectorDrawable = AppCompatDrawableManager.get().getDrawable(getActivity(), drawableResId);
//        Drawable vectorDrawable= ContextCompat.getDrawable(getActivity(),drawableResId);
//       // vectorDrawable.setColorFilter(color_pharmacy_open, PorterDuff.Mode.SRC_ATOP);
//        vectorDrawable.setTint(color_pharmacy_open);
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
//                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        vectorDrawable.draw(canvas);
//        return bitmap;
//    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
        outState.putParcelable("lastMarkerClicked_key",mLastMarkerClicked);
        outState.putInt("bottom_sheet_state",mBottomSheetBehavior.getState());
    }
    @Override
    public void onStart() {
        Util.logD(LOG_TAG, "onStart");
        super.onStart();


    }

    @Override
    public void onResume() {
        Util.logD(LOG_TAG,"onResume");
        super.onResume();
        IntentFilter filter = new IntentFilter(ListTabFragment.NEW_LOCATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationReceiver,filter);
    }

    @Override
    public void onPause() {
        Util.logD(LOG_TAG,"onPause");
         super.onPause();

    }

    @Override
    public void onStop() {
        Util.logD(LOG_TAG, "onStop");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);
        super.onStop();
    }
    //this callback executes after onstart
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Util.logD(LOG_TAG, "onMapsReady");
        //mTm.log("onMapsReady:"+this.toString());
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //to disable the two little images in the right-bottom side of the map
        mMap.getUiSettings().setMapToolbarEnabled(false);
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
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int count=0;
                        CustomCameraUpdate cu=null;
                        while(cu==null) {
                            count++;
                            cu= mPresenter.getCameraUpdate();
                            Util.logD(LOG_TAG,"count"+count);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        moveCamera(cu);
                    }
                });

            }
        });

        mMap.setOnMapClickListener(this);

    }


    //this is method to help us add a Marker to the map
    //BitmapDescriptorFactory.defaultMarker()
    //itmapDescriptorFactory.fromResource(R.drawable.ic_maps_position)
    @Override
    public void addMarkerToMap(PharmacyObjectMap pharmacyObjectMap) {
        //if(!isAdded()) return;
        //me da un npe porque el pharmacyObjetMap es nulo: a ver si con esta linea se arregla
        if(pharmacyObjectMap==null) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(pharmacyObjectMap.getLat(), pharmacyObjectMap.getLon())
        );
        if(pharmacyObjectMap.getName().equals(USER_LOCATION)) {
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Tu ubicación")
                    .snippet(Util.getStreetFromAddress(pharmacyObjectMap.getAddressFormatted()));

            //   markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position));
        } else {
            Bitmap bitmap= mPresenter.onRequestCustomBitmap(pharmacyObjectMap.getOrder(), pharmacyObjectMap.isOpen());
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title(pharmacyObjectMap.getName())
                    .snippet(getString(R.string.format_distance, pharmacyObjectMap.getDistance()/1000));
        }
        markerOption.anchor(0.5f, 0.5f);

        Marker newMark = mMap.addMarker(markerOption);
        newMark.showInfoWindow();//only last infowindow shows up
        mPresenter.onAddMarkerToHash(newMark, pharmacyObjectMap);

    }



    @Override
    public void moveCamera(CustomCameraUpdate cameraUpdate) {
        if(cameraUpdate.isNoResultsPosition()) {
            handleNoResults((cameraUpdate));
        } else {
            mMap.moveCamera(cameraUpdate.getmCameraUpdate());
        }


    }

    private void handleNoResults(CustomCameraUpdate cameraUpdate){
        mMap.moveCamera(cameraUpdate.getmCameraUpdate());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15),2000, null);
        String message="Sin resultados. Radio de busqueda insuficiente";
        Snackbar.make(mRootView,message,Snackbar.LENGTH_INDEFINITE).show();
    }



    @Override
    public boolean collapseBottomSheet() {
       if(isBottomSheetExpanded()) {
           mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
           return true;
       }
       return false;
    }

//    @Override
//    public void handleDispatchTouchEvent(MotionEvent event) {
//        if(isBottomSheetExpanded()){
//            Rect rect=new Rect();
//            bottomSheet.getGlobalVisibleRect(rect);
//            if(!rect.contains((int)event.getRawX(),(int)event.getRawY())){
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            }
//        }
//    }




    @Override
    public void refreshMapIfNecesary(PharmacyObjectMap updatedPharmacy) {
        if(mLastMarkerClicked.getPhone().equals(updatedPharmacy.getPhone())) {
            showPharmacyInBottomSheet(updatedPharmacy);
        }

    }

    @Override
    public void removeMarker(Marker marker) {

    }

    @Override
    public void showSnackBar(String message) {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
            }
        }, 30);
    }
    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }
    private void setStateBottomSheet(int state) {
        if(state==STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void setUpBotomSheet(){
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        //initially set hidden(in case there are no pharmacies around).Not working
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


    }
    public void updateClickedPhoneToPresenter(String phone) {
        mPresenter.updateFavoriteFlag(phone);


    }

    public void removeMarkerInPresenter(String phone) {
        mPresenter.removeMarkerInHashMapAndMapFromList(phone);

    }

    private void setUpIvFavorite(){
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.handleClickFavorite();

            }


        });
    }
    private void setUpIvGo(){
        ivGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mPresenter.handleClickGo();

            }
        });
    }

    public void setUpTvPhone(){

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.handleClickCall();
            }
        });
    }

    private void setUpIvCall(){
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleClickCall();

            }
        });

    }

    private void setUpIvShare(){
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleClickShare();
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

            mPresenter.handleOnMarkerClick(marker);
            if( !isBottomSheetExpanded()) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return false;
    }
    @Override
    public void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSortedPharmacy, PharmacyObjectMap lastClicked) {
        Util.logD(LOG_TAG,"preshowPharmacy");

        //if there has been a rotation
        if(mRotation) {
            if(lastClicked!=null) {
                Util.logD(LOG_TAG,"firstsor");
                //npe   java.lang.NullPointerException: Attempt to invoke virtual method 'double java.lang.Double.doubleValue()' on a null object reference
                addMarkerToMap(lastClicked);
                firstSortedPharmacy = lastClicked;
                //firstSortedPharmacy=mLastMarkerClicked;
                setStateBottomSheet(mBottomSheetState);
            }

            mRotation=false;
         }


        showPharmacyInBottomSheet(firstSortedPharmacy);
        mBottomSheetBehavior.setPeekHeight(llUper.getHeight());





    }

    @Override
    public void showPharmacyInBottomSheet(PharmacyObjectMap pharmacy){

            int color = pharmacy.isOpen() ? color_pharmacy_open : color_pharmacy_close;
            llUper.setBackgroundColor(color);
//            Marker marker=getKeyFromValue(pharmacy);
//            HashMap hashMap= mPresenter.onGetHashMap();
//            PharmacyObjectMap pharmacyObjectMap = (PharmacyObjectMap)hashMap.get(marker);
            Drawable favDraResid=ContextCompat.getDrawable(getActivity(),pharmacy.isFavorite()?R.drawable.heart:R.drawable.heart_outline);
            ivFavorite.setImageDrawable(favDraResid);
            setTintedVectorDrawable(ivCall, R.drawable.phone, color);
            setTintedVectorDrawable(ivGo, R.drawable.directions, color);
            setTintedVectorDrawable(ivDistance, R.drawable.distance, color);
            setTintedVectorDrawable(ivMarker, R.drawable.map_marker, color);
            setTintedVectorDrawable(ivPhone, R.drawable.phone, color);
            setTintedVectorDrawable(ivClock, R.drawable.clock, color);
            setTintedVectorDrawable(ivShare,R.drawable.share,color);

            tvName.setText(pharmacy.getName());
            ivOrder.setImageBitmap(pharmacy.getMarkerImage());

            tvCall.setTextColor(color);
            tvGo.setTextColor(color);
            tvShare.setTextColor(color);


            tvDistance.setText(getString(R.string.format_distance, pharmacy.getDistance() / 1000));
            tvAdress.setText(pharmacy.getAddressFormatted());
            tvHours.setText(pharmacy.getHours());
            tvPhone.setText(pharmacy.getPhoneFormatted());
            mPresenter.onSetLastMarkerClick(pharmacy);
            mLastMarkerClicked=pharmacy;




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

    private boolean isBottomSheetExpanded(){
       return mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED;
    }





//    @SuppressWarnings("ResourceType")
//    private void setStatusBarDim(boolean dim) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().getWindow().setStatusBarColor(dim ? Color.TRANSPARENT :
//                    ContextCompat.getColor(getActivity(), getThemedResId(R.attr.colorPrimaryDark)));
//        }
//    }
//
//    private int getThemedResId(@AttrRes int attr) {
//        TypedArray a = getActivity().getTheme().obtainStyledAttributes(new int[]{attr});
//        int resId = a.getResourceId(0, 0);
//        a.recycle();
//        return resId;
//    }



    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onDestroy() {
        Util.logD(LOG_TAG, "onDestroy");
        unbinder.unbind();
        super.onDestroy();
    }

}
