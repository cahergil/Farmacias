package com.chernandezgil.farmacias.view;

/**
 * Created by Carlos on 01/08/2016.
 */
public interface MainActivityContract {

    public interface View {

    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();
        void onStart();


    }
}
