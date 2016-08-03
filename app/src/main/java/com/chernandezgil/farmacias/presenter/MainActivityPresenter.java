package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.view.MainActivityContract;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MainActivityPresenter implements MainActivityContract.Presenter<MainActivityContract.View> {

    private MainActivityContract.View mMainActivityView;


    public MainActivityPresenter(){

    }


    public void start(){

    }

    public void stop(){

    }


    @Override
    public void setView(MainActivityContract.View view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mMainActivityView =view;
    }

    @Override
    public void detachView() {

        mMainActivityView =null;

    }
}
