package com.chernandezgil.farmacias.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.DividerItemDecoration;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.presenter.FindPresenter;
import com.chernandezgil.farmacias.ui.adapter.FindAdapter;
import com.chernandezgil.farmacias.view.FindContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter;

/**
 * Created by Carlos on 10/07/2016.
 */
public class FindFragment extends Fragment implements FindContract.View{


    private static final String LOG_TAG =FindFragment.class.getSimpleName() ;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.findRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;


    private FindPresenter mPresenter;
    private Unbinder unbinder;
    private FindAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logD(LOG_TAG,"onCreate");
        LoaderProvider loaderProvider=new LoaderProvider(getActivity());
        LoaderManager loaderManager = getLoaderManager();
        mPresenter = new FindPresenter(loaderManager,loaderProvider);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.logD(LOG_TAG,"onCreateView");
        View view=inflater.inflate(R.layout.fragment_find,container,false);
        unbinder=ButterKnife.bind(this,view);
        setUpRecyclerView();
        mPresenter.setView(this);
        mPresenter.onStartLoader();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_find,menu);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView= (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("busca farmacia");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.onRestartLoader(newText);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Toast.makeText(getContext(),"onclick search", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.action_filter:
                Toast.makeText(getContext(),"onclick filter", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpRecyclerView(){

        mAdapter = new FindAdapter(getContext());
        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));


    }
    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
        mAdapter.swapData(pharmacyList);

    }

    @Override
    public void showNoResults() {

        mEmptyView.setVisibility(View.VISIBLE);
        mAdapter.swapData(null);

    }

    @Override
    public void hideNoResults() {
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        Util.logD(LOG_TAG,"onDestroyView");
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Util.logD(LOG_TAG,"onDestroy");
        super.onDestroy();
    }
}
