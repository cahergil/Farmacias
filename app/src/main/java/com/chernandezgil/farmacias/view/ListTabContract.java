package com.chernandezgil.farmacias.view;

import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.List;

/**
 * Created by Carlos on 08/08/2016.
 */
public interface ListTabContract {

    public interface View {
        public void showResults(List<Pharmacy> pharmacyList);
        public void showNoResults();
        public void showLoading();
        public void hideLoading();
        public void setAddress(String address);
    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onGetAddressFromLocation(Location location);




    }
}
