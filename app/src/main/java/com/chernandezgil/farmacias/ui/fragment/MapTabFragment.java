package com.chernandezgil.farmacias.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.customwidget.CustomSupporMapFragment;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.PharmacyObjectMap;
import com.chernandezgil.farmacias.presenter.MapTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.MapTabContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Carlos on 10/07/2016.
 */
public class MapTabFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,MapTabContract.View {


    private static final String LOG_TAG = MapTabFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Location mLocation;
    TimeMeasure mTm;

    private BottomSheetBehavior mBottomSheetBehavior;
    private CustomSupporMapFragment mMapFragment;
    private boolean mRotation=false;
    private MapTabPresenter mMapTabPresenter;
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
    CustomSupporMapFragment mapFragment;

    public MapTabFragment() {
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
        PreferencesManager preferencesManager=new AndroidPrefsManager(getActivity());
        mMapTabPresenter =new MapTabPresenter(mLoaderProvider,mLoaderManager,mGeocoder,preferencesManager);
        mMapTabPresenter.setLocation(mLocation);
        mAddress = mMapTabPresenter.onGetAddressFromLocation(mLocation);
        mMapTabPresenter.onSetAddress(mAddress);
        mMapTabPresenter.onStartLoader();



    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.LOGD(LOG_TAG, "onCreateView:"+this.toString());
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
            mLocation = savedInstanceState.getParcelable("location_key");
            mLastMarkerClicked=savedInstanceState.getParcelable("lastMarkerClicked_key");
            mMapTabPresenter.onSetLastMarkerClick(mLastMarkerClicked);
            mBottomSheetState=savedInstanceState.getInt("bottom_sheet_state");
        }

        mMapTabPresenter.setView(this);
        markerBitmap=Util.getBitmapFromVectorDrawable(getActivity(),R.drawable.hospital_pin_stroke);
        mMapTabPresenter.onSetMarkerBitMap(markerBitmap);
        return view;
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
    //this callback executes after onstart
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Util.LOGD(LOG_TAG, "onMapsReady");
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
                            cu= mMapTabPresenter.getCameraUpdate();
                            Util.LOGD(LOG_TAG,"count"+count);
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
        //mapFragment.onResume();
        setUserVisibleHint(true);

    }

    //this is method to help us add a Marker to the map
    //BitmapDescriptorFactory.defaultMarker()
    //itmapDescriptorFactory.fromResource(R.drawable.ic_maps_position)
    @Override
    public void addMarkerToMap(PharmacyObjectMap pharmacyObjectMap,boolean flagFavorite) {
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(pharmacyObjectMap.getLat(), pharmacyObjectMap.getLon())
        );
        if(pharmacyObjectMap.getName().equals("userLocation")) {
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Tu ubicación")
                    .snippet(Util.getStreetFromAddress(pharmacyObjectMap.getAddressFormatted()));

            //   markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position));
        } else {
            Bitmap bitmap= mMapTabPresenter.onRequestCustomBitmap(pharmacyObjectMap.getOrder(), pharmacyObjectMap.isOpen());
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title(pharmacyObjectMap.getName())
                    .snippet(getString(R.string.format_distance, pharmacyObjectMap.getDistance()/1000));
        }
        markerOption.anchor(0.5f, 0.5f);
        if(flagFavorite) {

        }
        Marker newMark = mMap.addMarker(markerOption);
        newMark.showInfoWindow();//only last infowindow shows up
        mMapTabPresenter.onAddMarkerToHash(newMark, pharmacyObjectMap);

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
    private void handlePhoneCall(){
        String uri="tel:" + mMapTabPresenter.onGetDestinationPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }


    @Override
    public boolean collapseBottomSheet() {
       if(isBottomSheetExpanded()) {
           mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
           return true;
       }
       return false;
    }

    @Override
    public void handleDispatchTouchEvent(MotionEvent event) {
        if(isBottomSheetExpanded()){
            Rect rect=new Rect();
            bottomSheet.getGlobalVisibleRect(rect);
            if(!rect.contains((int)event.getRawX(),(int)event.getRawY())){
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }


    @Override
    public void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSortedPharmacy, PharmacyObjectMap lastClicked) {
        Util.LOGD(LOG_TAG,"preshowPharmacy");
        if(mRotation) {
            if(lastClicked!=null) {
                addMarkerToMap(lastClicked,false);
                firstSortedPharmacy=mLastMarkerClicked;
                setStateBottomShett(mBottomSheetState);
            }

            mRotation=false;
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }


        showPharmacyInBottomSheet(firstSortedPharmacy);
        //http://stackoverflow.com/questions/37822264/android-bottom-sheet-behavior-not-working-properly-views-not-show-on-first-run
        mBottomSheetBehavior.setPeekHeight(llUper.getHeight());
        //a trick to show expanded, else doesn't show
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);




    }

    @Override
    public void refreshMap(PharmacyObjectMap updatedPharmacy) {
        if(mFromListTab) {
            mFromListTab=false;
            if(mLastMarkerClicked.getPhone().equals(updatedPharmacy.getPhone())) {
                showPharmacyInBottomSheet(updatedPharmacy);
            }
            return;
        }
        showPharmacyInBottomSheet(updatedPharmacy);
    }

    @Override
    public void removeMarker(Marker marker) {

    }

    private void setStateBottomShett(int state) {
        if(state==STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    private void setUpIvGo(){
        ivGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng destinationLatLng= mMapTabPresenter.onGetDestinationLocale();
                String destinationAddress= mMapTabPresenter.onGetDestinationAddress();

                Util.startGoogleDirections(getActivity(),new LatLng(mLocation.getLatitude(),mLocation.getLatitude())
                        ,mAddress,
                        destinationLatLng
                        ,destinationAddress);

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
                String name=mLastMarkerClicked.getName();
                double dist=mLastMarkerClicked.getDistance()/1000;
                String dir=mLastMarkerClicked.getAddressFormatted();
                String tel=mLastMarkerClicked.getPhone();
                Util.startShare(getActivity(),name,dist,dir,tel);
            }
        });

    }

    private void setUpBotomSheet(){
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //initially set hidden(in case there are no pharmacies around).Not working
        mBottomSheetBehavior.setHideable(true);
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_COLLAPSED) {
                   // setStatusBarDim(false);
                } else if(newState==BottomSheetBehavior.STATE_EXPANDED) {
                   // setStatusBarDim(true);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
              //  Util.LOGD(LOG_TAG,""+slideOffset);
              //  fab.setAlpha(slideOffset);

            }
        });
    }
    public void updateClickedPhoneToPresenter(String phone,boolean fromListTab) {
        mFromListTab=fromListTab;
        mMapTabPresenter.updateFavoriteFlag(phone);
    }

    public void removeMarkerFromHashInPresenter(String phone) {
        mMapTabPresenter.removeMarkerInHashFromList(phone);

    }
    private void setUpIvFavorite(){
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone=mLastMarkerClicked.getPhone();
                updateClickedPhoneToPresenter(phone,false);
                phone=phone.replaceAll(" ","");
                mMapTabPresenter.removeMarkerInHashFromMap(mLastMarkerClicked);

                int favorite;
                String snackMessage;
                if(mLastMarkerClicked.isFavorite()) {
                    snackMessage="Pharmacy deleted from favorites";
                    favorite=0;
                } else {
                    snackMessage="Pharmacy added to favorites";
                    favorite=1;
                }
                Uri uri= DbContract.FarmaciasEntity.buildFarmaciasUri(phone);
                ContentValues contentValues=new ContentValues();
                contentValues.put(DbContract.FarmaciasEntity.FAVORITE,favorite);

                int rowsUpdated=getActivity().getContentResolver().update(uri,contentValues,
                        DbContract.FarmaciasEntity.PHONE + " LIKE '%" + phone + "%'",
                        null);
                if(rowsUpdated==1) {
                    Snackbar.make(mRootView,snackMessage,Snackbar.LENGTH_SHORT).show();

                }
                Util.LOGD(LOG_TAG,"rows updates: " + rowsUpdated);
            }


        });
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
            HashMap hashMap= mMapTabPresenter.onGetHashMap();
            PharmacyObjectMap pharmacyObjectMap = (PharmacyObjectMap)hashMap.get(marker);
            if(pharmacyObjectMap.getName().equals("userLocation")) {
                return false;
            }
            showPharmacyInBottomSheet(pharmacyObjectMap);
//            if( !isBottomSheetExpanded()) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
            return false;
    }


    private void showPharmacyInBottomSheet(PharmacyObjectMap pharmacy){

            int color = pharmacy.isOpen() ? color_pharmacy_open : color_pharmacy_close;
            llUper.setBackgroundColor(color);
//            Marker marker=getKeyFromValue(pharmacy);
//            HashMap hashMap= mMapTabPresenter.onGetHashMap();
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
            mMapTabPresenter.onSetLastMarkerClick(pharmacy);
            mLastMarkerClicked=pharmacy;

            if( !isBottomSheetExpanded()) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }



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



    @Override
    public void onDestroy() {
        Util.LOGD(LOG_TAG, "onDestroy");
        unbinder.unbind();
        super.onDestroy();
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

}
