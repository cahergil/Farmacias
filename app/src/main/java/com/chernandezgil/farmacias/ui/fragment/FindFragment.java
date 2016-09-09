package com.chernandezgil.farmacias.ui.fragment;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.DividerItemDecoration;
import com.chernandezgil.farmacias.Utilities.SearchUtils;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.data.source.local.RecentSuggestionsProvider;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;
import com.chernandezgil.farmacias.presenter.FindPresenter;
import com.chernandezgil.farmacias.ui.adapter.FindRecyclerViewAdapter;
import com.chernandezgil.farmacias.ui.adapter.FindSuggestionsAdapter;
import com.chernandezgil.farmacias.ui.adapter.QuickSearchAdapter;
import com.chernandezgil.farmacias.view.FindContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter;

/**
 * Created by Carlos on 10/07/2016.
 */
public class FindFragment extends Fragment implements FindContract.View,QuickSearchAdapter.OnClickHandler {


    private static final String LOG_TAG = FindFragment.class.getSimpleName();
    private static final int SUGGESTION_LOADER = 1;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.findRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;

    //Activity UI elements
    private RecyclerView mQuickSearchRecyclerView;
    private EditText mSearchText;
    private ImageView mClearSearch;
    private CardView mSearchCardView;
    private ImageView mImageSearchBack;

    private FindPresenter mPresenter;
    private Unbinder unbinder;
    private FindRecyclerViewAdapter mAdapter;
    private FindSuggestionsAdapter mSuggestionsAdapter;
    private SearchRecentSuggestions mRecentSuggestions;
    private QuickSearchAdapter mQuickSearchAdapter;
    private RelativeLayout mViewSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logD(LOG_TAG, "onCreate");
        LoaderProvider loaderProvider = new LoaderProvider(getActivity());
        LoaderManager loaderManager = getLoaderManager();
        mPresenter = new FindPresenter(loaderManager, loaderProvider);
        setHasOptionsMenu(true);
        mRecentSuggestions = new SearchRecentSuggestions(getContext(),
                RecentSuggestionsProvider.AUTHORITY, RecentSuggestionsProvider.MODE);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.logD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUpRecyclerView();
        mPresenter.setView(this);
        //   mPresenter.onStartLoader();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSearchUiWidgets();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_find, menu);
//        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
//
//        searchView.setQueryHint("busca farmacia");
//
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                Toast.makeText(getActivity(),"focuschange",Toast.LENGTH_SHORT).show();
//            }
//        });
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //   return query.length()>2;
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//          //      mPresenter.onRestartLoader(newText);
//                return true;
//            }
//        });
 //       mSuggestionsAdapter = new FindSuggestionsAdapter(getContext(), null, SUGGESTION_LOADER);
 //       searchView.setSuggestionsAdapter(mSuggestionsAdapter);
//        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionSelect(int i) {
//                return false;
//            }
//
//            @Override
//            public boolean onSuggestionClick(int i) {
//                Cursor c = (Cursor) mSuggestionsAdapter.getItem(i);
//
//                String name= c.getString(FindSuggestionsAdapter.COL_NAME);
//                mRecentSuggestions.saveRecentQuery(name,null);
//
//                searchView.setQuery("", false);
//                searchView.setIconified(true);
//                return true;
//            }
//        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Toast.makeText(getContext(), "onclick search", Toast.LENGTH_SHORT).show();
                mPresenter.onStartLoaderQuickSearch("");
                SearchUtils.setUpAnimations(getContext(),mSearchCardView,mViewSearch, mQuickSearchRecyclerView);
                return true;


            case R.id.action_filter:
                Toast.makeText(getContext(), "onclick filter", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeSearchUiWidgets(){

        setUpQuickSearchRecyclerView();

        mSearchText = (EditText) getActivity().findViewById(R.id.edit_text_search);
        mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isVisible) {
                if(isVisible) {
                    showQuickSearchRecyclerView();
                }
            }
        });
        mClearSearch = (ImageView) getActivity().findViewById(R.id.clearSearch);
        mSearchCardView = (CardView) getActivity().findViewById(R.id.card_search);
        mViewSearch =(RelativeLayout)getActivity().findViewById(R.id.view_search);
        mImageSearchBack = (ImageView) getActivity().findViewById(R.id.image_search_back);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPresenter.onStartLoaderQuickSearch(mSearchText.getText().toString());
                if(mSearchText.getText().length()>0) {
                    mClearSearch.setVisibility(View.VISIBLE);
                } else {
                    mClearSearch.setVisibility(View.INVISIBLE);
                }
            }
        });
        mImageSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchUtils.setUpAnimations(getContext(),mSearchCardView,mViewSearch, mQuickSearchRecyclerView);
                //delete current text so that in the next appearance don't show
                clearSearchEditor();
            }
        });
        mClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSearchEditor();
            }
        });

    }
    private void clearSearchEditor(){
        mSearchText.setText(Constants.EMPTY_STRING);
    }
    private void setUpQuickSearchRecyclerView() {
        mQuickSearchRecyclerView = (RecyclerView) getActivity().findViewById(R.id.listView);
        mQuickSearchAdapter = new QuickSearchAdapter(getContext(),this);
        mQuickSearchRecyclerView.setAdapter(mQuickSearchAdapter);

        mQuickSearchRecyclerView.setHasFixedSize(true);
        mQuickSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void showQuickSearchRecyclerView(){
        mSearchCardView.post(new Runnable() {
            @Override
            public void run() {
                mQuickSearchRecyclerView.setVisibility(View.VISIBLE);
            }
        });

    }
    private void hideSearchCardView(){
        mSearchCardView.setVisibility(View.INVISIBLE);
    }




    private void setUpRecyclerView() {

        mAdapter = new FindRecyclerViewAdapter(getContext());
        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));


    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
            mAdapter.swapData(pharmacyList);
            mQuickSearchRecyclerView.setVisibility(View.GONE);
 //       MatrixCursor c = transformListInToCursor(pharmacyList); //MatrixCursor implements Cursor interface;
 //       mSuggestionsAdapter.swapCursor(c);

    }

    public MatrixCursor transformListInToCursor(List<Pharmacy> pharmacyList) {
        if (pharmacyList == null) return null;
        String[] columnNames = {DbContract.FarmaciasEntity._ID,
                DbContract.FarmaciasEntity.NAME,
                DbContract.FarmaciasEntity.ADDRESS,
                DbContract.FarmaciasEntity.LOCALITY,
                DbContract.FarmaciasEntity.PROVINCE
        };
        MatrixCursor cursor = new MatrixCursor(columnNames, pharmacyList.size());
        MatrixCursor.RowBuilder builder;
        for (Pharmacy ph : pharmacyList) {
            builder = cursor.newRow();
            builder.add(ph.get_id());
            builder.add(ph.getName());
            builder.add(ph.getAddress());
            builder.add(ph.getLocality());
            builder.add(ph.getProvince());
        }
        return cursor;
    }

    @Override
    public void showNoResults() {

      //  mEmptyView.setVisibility(View.VISIBLE);
      //  mSuggestionsAdapter.swapCursor(null);
            mAdapter.swapData(null);

    }

    @Override
    public void showResultsQuickSearch(List<SuggestionsBean> list) {
        mQuickSearchAdapter.swapData(list);
    }

    @Override
    public void showNoResultsQuickSearch() {
        List<SuggestionsBean> voidList = new ArrayList<>();
        mQuickSearchAdapter.swapData(voidList);
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
    public void hideQuickSearchRecyclerView() {
        mQuickSearchRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onClickSuggestions(String text) {
        hideSoftKeyBoard();
        mSearchText.setText(text);
        clearFocusFromEditSearch();
        mPresenter.onRestartLoader(text);


    }

    private void clearFocusFromEditSearch(){
        mSearchText.clearFocus();
        //Note: When a View clears focus the framework is trying to give focus to the first focusable View from the top. Hence, if this View is the first from the top that can take focus, then all callbacks related to clearing focus will be invoked after which the framework will give focus to this view.
        //the solution is make another element focusable and request its focus, in this case I chose mRecyclerview
        mRecyclerView.requestFocus();
    }
    private void hideSoftKeyBoard(){

        View view=getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onDestroyView() {
        Util.logD(LOG_TAG, "onDestroyView");
        //recordar aqui hay que nulificar los elementos,creo
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Util.logD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


}
