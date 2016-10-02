package com.chernandezgil.farmacias.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.SearchUtils;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.data.LoaderProvider;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.data.source.local.RecentSuggestionsProvider;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;
import com.chernandezgil.farmacias.presenter.FindPresenter;
import com.chernandezgil.farmacias.ui.adapter.item_animator.CustomItemAnimator;
import com.chernandezgil.farmacias.ui.adapter.FindQuickSearchAdapter;
import com.chernandezgil.farmacias.ui.adapter.FindRecyclerViewAdapter;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManagerImp;
import com.chernandezgil.farmacias.view.FindContract;
import com.jakewharton.rxbinding.widget.RxTextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by Carlos on 10/07/2016.
 */
public class FindFragment extends Fragment implements FindContract.View,
        FindQuickSearchAdapter.OnClickHandler,FindRecyclerViewAdapter.OnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener{


    private static final String LOG_TAG = FindFragment.class.getSimpleName();

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;
    @BindView(R.id.frame)
    FrameLayout mRootLayout;

    //Activity UI elements
    private RecyclerView mQuickSearchRecyclerView;
    private EditText mSearchEditor;
    private ImageView mClearSearch;
    private CardView mSearchCardView;
    private ImageView mImageSearchBack;

    private FindPresenter mPresenter;
    private Unbinder unbinder;
    private FindRecyclerViewAdapter mAdapter;
    private SearchRecentSuggestions mRecentSearchSuggestions;
    private FindQuickSearchAdapter mFindQuickSearchAdapter;
    private RelativeLayout mViewSearch;
    private Location mLocation;
    private boolean mRotation;
    private boolean mCardOnScreen;
    private CompositeSubscription mCompositeSubscription;
    private PreferencesManager mSharedPreferences;
    private String mQuickSearchText;
    private Drawable mDimDrawable;
    private boolean isBackPressed=false; //half implemented functionality, only works on portrait


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.logD(LOG_TAG, "onCreate");

        mSharedPreferences = new PreferencesManagerImp(getActivity().getApplicationContext());
        mLocation = mSharedPreferences.getLocation();
        if (savedInstanceState != null) {
            mRotation = true;
        }
        LoaderProvider loaderProvider = new LoaderProvider(getContext());
        LoaderManager loaderManager = getLoaderManager();
        Geocoder geocoder = new Geocoder(getActivity());
        // loaderManager.enableDebugLogging(true);
        mPresenter = new FindPresenter(mLocation, loaderManager, loaderProvider,geocoder);

        setHasOptionsMenu(true);
        mRecentSearchSuggestions = new SearchRecentSuggestions(getContext(),
                RecentSuggestionsProvider.AUTHORITY, RecentSuggestionsProvider.MODE);
        mCompositeSubscription = new CompositeSubscription();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_find_favorite, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUpRecyclerView();
        setDimDrawable();
        mPresenter.setView(this);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Utils.logD(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAppBarElevation(R.dimen.appbar_elevation);
        }
        initializeSearchUiWidgets();
        if (savedInstanceState == null) {
            //LoaderManager retains the cursor of the last result
            mPresenter.onInitLoader();
        } else {

            mCardOnScreen = savedInstanceState.getBoolean("card_on_screen_key");
            String searchText = savedInstanceState.getString("last_search_editor_key");
            if (mCardOnScreen) {
                int quickSearchRecyclerViewVisibility=savedInstanceState.getInt("quickSearchRecyclerViewState");
                mPresenter.onInitLoader();
                mSearchCardView.setVisibility(View.VISIBLE);
              //  int options = mSearchEditor.getImeOptions();
             //   mSearchEditor.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                mSearchEditor.setImeOptions(mSearchEditor.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                        EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
                //if quickSearchRecyclerView was visible before rotation, that means that the user
                //was searching-> restore state of searching
                if (quickSearchRecyclerViewVisibility == View.VISIBLE) {
                    Utils.setLightStatusBar(mSearchCardView,getActivity());
                    mQuickSearchRecyclerView.setVisibility(View.VISIBLE);
                    //don't know why always get the mSearchEditor.getText()=""
                    //although in the view appears the characters.
                    //for that reason save the last search string
                    mFindQuickSearchAdapter.setmSearchString(searchText);
                    if(searchText.length()>0) {
                        showClearSearchIcon();
                    }
                    //LoaderManager retains the state of the cursor of the last search
                    mPresenter.onInitLoaderQuickSearch();

                }

                //mSearchEditor.setText(searchText);

                //falta por ver el foco; si al poner el searcheditor el loader del quick search se activa
                // y el teclado
            } else { //restore only the state of the recyclerview results
                mPresenter.onInitLoader();

            }


        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_find, menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
               // isBackPressed = false;
                mFindQuickSearchAdapter.setmSearchString(Constants.EMPTY_STRING);
                //with onInitLoaderQuickSearch if the user introduces a search string with cero results,
                //and after closes the search. When opening again the Quick searach rv is going to show
                //the previous results. This way always present the recent searches
                mPresenter.onRestartLoaderQuickSearch("");
                initializeSearchCardView();
                Utils.setLightStatusBar(mSearchCardView,getActivity());

                return true;


            case R.id.action_filter:
                Toast.makeText(getContext(), "onclick filter", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location_key", mLocation);
        outState.putString("last_search_editor_key", mSearchEditor.getText().toString());
        outState.putBoolean("card_on_screen_key", mCardOnScreen);
        outState.putInt("quickSearchRecyclerViewState", mQuickSearchRecyclerView.getVisibility());
        //0 visible; 8 gone; 4 invisible

    }

    @Override
    public void onResume() {
        Utils.logD(LOG_TAG, "onResume");
        super.onResume();
        mSharedPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        Utils.logD(LOG_TAG, "onStop");
        mSharedPreferences.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private void initializeSearchCardView() {
        SearchUtils.setUpAnimations(getActivity(), mSearchCardView, mViewSearch, mQuickSearchRecyclerView);
        //if we haben removed the focus before this is necessary. If it is the first click not.
        requestFocusOnSearchEditor();
        mCardOnScreen = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setAppBarElevation(int elevation) {
        AppBarLayout appBarLayout= (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        if(elevation!=0) {
            appBarLayout.setElevation(getResources().getDimension(R.dimen.appbar_elevation));
        } else {
            appBarLayout.setElevation(0);
        }
    }
    private void initializeSearchUiWidgets() {

        setUpQuickSearchRecyclerView();

        mSearchEditor = (EditText) getActivity().findViewById(R.id.edit_text_search);
        mSearchEditor.setImeOptions(mSearchEditor.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        mSearchEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Utils.logD(LOG_TAG, "edit_text has focus");
                    mRecyclerView.setVisibility(View.GONE);
                    showQuickSearchRecyclerView();
                    dimScreen();

                } else {

                    Utils.logD(LOG_TAG, "edit_text lost focus");
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //napa, backpressed funciona cuando no esta rotada la pantalla, no se porque, da un OOM exception
//                    if(!mRotation) {
//                        if (isBackPressed) {
//                            isBackPressed = false;
//                        } else {
//                            hideQuickSearchRecyclerView();
//                        }
//                    } else {
//                        hideQuickSearchRecyclerView();
//                    }
                    hideQuickSearchRecyclerView();
                    unDimScren();
                }
            }
        });
        mClearSearch = (ImageView) getActivity().findViewById(R.id.clearSearch);
        mSearchCardView = (CardView) getActivity().findViewById(R.id.card_search);
        mViewSearch = (RelativeLayout) getActivity().findViewById(R.id.view_search);
        mImageSearchBack = (ImageView) getActivity().findViewById(R.id.image_search_back);
        mSearchEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              //  String text = mSearchEditor.getText().toString();
                  String text =  editable.toString();

                if (mQuickSearchText != null && mQuickSearchText.equals(text)) {
                    //after having selected a suggestion:
                    //set text of suggestions in SearchEditor -> gets the focus again
                    //use get the data again to be ready for the next time we click on searchEditor
                    startQuickSearch(text,false);
                    return;
                }
                if (!mCardOnScreen) return;
                if (mRotation) {
                    //after rotation there is no need to make again a search, since we have already the
                    //cursor data through the LoaderManager
                    mRotation = false;
                    return;
                }

                startQuickSearch(text,true);
            }
        });
        //https://kotlin.link/articles/RxAndroid-and-Kotlin-Part-1.html
//        Subscription editorAterTextChangeEvent = RxTextView.afterTextChangeEvents(mSearchEditor)
//                .debounce(100, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(event->{
//                    startQuickSearch(event.view().getText().toString());
//                });

        Subscription editorActionEvent = RxTextView.editorActionEvents(mSearchEditor)
                .subscribe(event -> {
                    if (event.actionId() == EditorInfo.IME_ACTION_SEARCH) {
                        onClickImeSearchIcon(event.view().getText().toString());
                    }
                });

        //    mCompositeSubscription.add(editorAterTextChangeEvent);
        mCompositeSubscription.add(editorActionEvent);


        mImageSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackPressed = true;
                SearchUtils.setUpAnimations(getActivity(), mSearchCardView, mViewSearch, mQuickSearchRecyclerView);
                //put this variable here, so that clearSearchEditor() doesn't execute another search
                mCardOnScreen = false;
                //delete current text so that in the next appearance don't show
                clearSearchEditor();
                clearFocusFromSearchEditor();



            }
        });
        mClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSearchEditor();
                requestFocusOnSearchEditor();
            }
        });

    }

    private void setDimDrawable(){
        mDimDrawable =ContextCompat.getDrawable(getContext(),R.drawable.dim_drawable);
    }
    private void dimScreen(){
        mRootLayout.setForeground(mDimDrawable);
        mRootLayout.getForeground().setAlpha(180);
    }
    private void unDimScren() {
        //this way the vertical separators in locality, adress etc are shown
        mRootLayout.setForeground(null);
        //mRootLayout.getForeground().setAlpha(0);
    }
    private void showClearSearchIcon() {
        mClearSearch.setVisibility(View.VISIBLE);
    }
    private void hideClearSearchIcon() {
        mClearSearch.setVisibility(View.INVISIBLE);
    }
    private void startQuickSearch(String s,boolean flagShowQuickSearchRecyclerView) {
        Utils.logD(LOG_TAG, "startQuickSearch");

        if(flagShowQuickSearchRecyclerView) {
            showQuickSearchRecyclerView();

        }
        mPresenter.onRestartLoaderQuickSearch(s);
        mFindQuickSearchAdapter.setmSearchString(s);
        if (s.length() > 0) {
            showClearSearchIcon();
        } else {
            hideClearSearchIcon();
        }

    }

    private void requestFocusOnSearchEditor() {
        mSearchEditor.requestFocus();
    }

    private void clearSearchEditor() {

        mSearchEditor.getText().clear();
    }



    private void showQuickSearchRecyclerView() {
        mQuickSearchRecyclerView.setVisibility(View.VISIBLE);

    }

    private void hideSearchCardView() {

        mSearchCardView.setVisibility(View.INVISIBLE);
    }

    private void setUpQuickSearchRecyclerView() {

        mQuickSearchRecyclerView = (RecyclerView) getActivity().findViewById(R.id.rv);
        mFindQuickSearchAdapter = new FindQuickSearchAdapter(getContext(), this);
        mQuickSearchRecyclerView.setAdapter(mFindQuickSearchAdapter);
        mQuickSearchRecyclerView.setHasFixedSize(true);
        mQuickSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    private void setUpRecyclerView() {

        CustomItemAnimator customItemAnimator = new CustomItemAnimator();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mAdapter = new FindRecyclerViewAdapter(getContext(),mRecyclerView, customItemAnimator, this);
        mRecyclerView.setItemAnimator(customItemAnimator);
    //    SlideInBottomAnimatorAdapter slideAdapter = new SlideInBottomAnimatorAdapter(mAdapter,mRecyclerView);


        mRecyclerView.setAdapter(mAdapter);
       // mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        Utils.logD(LOG_TAG,"prueba");

    }

    @Override
    public void showResults(List<Pharmacy> pharmacyList) {
        Utils.logD(LOG_TAG, "showResults");
        mAdapter.swapData(pharmacyList);


    }

    @Override
    public void showNoResults() {

        mAdapter.swapData(null);

    }

    @Override
    public void showResultsQuickSearch(List<SuggestionsBean> list) {
        Utils.logD(LOG_TAG,"showResultsQuickSearch");

        mFindQuickSearchAdapter.swapData(list);
    }

    @Override
    public void showNoResultsQuickSearch() {
        List<SuggestionsBean> voidList = new ArrayList<>();
        mFindQuickSearchAdapter.swapData(voidList);
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


    private void onClickImeSearchIcon(String text) {
        onClickSuggestions(text);
    }

    /**
     * Initialize a search for the recyclerview and a search for the QuickSearchRecyclerview( just after
     * mSearchEditor.setText(text)), that way the QuickSearchRecyclerview is always updated
     * @param text
     */
    @Override
    public void onClickSuggestions(String text) {
        mQuickSearchText = text;
        mRecentSearchSuggestions.saveRecentQuery(text, null);
        hideSoftKeyBoard();
        hideQuickSearchRecyclerView();
        mSearchEditor.setText(text);
        clearFocusFromSearchEditor();
        mPresenter.onRestartLoader(text);


    }

    private void clearFocusFromSearchEditor() {
     //   mSearchEditor.clearFocus();
        //Note: When a View clears focus the framework is trying to give focus to the first focusable View from the top. Hence, if this View is the first from the top that can take focus, then all callbacks related to clearing focus will be invoked after which the framework will give focus to this view.
        //the solution is make another element focusable and request its focus, in this case I chose mRecyclerview
    //    mRecyclerView.requestFocus();
        mRootLayout.requestFocus();
    }

    private void getSoftKeyboardState() {
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
    }

    private void hideSoftKeyBoard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void restoreToolbarActivityUiState() {
        hideSoftKeyBoard(); // task done when back in search editor
        hideSearchCardView(); // task done when back in search editor
        hideQuickSearchRecyclerView(); // task done when back in search editor
     //   clearSearchEditor();
     //   clearFocusFromSearchEditor();
    }

    @Override
    public void showSnackBar(String message) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
            }
        }, 30);
    }

    @Override
    public void onClickGo(Pharmacy pharmacy) {
        mPresenter.onClickGo(pharmacy,mLocation);
    }

    @Override
    public void onClickFavorite(Pharmacy pharmacy) {
        mPresenter.onClickFavorite(pharmacy);
    }

    @Override
    public void onClickPhone(String phone) {
        mPresenter.onClickPhone(phone);
    }

    @Override
    public void onClickShare(Pharmacy pharmacy) {
        mPresenter.onClickShare(pharmacy);
    }

    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mSharedPreferences.getLocationKey())) {
            //update location variable
            mLocation = mSharedPreferences.getLocation();
            //update location in presenter
            mPresenter.setLocation(mLocation);


        }
    }

    @Override
    public void onDestroyView() {
        Utils.logD(LOG_TAG, "onDestroyView");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAppBarElevation(0);
        }
        restoreToolbarActivityUiState();
        mCompositeSubscription.unsubscribe();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Utils.logD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


}
