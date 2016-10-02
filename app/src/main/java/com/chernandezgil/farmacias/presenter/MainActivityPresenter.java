package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.data.source.MainActivityInteractor;
import com.chernandezgil.farmacias.ui.adapter.PreferencesManager;
import com.chernandezgil.farmacias.view.MainActivityContract;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MainActivityPresenter implements MainActivityContract.Presenter<MainActivityContract.View> {

    private static final String LOG_TAG=MainActivityPresenter.class.getSimpleName();
    private MainActivityContract.View mMainActivityView;
    private MainActivityInteractor mMainActivityInteractor;
    private PreferencesManager mPreferencesManager;

    public MainActivityPresenter(PreferencesManager mPreferencesManager){
        this.mPreferencesManager = mPreferencesManager;

    }


    @Override
    public void setView(MainActivityContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mMainActivityView =view;
        mMainActivityInteractor=new MainActivityInteractor(mPreferencesManager);

    }


    @Override
    public void detachView() {

        mMainActivityView =null;

    }

    @Override
    public void onStart() {
        mMainActivityInteractor.loadData();
    }


}
