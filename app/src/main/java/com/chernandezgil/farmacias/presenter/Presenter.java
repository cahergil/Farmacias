package com.chernandezgil.farmacias.presenter;

/**
 * Created by Carlos on 01/08/2016.
 */
public interface Presenter<V> {


    void setView(V view);

    void detachView();


}