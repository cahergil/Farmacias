package com.chernandezgil.farmacias.ui.fragment;

import android.content.ContentValues;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.DividerItemDecoration;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.ListTabPresenter;
import com.chernandezgil.farmacias.ui.adapter.AndroidPrefsManager;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.ListContract;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter;

/**
 * Created by Carlos on 06/08/2016.
 */
public class ListTabFragment extends Fragment implements ListContract.View,ListTabAdapter.ListTabAdapterOnClickHandler {

    @BindView(R.id.pharmaciesRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    View mEmptyView;
    @BindView(R.id.txtLoading)
    TextView tvLoading;

    private Location mLocation;
    private ListTabPresenter mPresenter;
    private ListTabAdapter mAdapter;
    private String mAddress;
    private ArrayList<Pharmacy> mPharmacyList;
    private Unbinder unbinder;
    private static final String LOG_TAG=ListTabFragment.class.getSimpleName();
    private static final String RECYCLER_STATE_KEY ="recycler_key";
    private static final String LIST_PHARMACY_KEY="pharmacyList_key";
    private Parcelable mLayoutManagerState;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();

        if(savedInstanceState==null) {
            if (bundle != null) {
                mLocation=bundle.getParcelable("location_key");

             }
        } else {
            mLocation=savedInstanceState.getParcelable("location_key");
            mAddress = savedInstanceState.getString("address_key");
        }
        LoaderProvider loaderProvider = new LoaderProvider(getActivity());
        PreferencesManager preferencesManager = new AndroidPrefsManager(getActivity());
        mPresenter = new ListTabPresenter(mLocation,loaderProvider,getLoaderManager(),new Geocoder(getActivity()),preferencesManager);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab_list,container,false);
        unbinder=ButterKnife.bind(this,view);
        setUpRecyclerView();
        mPresenter.setView(this);
        if(savedInstanceState==null) {
           mPresenter.onGetAddressFromLocation(mLocation);
        } else {
            if(savedInstanceState.containsKey(RECYCLER_STATE_KEY)) {
                mLayoutManagerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            }
            if(savedInstanceState.containsKey(LIST_PHARMACY_KEY)) {
                mPharmacyList=savedInstanceState.getParcelableArrayList(LIST_PHARMACY_KEY);
            }

        }
        mPresenter.onStartLoader();
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key",mLocation);
        if(mAddress!=null) {
            outState.putString("address_key", mAddress);
        }
        outState.putParcelable(RECYCLER_STATE_KEY,mRecyclerView.getLayoutManager().onSaveInstanceState());
        if(mPharmacyList!=null) {
            outState.putParcelableArrayList(LIST_PHARMACY_KEY,mPharmacyList);
        }
    }


    private void setUpRecyclerView(){
        mAdapter=new ListTabAdapter(getActivity(),this);

   //     mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);
        mRecyclerView.setLayoutManager(new  LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
    //    mPharmacyList= (ArrayList<Pharmacy>) mAdapter.getPharmaList();
        //copy the state of the adapter to the results of Loader
        if(mPharmacyList!=null) {
            for (int i = 0;i<mPharmacyList.size();i++) {
                pharmacyList.get(i).setArrow_down(mPharmacyList.get(i).isArrow_down());
                pharmacyList.get(i).setOptionsRow(mPharmacyList.get(i).isOptionsRow());
            }
        }
        mAdapter.swapData(pharmacyList);
        if(mLayoutManagerState !=null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
        }
        mPharmacyList= (ArrayList<Pharmacy>) pharmacyList;
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

        mAddress=address;
    }


    @Override
    public void onClickGo(int position) {
        Util.LOGD(LOG_TAG,"onClickGo");
        Pharmacy pharmacy=mPharmacyList.get(position);
        Util.startGoogleDirections(getActivity(),
                new LatLng(mLocation.getLatitude(),mLocation.getLongitude()),
                mAddress,new LatLng(pharmacy.getLat(),pharmacy.getLon()),
                pharmacy.getAddressFormatted()
                );
    }

    @Override
    public void onClickFavorite( int position) {
        Pharmacy pharmacy=mPharmacyList.get(position);
        Uri uri= DbContract.FarmaciasEntity.buildFarmaciasUri(pharmacy.getPhone());
        ContentValues contentValues=new ContentValues();
        int favorite;

        if(pharmacy.isFavorite()) {
            favorite=0;
        } else {
            favorite=1;
        }
        String phone=pharmacy.getPhone();


        contentValues.put(DbContract.FarmaciasEntity.FAVORITE,favorite);

        int rowsUpdated=getActivity().getContentResolver().update(uri,contentValues,
                DbContract.FarmaciasEntity.PHONE + " LIKE '%" + phone + "%'",
                null);
        Util.LOGD(LOG_TAG,"rows updates: " + rowsUpdated);
//        for(int i =0; i<)
//        ListTabAdapter.ViewHolder holder=mRecyclerView.findViewHolderForAdapterPosition(position);
//        vh.isRecyclable()
    }

    @Override
    public void onClick(ListTabAdapter.ViewHolder vh) {

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

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
