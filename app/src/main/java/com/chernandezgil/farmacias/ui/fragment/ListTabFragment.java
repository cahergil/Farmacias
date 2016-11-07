package com.chernandezgil.farmacias.ui.fragment;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.customwidget.SnackBarWrapper;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.ListTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.item_animator.CustomItemAnimator;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.ListTabContract;
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
        ListTabAdapter.ListTabAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.pharmaciesRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    RelativeLayout mEmptyView;
    @BindView(R.id.frame)
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
    private UpdateFavorite mCallback;
    private boolean[] mSpandState;
    private boolean mRotation;
    private PreferencesManager mSharedPreferences;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (UpdateFavorite) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName()
                    + " must implement UpdateFavorite");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.logD(LOG_TAG, "onCreate");
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
        Utils.logD(LOG_TAG, "onCreateView");
        unbinder = ButterKnife.bind(this, view);
        setUpRecyclerView();

        if (savedInstanceState == null) {
            mPresenter.onGetAddressFromLocation(mLocation);
        } else {
            mRotation = true;
            if (savedInstanceState.containsKey(RECYCLER_STATE_KEY)) {
                mLayoutManagerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            }

            if (savedInstanceState.containsKey(EXPANDABLE_STATE_KEY)) {
                mSpandState = savedInstanceState.getBooleanArray(EXPANDABLE_STATE_KEY);
            }
            if (savedInstanceState.containsKey(ADDRESS_KEY)) {
                mAddress = savedInstanceState.getString(ADDRESS_KEY);
            }

        }

        //in both fragment, since map fragment could be the currentItem in TabLayout

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onStartLoader();
      //  setUserVisibleHint(true); setting it here doen't work
      // I opted for setting this value in the instantiation of
      //  the fragment in FragmentPagerAdapter. solution not valid after 24.0.0
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Utils.logD(LOG_TAG,"setUserVisibleHint:"+isVisibleToUser);
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
        Utils.logD(LOG_TAG, "onStart");
        super.onStart();


    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume");
        super.onResume();
        mSharedPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        Utils.logD(LOG_TAG, "onPause");

        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop");
        mSharedPreferences.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private void setUpRecyclerView() {
        CustomItemAnimator customItemAnimator = new CustomItemAnimator();
        mAdapter = new ListTabAdapter(getActivity(),this,mRecyclerView,customItemAnimator);
       // SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);

        mRecyclerView.setItemAnimator(customItemAnimator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ListTabAdapter getAdapter(){
       return (ListTabAdapter)  mRecyclerView.getAdapter();
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
    }

    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onClickGo(Pharmacy pharmacy) {
        Utils.logD(LOG_TAG, "onClickGo");

        mPresenter.handleClickGo(pharmacy,mLocation,mAddress);


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
    public void showSnackBar(String message) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
            }
        }, 30);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(mSharedPreferences.getLocationKey())) {

            //anado isAdded porque me dio este npe
            // Process: com.chernandezgil.farmacias, PID: 8683
            //java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.res.TypedArray android.content.Context.obtainStyledAttributes(android.util.AttributeSet, int[], int, int)' on a null object reference
            if(isAdded()) {
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
                Toast.makeText(getActivity(), "Presionado action", Toast.LENGTH_LONG).show();
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

    //    private static class ToastThread extends Thread {
//        private boolean mRunning = false;
//        private Toast toast;
//        public ToastThread(Toast toast) {
//            this.toast = toast;
//        }
//
//
//        Handler handler= new Handler(Looper.getMainLooper());
//        @Override
//        public void run() {
//            int count =0;
//            while (count<10) {
//                count++;
//               // SystemClock.sleep(1000);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        toast.show();
//                    }
//                });
//
//
//            }
//        }
//
//        public void close() {
//            mRunning = false;
//        }
//
//    }
    public interface UpdateFavorite {
        public void onUpdateFavorite(String phone, boolean fromListMap);
    }

    @Override
    public void onDestroyView() {
        if(mSnackBar != null) {
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
        Utils.logD(LOG_TAG, "onDestroy");
        unbinder.unbind();
        super.onDestroy();
    }
}
