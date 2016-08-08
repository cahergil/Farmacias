package com.chernandezgil.farmacias.ui.fragment;

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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(savedInstanceState==null) {
            if (bundle != null) {
                mLocation=bundle.getParcelable("location_key");
                LoaderProvider loaderProvider = new LoaderProvider(getActivity());
                mPresenter = new ListPresenter(mLocation,loaderProvider,getLoaderManager());

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab_list,container,false);
        ButterKnife.bind(this,view);
        setUpRecyclerView();
        mPresenter.setView(this);
        mPresenter.onStartLoader();
        return view;
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
    public void onClickGo(ListTabAdapter.ViewHolder vh,int position) {

//        Util.startGoogleDirections(getActivity(),
//                new LatLng(mLocation.getLatitude(),mLocation.getLongitude()),
//                );
    }

    @Override
    public void onClick(ListTabAdapter.ViewHolder vh) {

    }
}
