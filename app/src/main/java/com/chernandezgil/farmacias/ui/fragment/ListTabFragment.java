package com.chernandezgil.farmacias.ui.fragment;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import com.chernandezgil.farmacias.model.CustomMarker;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.ListPresenter;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;
import com.chernandezgil.farmacias.view.ListContract;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private ListPresenter mPresenter;
    private ListTabAdapter mAdapter;
    private String mAddress;
    private List<Pharmacy> mPharmacyList;
    private Unbinder unbinder;

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
        mPresenter = new ListPresenter(mLocation,loaderProvider,getLoaderManager(),new Geocoder(getActivity()));


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
    }

    private void setUpRecyclerView(){
        mAdapter=new ListTabAdapter(getActivity(),mEmptyView,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
        mAdapter.swapData(pharmacyList);
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
        mAddress=address;
    }


    @Override
    public void onClickGo(ListTabAdapter.ViewHolder vh,int position) {

        Pharmacy pharmacy=mPharmacyList.get(position);
        Util.startGoogleDirections(getActivity(),
                new LatLng(mLocation.getLatitude(),mLocation.getLongitude()),
                mAddress,new LatLng(pharmacy.getLat(),pharmacy.getLon()),
                pharmacy.getAddressFormatted()
                );
    }

    @Override
    public void onClickToogle(ListTabAdapter.ViewHolder vh, int position) {
//        for(int i =0; i<)
//        ListTabAdapter.ViewHolder holder=mRecyclerView.findViewHolderForAdapterPosition(position);
//        vh.isRecyclable()
    }

    @Override
    public void onClick(ListTabAdapter.ViewHolder vh) {

    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
