package com.chernandezgil.farmacias.ui.fragment;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.customwidget.SnackBarWrapper;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.customwidget.CustomSupporMapFragment;
import com.chernandezgil.farmacias.customwidget.dialog.DialogOpeningHoursPharmacy;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.CustomCameraUpdate;
import com.chernandezgil.farmacias.model.PharmacyObjectMap;
import com.chernandezgil.farmacias.presenter.MapTabPresenter;
import com.chernandezgil.farmacias.ui.activity.MainActivity;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.MapTabContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Carlos on 10/07/2016.
 */
public class MapTabFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, MapTabContract.View {


    private static final String LOG_TAG = MapTabFragment.class.getSimpleName();
    public static final String USER_LOCATION = "userLocation";
    private static final int USER_CIRCLE_RADIO = 150; //meters
    private static final int USER_CIRCLE_ANIMATION_MS = 3000;
    private static final int STATE_COLLAPSED = 0;
    private static final int STATE_EXPANDED = 1;
    private static final String LOCATION_KEY ="location_key";
    private static final String LAST_MARKER_KEY ="lastMarkerClicked_key";
    private static final String BOTTOM_SHEET_STATE= "bottom_sheet_state";
    private GoogleMap mMap;
    private Location mLocation;
    private TimeMeasure mTm;

    private BottomSheetBehavior mBottomSheetBehavior;
    private CustomSupporMapFragment mMapFragment;
    private boolean mRotation = false;
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

    @BindView(R.id.zoomPlus)
    ImageView ivZoomPlus;
    @BindView(R.id.zoomMinus)
    ImageView ivZoomMinus;


    @BindView(R.id.wrapperCoordinator)
    CoordinatorLayout mWrapperCoordinator;

    private Geocoder mGeocoder;
    private PharmacyObjectMap mLastMarkerClicked;
    private Bitmap markerBitmap;
    private Unbinder unbinder;

    private int mBottomSheetState;
    private boolean mFromListTab;
    private CustomSupporMapFragment mapFragment;
    private PreferencesManager mSharedPreferences;
    private SnackBarWrapper mSnackBar;

    private boolean mCancelThread;
    private ValueAnimator mAnimator;

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.logD(LOG_TAG, "onReceive");
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
        Point point = projection.toScreenLocation(latLng);

        Rect rect = new Rect();
        bottomSheet.getGlobalVisibleRect(rect);
        if (!rect.contains(point.x, point.y)) {
            Utils.logD(LOG_TAG, "outside bottom sheet");
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public MapTabFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mTm = new TimeMeasure(LOG_TAG);
        Utils.logD(LOG_TAG, "onCreate:" + this);
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        mLocation = mSharedPreferences.getLocation();
        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
        mLoaderProvider = new LoaderProvider(getActivity());
        mLoaderManager = getLoaderManager();
        mPresenter = new MapTabPresenter(mLoaderProvider, mLoaderManager, mGeocoder, mSharedPreferences);
        mPresenter.setView(this);
        mPresenter.setLocation(mLocation);
        mAddress = mPresenter.onGetAddressFromLocation(mLocation);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onCreateView:" + this);
        View view = inflater.inflate(R.layout.fragment_tab_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUpBotomSheet();
        setUpTvPhone();
        setUpIvCall();
        setUpIvGo();
        setUpIvShare();
        setUpIvFavorite();
        setUpIvOpeningHours();
        setUpZoomControls();


        mapFragment = Utils.handleMapFragmentRecreation(getChildFragmentManager(),
                R.id.mapFragmentContainer, "mapFragment");
        mapFragment.getMapAsync(this);

        mMapFragment = mapFragment;
        if (savedInstanceState != null) {
            mRotation = true;
            mLastMarkerClicked = savedInstanceState.getParcelable(LAST_MARKER_KEY);
            mPresenter.onSetLastMarkerClick(mLastMarkerClicked);
            mBottomSheetState = savedInstanceState.getInt(BOTTOM_SHEET_STATE);
        }


        markerBitmap = Utils.getBitmapFromVectorDrawable(getActivity().getApplicationContext(), R.drawable.hospital_pin_stroke);
        mPresenter.onSetMarkerBitMap(markerBitmap);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onViewCreated:"+this);
        super.onViewCreated(view, savedInstanceState);
        //start the loader once the view is ready
        mPresenter.onStartLoader();
        //  setUserVisibleHint(true); setting it here doesn't work
        //  I opted for setting this value in the instantiation of
        //  the fragment in FragmentPagerAdapter.
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //Utils.logD(LOG_TAG, "setUserVisibleHint:" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LOCATION_KEY, mLocation);
        outState.putParcelable(LAST_MARKER_KEY, mLastMarkerClicked);
        outState.putInt(BOTTOM_SHEET_STATE, mBottomSheetBehavior.getState());
    }

    @Override
    public void onStart() {
        Utils.logD(LOG_TAG, "onStart:"+this);
        super.onStart();


    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume:"+this);
        super.onResume();
        IntentFilter filter = new IntentFilter(ListTabFragment.NEW_LOCATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationReceiver, filter);
    }

    @Override
    public void onPause() {
        Utils.logD(LOG_TAG, "onPause:"+this);
        super.onPause();

    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop:"+this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);
        super.onStop();
    }

    //this callback executes after onstart
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Utils.logD(LOG_TAG, "onMapsReady");
        //mTm.log("onMapsReady:"+this.toString());
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //to disable the two little images in the right-bottom side of the map
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(LOG_TAG, "onmapLoaded");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        CustomCameraUpdate cu = null;
                        while (cu == null && !mCancelThread) {
                            count++;
                            cu = mPresenter.getCameraUpdate();
                            Utils.logD(LOG_TAG, "count" + count);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (count > 15) mCancelThread = true;
                        }
                        if (!mCancelThread) {
                            moveCamera(cu);
                        }
                    }
                });

            }
        });

        mMap.setOnMapClickListener(this);

    }


    //this is method to ic_help us add a Marker to the map
    //BitmapDescriptorFactory.defaultMarker()
    //itmapDescriptorFactory.fromResource(R.drawable.ic_maps_position)
    @Override
    public void addMarkerToMap(PharmacyObjectMap pharmacyObjectMap) {


        if (pharmacyObjectMap == null) {
            return;
        }
        if (!isAdded()) return;
        MarkerOptions markerOption = new MarkerOptions();
        double lat = pharmacyObjectMap.getLat();
        double lon = pharmacyObjectMap.getLon();
        markerOption.position(new LatLng(lat, lon));

        if (pharmacyObjectMap.getName().equals(USER_LOCATION)) {
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(getString(R.string.mtf_tu_ubicacion))
                    .snippet(mSharedPreferences.getStreet());

            //cancel previous animation
            if (mAnimator != null && mAnimator.isRunning()) {
                //mAnimator.cancel();
                mAnimator.end();
            }
            Paint strokePaint = new Paint();
            strokePaint.setColor(Color.parseColor("#0D47A1"));
            Paint fillPaint = new Paint();
            fillPaint.setColor(Color.parseColor("#90CAF9"));

            Circle circle = mMap.addCircle(
                    new CircleOptions()
                            .center(new LatLng(lat, lon))
                            .strokeWidth(4f)
                            .strokeColor(strokePaint.getColor())
                            .fillColor(fillPaint.getColor())
                            .radius(USER_CIRCLE_RADIO)

            );
            mAnimator = new ValueAnimator();
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setIntValues(0, 1);
            mAnimator.setDuration(USER_CIRCLE_ANIMATION_MS);
            mAnimator.setEvaluator(new IntEvaluator());
            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    strokePaint.setAlpha((int) ((1 - animatedFraction) * 255));
                    fillPaint.setAlpha((int) ((1 - animatedFraction) * 255));
                    circle.setRadius(animatedFraction * USER_CIRCLE_RADIO);
                    circle.setStrokeColor(strokePaint.getColor());
                    circle.setFillColor(fillPaint.getColor());

                }
            });
            //if I start the animator here, the zoom stops working


        } else {
            String distance = getString(R.string.format_distance, pharmacyObjectMap.getDistance() / 1000);
            Bitmap bitmap = mPresenter.onRequestCustomBitmap(pharmacyObjectMap.getOrder(), pharmacyObjectMap.isOpen());
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title(pharmacyObjectMap.getName())
                    .snippet(distance);
        }
        markerOption.anchor(0.5f, 0.5f);

        Marker newMark = mMap.addMarker(markerOption);
        newMark.showInfoWindow();//only last infowindow shows up
        mPresenter.onAddMarkerToHash(newMark, pharmacyObjectMap);

    }


    @Override
    public void moveCamera(CustomCameraUpdate cameraUpdate) {
        if (cameraUpdate.isNoResultsPosition()) {
            handleNoResults((cameraUpdate));
        } else {
            mBottomSheetBehavior.setState(mBottomSheetBehavior.STATE_COLLAPSED);
            mMap.animateCamera(cameraUpdate.getmCameraUpdate());


        }
        mAnimator.start();

    }

    private void handleNoResults(CustomCameraUpdate cameraUpdate) {
        mMap.moveCamera(cameraUpdate.getmCameraUpdate());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        list.add(new LatLng(39.63108, -7.62451));
        list.add(new LatLng(37.92687, -7.59155));
        list.add(new LatLng(37.92687, -4.52637));
        list.add(new LatLng(40.41768, -4.63623));
        list.add(new LatLng(40.54929, -6.97083));
        list.add(new LatLng(39.63108, -7.62451));
        list.add(new LatLng(37.92687, -7.59155));
        list.add(new LatLng(37.92687, -4.52637));
        String message;
        if (Utils.contains(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), list)) {
            message = getString(R.string.mtf_radio_busqueda_insuficiente);
            // Snackbar.make(mRootView,message,Snackbar.LENGTH_INDEFINITE).show();
        } else {
            message = getString(R.string.mtf_fuera_de_extremadura);
        }

        mSnackBar = new SnackBarWrapper(getActivity(),message,Snackbar.LENGTH_INDEFINITE);
        mSnackBar.show();

        //Snackbar.make(mRootView, message, Snackbar.LENGTH_INDEFINITE).show();
    }


    @Override
    public boolean collapseBottomSheet() {
        if (isBottomSheetExpanded()) {
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
        if (mLastMarkerClicked.getPhone().equals(updatedPharmacy.getPhone())) {
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

    @Override
    public void setStateBottomSheet(int state) {
        if (state == STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void setUpBotomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        //initially set hidden(in case there are no pharmacies around).Not working
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


    }

    public void updateClickedPhoneToPresenter(String phone) {
        mPresenter.updateFavoriteFlag(phone);


    }

    public void removeMarkerInPresenter(String phone) {
        mPresenter.removeMarkerInHashMapAndMapFromList(phone);

    }

    private void setUpIvFavorite() {
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.handleClickFavorite();

            }


        });
    }

    private void setUpIvGo() {
        ivGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleClickGo();

            }
        });
    }

    public void setUpTvPhone() {

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.handleClickCall();
            }
        });
    }

    private void setUpIvCall() {
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleClickCall();

            }
        });

    }

    private void setUpIvShare() {
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleClickShare();
            }
        });

    }

    private void setUpIvOpeningHours() {
        tvHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.handleClickOpeningHours();
            }
        });

    }

    @Override
    public void showOpeningHours(int layoutId, int backgroundColor) {

        DialogOpeningHoursPharmacy dialog = DialogOpeningHoursPharmacy.newInstance(layoutId, backgroundColor);
        dialog.show(getActivity().getSupportFragmentManager(), "DIALOG");

    }

    private void setUpZoomControls() {
        ivZoomPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        ivZoomMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());

            }
        });
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        mPresenter.handleOnMarkerClick(marker);
        return false;
    }

    @Override
    public void preShowPharmacyInBottomSheet(PharmacyObjectMap firstSortedPharmacy, PharmacyObjectMap lastClicked) {
        Utils.logD(LOG_TAG, "preshowPharmacy");

        //if there has been a rotation
        if (mRotation) {
            if (lastClicked != null) {
                Utils.logD(LOG_TAG, "firstsor");
                //npe   java.lang.NullPointerException: Attempt to invoke virtual method 'double java.lang.Double.doubleValue()' on a null object reference
                addMarkerToMap(lastClicked);
                firstSortedPharmacy = lastClicked;
                //firstSortedPharmacy=mLastMarkerClicked;
                setStateBottomSheet(mBottomSheetState);
            }

            mRotation = false;
        }


        showPharmacyInBottomSheet(firstSortedPharmacy);
        mBottomSheetBehavior.setPeekHeight(llUper.getHeight());


    }

    @Override
    public void showPharmacyInBottomSheet(PharmacyObjectMap pharmacy) {

        int color = pharmacy.isOpen() ? color_pharmacy_open : color_pharmacy_close;
        llUper.setBackgroundColor(color);
//            Marker marker=getKeyFromValue(pharmacy);
//            HashMap hashMap= mPresenter.onGetHashMap();
//            PharmacyObjectMap pharmacyObjectMap = (PharmacyObjectMap)hashMap.get(marker);
        Drawable favDraResid = ContextCompat.getDrawable(getActivity(), pharmacy.isFavorite() ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        ivFavorite.setImageDrawable(favDraResid);
        tintVectorDrawable(ivCall, color);
        tintVectorDrawable(ivGo, color);
        tintVectorDrawable(ivDistance, color);
        tintVectorDrawable(ivMarker, color);
        tintVectorDrawable(ivPhone, color);
        tintVectorDrawable(ivClock, color);
        tintVectorDrawable(ivShare, color);

        tvName.setText(pharmacy.getName());
        ivOrder.setImageBitmap(pharmacy.getMarkerImage());

        tvCall.setTextColor(color);
        tvGo.setTextColor(color);
        tvShare.setTextColor(color);


        tvDistance.setText(getString(R.string.format_distance, pharmacy.getDistance() / 1000));
        tvAdress.setText(pharmacy.getAddressFormatted());
//        boolean open = Utils.isPharmacyOpen(pharmacy.getHours());
//        tvHours.setText(open ? getString(R.string.mtf_openinghour_open) : getString(R.string.mtf_openinghour_close));
        tvHours.setText(getString(R.string.horario));
        tvPhone.setText(pharmacy.getPhoneFormatted());
        mPresenter.onSetLastMarkerClick(pharmacy);
        mLastMarkerClicked = pharmacy;


    }

    public void tintVectorDrawable(ImageView imageView, int color) {

        DrawableCompat.setTint(imageView.getDrawable(), color);

    }

    @Override
    public boolean isBottomSheetExpanded() {
        return mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }


    public void setBottomSheetPosition() {
        final int screenHeight = Utils.getScreenHeight(getActivity());
        Utils.logD(LOG_TAG, "Screen height:" + screenHeight);

        final int[] position = new int[2];
        mWrapperCoordinator.getLocationInWindow(position);

        final int[] position1 = new int[2];
        mWrapperCoordinator.getLocationOnScreen(position1);

        final int posBottomWrapperCoordinator = position[1] + mWrapperCoordinator.getHeight();
        final int posBottomWrapperCoordinator1 = position1[1] + mWrapperCoordinator.getHeight();

        Utils.logD(LOG_TAG, "pos:" + position[1]);
        Utils.logD(LOG_TAG, "pos1:" + position1[1]);

        Utils.logD(LOG_TAG, "y+height:" + posBottomWrapperCoordinator);
        Utils.logD(LOG_TAG, "y1+height:" + posBottomWrapperCoordinator1);
        final int bottomNavigationHeight = ((MainActivity) getContext()).getBottomNavigationView().getHeight();
        Utils.logD(LOG_TAG, "bottom navigation height:" + bottomNavigationHeight);
        Utils.logD(LOG_TAG, "y1+height+bnheight:" + (posBottomWrapperCoordinator + bottomNavigationHeight));

        mWrapperCoordinator.setTranslationY(0);
        mWrapperCoordinator.setBottom(Utils.getScreenHeight(getActivity()));
    }

    public void translateYBottomSheet(float offset) {
        mWrapperCoordinator.setTranslationY(offset);
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
    public void onDestroyView() {
        Utils.logD(LOG_TAG, "onDestroyView:"+this);
        mCancelThread = true;
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (mSnackBar != null) {
            mSnackBar.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Utils.logD(LOG_TAG, "onDestroy:"+this);
        unbinder.unbind();
        super.onDestroy();
    }

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
