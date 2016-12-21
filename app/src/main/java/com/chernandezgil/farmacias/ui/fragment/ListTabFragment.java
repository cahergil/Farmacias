package com.chernandezgil.farmacias.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.customwidget.ScrollerLinearLayoutManager;
import com.chernandezgil.farmacias.customwidget.dialog.DialogOpeningHoursPharmacy;
import com.chernandezgil.farmacias.customwidget.SnackBarWrapper;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.ListTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.item_animator.CustomItemAnimator;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.ListTabContract;
import com.chernandezgil.farmacias.view.MoveListToTop;
import com.github.andrewlord1990.snackbarbuilder.callback.SnackbarCallback;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Carlos on 06/08/2016.
 */
public class ListTabFragment extends Fragment implements ListTabContract.View,
        ListTabAdapter.ListTabAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoveListToTop {

    @BindView(R.id.pharmaciesRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    RelativeLayout mEmptyView;
    @BindView(R.id.frameRoot)
    FrameLayout mRootView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private Location mLocation;
    private ListTabPresenter mPresenter;
    private ListTabAdapter mAdapter;
    private SnackBarWrapper mSnackBar;
    private String mAddress;

    private Unbinder unbinder;
    private static final String LOG_TAG = ListTabFragment.class.getSimpleName();
    private static final String RECYCLER_STATE_KEY = "recycler_key";
    private static final String ADDRESS_KEY = "address_key";
    private static final String EXPANDABLE_STATE_KEY = "state_key";
    public static final String NEW_LOCATION = "new_location";
    private Parcelable mLayoutManagerState;
    private Callbacks mCallback;
    private boolean mRotation;
    private PreferencesManager mSharedPreferences;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName()
                    + " must implement Callbacks");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.logD(LOG_TAG, "onCreate:" + this);
        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        mLocation = mSharedPreferences.getLocation();
        LoaderProvider loaderProvider = new LoaderProvider(getActivity().getApplicationContext());
        mPresenter = new ListTabPresenter(loaderProvider, getLoaderManager(), new Geocoder(getActivity()), mSharedPreferences);
        mPresenter.setLocation(mLocation);
        mPresenter.setView(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_list, container, false);
        Utils.logD(LOG_TAG, "onCreateView:" + this);
        unbinder = ButterKnife.bind(this, view);
        setUpRecyclerView();
        if (savedInstanceState == null) {
            mPresenter.onGetAddressFromLocation(mLocation);
        } else {
            mRotation = true;
            if (savedInstanceState.containsKey(RECYCLER_STATE_KEY)) {
                mLayoutManagerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            }
            if (savedInstanceState.containsKey(ADDRESS_KEY)) {
                mAddress = savedInstanceState.getString(ADDRESS_KEY);
            }

        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onViewCreated:" + this);
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onStartLoader();
        //  setUserVisibleHint(true); solution not valid after 24.0.0 SL
        //  I opted for setting this value in the instantiation of
        //  the fragments in FragmentPagerAdapter.
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Utils.logD(LOG_TAG, "setUserVisibleHint:" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mAddress != null) {
            outState.putString(ADDRESS_KEY, mAddress);
        }
        outState.putParcelable(RECYCLER_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());


    }

    @Override
    public void onStart() {
        Utils.logD(LOG_TAG, "onStart:" + this);
        super.onStart();


    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume:" + this);
        super.onResume();
        mSharedPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        Utils.logD(LOG_TAG, "onPause:"+this);

        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop:"+this);
        mSharedPreferences.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private void setUpRecyclerView() {
        CustomItemAnimator customItemAnimator = new CustomItemAnimator();
        mAdapter = new ListTabAdapter(this, mRecyclerView, customItemAnimator);
        mRecyclerView.setItemAnimator(customItemAnimator);
        //    ScrollerLinearLayoutManager to manage tappin twice on BottomNavigation
        mRecyclerView.setLayoutManager(new ScrollerLinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
        Utils.logD(LOG_TAG, "showResults");
        mAdapter.swapData(pharmacyList);
        if (mLayoutManagerState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
        }

    }

    @Override
    public void showNoResults() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setAddress(String address) {

        mAddress = address;
        if (address != null && !address.equals(Constants.EMPTY_STRING)) {
            mCallback.onAddressUpdated(address);
        } else {
            mCallback.onAddressUpdated(Constants.EMPTY_STRING);
        }
    }

    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showOpeningHours(int layoutId, int backgroundColor) {
        DialogOpeningHoursPharmacy dialog = DialogOpeningHoursPharmacy.newInstance(layoutId, backgroundColor);
        dialog.show(getActivity().getSupportFragmentManager(), "DIALOG");

    }

    @Override
    public void onClickGo(Pharmacy pharmacy) {
        Utils.logD(LOG_TAG, "onClickGo");

        mPresenter.handleClickGo(pharmacy, mLocation, mAddress);


    }

    @Override
    public void onClickFavorite(Pharmacy pharmacy) {
        //the phone number acts as primary key
        String phone = pharmacy.getPhone();
        mCallback.onUpdateFavorite(phone, true);
        mPresenter.handleClickFavorite(pharmacy);


    }


    @Override
    public void onClickPhone(String phone) {
        mPresenter.handleClickCall(phone);

    }

    @Override
    public void onClickShare(Pharmacy pharmacy) {
        mPresenter.handleClickShare(pharmacy);
    }

    @Override
    public void onClickOpeningHours(Pharmacy pharmacy) {
        mPresenter.onClickOpeningHours(pharmacy);
    }

    @Override
    public void showSnackBar(String message) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                SnackBarWrapper snackBarWrapper = new SnackBarWrapper(getActivity(), message, Snackbar.LENGTH_SHORT);
                snackBarWrapper.show();
            }
        }, 30);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(mSharedPreferences.getLocationKey())) {

            //anado isAdded porque me dio este npe
            // Process: com.chernandezgil.farmacias, PID: 8683
            //java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.res.TypedArray android.content.Context.obtainStyledAttributes(android.util.AttributeSet, int[], int, int)' on a null object reference
            if (isAdded()) {
                mSnackBar = new SnackBarWrapper(getActivity());
                mSnackBar.addCallback(createSnackbarCallback());
                mSnackBar.show();
            }

        }
    }

    private SnackbarCallback createSnackbarCallback() {
        return new SnackbarCallback() {
            @Override
            public void onSnackbarActionPressed(Snackbar snackbar) {
                //Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                //update location variable
                mLocation = mSharedPreferences.getLocation();
                //get the new address
                mPresenter.onGetAddressFromLocation(mLocation);
                // set location in Presenter
                mPresenter.setLocation(mLocation);
                mPresenter.onStartLoader();
                Intent intent = new Intent(NEW_LOCATION);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }

            @Override
            public void onSnackbarSwiped(Snackbar snackbar) {
                //showToast("Swiped");
            }

            @Override
            public void onSnackbarTimedOut(Snackbar snackbar) {
                // showToast("Timed out");
            }
        };
    }

    @Override
    public void moveSmoothToTop() {
        if (mAdapter.getItemCount() != 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }


    public interface Callbacks {
        void onUpdateFavorite(String phone, boolean fromListMap);

        void onAddressUpdated(String address);
    }

    @Override
    public void onDestroyView() {
        Utils.logD(LOG_TAG, "onDestroyView:"+this);
        if (mSnackBar != null) {
            mSnackBar.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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
        Utils.logD(LOG_TAG, "onDestroy:"+this);
        unbinder.unbind();
        super.onDestroy();
    }
}
