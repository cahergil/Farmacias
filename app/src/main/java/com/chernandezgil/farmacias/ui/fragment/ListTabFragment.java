package com.chernandezgil.farmacias.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.ListTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.ListTabContract;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter;

/**
 * Created by Carlos on 06/08/2016.
 */
public class ListTabFragment extends Fragment implements ListTabContract.View, ListTabAdapter.ListTabAdapterOnClickHandler {

    @BindView(R.id.pharmaciesRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    View mEmptyView;
    @BindView(R.id.txtLoading)
    TextView tvLoading;
    @BindView(R.id.frame)
    FrameLayout mRootView;

    private Location mLocation;
    private ListTabPresenter mPresenter;
    private ListTabAdapter mAdapter;
    private String mAddress;
    private List<Pharmacy> mPharmacyList;
    private Unbinder unbinder;
    private static final String LOG_TAG = ListTabFragment.class.getSimpleName();
    private static final String RECYCLER_STATE_KEY = "recycler_key";
    private static final String LIST_PHARMACY_KEY = "pharmacyList_key";
    private Parcelable mLayoutManagerState;
    private UpdateFavorite mCallback;
    private boolean[] mSpandState;
    private boolean mRotation;

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
        Bundle bundle = getArguments();

        if (savedInstanceState == null) {
            if (bundle != null) {
                mLocation = bundle.getParcelable("location_key");

            }
        } else {
            mRotation=true;
            mLocation = savedInstanceState.getParcelable("location_key");
            mAddress = savedInstanceState.getString("address_key");
        }
        LoaderProvider loaderProvider = new LoaderProvider(getActivity());
        PreferencesManager preferencesManager = new AndroidPrefsManager(getActivity());
        mPresenter = new ListTabPresenter(mLocation, loaderProvider, getLoaderManager(), new Geocoder(getActivity()), preferencesManager);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUpRecyclerView();
        mPresenter.setView(this);
        if (savedInstanceState == null) {
            mPresenter.onGetAddressFromLocation(mLocation);
        } else {
            if (savedInstanceState.containsKey(RECYCLER_STATE_KEY)) {
                mLayoutManagerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            }
            if (savedInstanceState.containsKey(LIST_PHARMACY_KEY)) {
                mPharmacyList = savedInstanceState.getParcelableArrayList(LIST_PHARMACY_KEY);
            }
            if(savedInstanceState.containsKey("state_key")) {
                mSpandState = savedInstanceState.getBooleanArray("state_key");
            }

        }
        mPresenter.onStartLoader();
        //in both fragment, since map fragment could be the currentItem in TabLayout
        setUserVisibleHint(true);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key", mLocation);
        if (mAddress != null) {
            outState.putString("address_key", mAddress);
        }
        outState.putParcelable(RECYCLER_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
//        if (mPharmacyList != null) {
//            outState.putParcelableArrayList(LIST_PHARMACY_KEY, mPharmacyList);
//        }
        if(mAdapter != null) {

            outState.putBooleanArray("state_key",mAdapter.getExpandStateArray());

        }
    }


    private void setUpRecyclerView() {
        mAdapter = new ListTabAdapter(getActivity(), this);

        //     mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
        Util.logD(LOG_TAG,"showResults");
        mAdapter.setExpandStateArray(mSpandState,mRotation);
        if(mRotation){
            mRotation=false;
        }
        mAdapter.swapData(pharmacyList);
        if (mLayoutManagerState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
        }
        mPharmacyList=pharmacyList;

    }

    @Override
    public void showNoResults() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        tvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        tvLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setAddress(String address) {

        mAddress = address;
    }


    @Override
    public void onClickGo(int position) {
        Util.logD(LOG_TAG, "onClickGo");
        Pharmacy pharmacy = mPharmacyList.get(position);
        Util.startGoogleDirections(getActivity(),
                new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),
                mAddress, new LatLng(pharmacy.getLat(), pharmacy.getLon()),
                pharmacy.getAddressFormatted()
        );
    }

    @Override
    public void onClickFavorite(int position) {
        //the phone number acts as primary key
        Pharmacy pharmacy = mPharmacyList.get(position);
        String phone = pharmacy.getPhone();
        mCallback.onUpdateFavorite(phone,true);
        int favorite;
        String snackMessage;
        if (pharmacy.isFavorite()) {
            snackMessage = "Pharmacy deleted from favorites";
            favorite = 0;
        } else {
            snackMessage = "Pharmacy added to favorites";
            favorite = 1;
        }

        Uri uri = DbContract.FarmaciasEntity.buildFarmaciasUri(phone);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FarmaciasEntity.FAVORITE, favorite);

        int rowsUpdated = getActivity().getContentResolver().update(uri, contentValues,
                DbContract.FarmaciasEntity.PHONE + " LIKE '%" + phone + "%'",
                null);
        if (rowsUpdated == 1) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mRootView, snackMessage, Snackbar.LENGTH_SHORT).show();
                }
            },30);

        }
        Util.logD(LOG_TAG, "rows updates: " + rowsUpdated);
//        for(int i =0; i<)
//        ListTabAdapter.ViewHolder holder=mRecyclerView.findViewHolderForAdapterPosition(position);
//        vh.isRecyclable()
    }

    public interface UpdateFavorite {
        public void onUpdateFavorite(String phone,boolean fromListMap);
    }
    @Override
    public void onClick(ListTabAdapter.ViewHolder vh) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback=null;
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
        unbinder.unbind();
        super.onDestroy();
    }
}
