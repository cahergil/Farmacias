package com.chernandezgil.farmacias.presenter;

import com.chernandezgil.farmacias.view.MainMvpView;

/**
 * Created by Carlos on 01/08/2016.
 */
public class MainActivityPresenter implements Presenter<MainMvpView> {

    private MainMvpView mMainMvpView;


    public MainActivityPresenter(){

    }

    @Override
    public void setView(MainMvpView view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        mMainMvpView=view;

    }

    public void start(){

    }

    public void stop(){

    }
    @Override
    public void detachView() {

        mMainMvpView=null;

    }
}
