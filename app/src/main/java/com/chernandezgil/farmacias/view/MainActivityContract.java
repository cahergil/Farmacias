package com.chernandezgil.farmacias.view;

/**
 * Created by Carlos on 01/08/2016.
 */
public interface MainActivityContract {

    interface View {

    }
    interface Presenter<V> {

        void setView(V view);
        void detachView();
        void onStart();


    }
}
