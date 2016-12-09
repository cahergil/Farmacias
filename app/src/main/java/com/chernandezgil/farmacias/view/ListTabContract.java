package com.chernandezgil.farmacias.view;

import android.content.Intent;
import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.ui.adapter.ListTabAdapter;

import java.util.List;

/**
 * Created by Carlos on 08/08/2016.
 */
public interface ListTabContract {

    public interface View {
        public void showResults(List<Pharmacy> pharmacyList);
        public void showNoResults();
        public void showLoading();
        public void showSnackBar(String message);
        public void hideLoading();
        public void setAddress(String address);
        public void launchActivity(Intent intent);
        public void showOpeningHours(int layout);


    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onStartLoader();
        public void onGetAddressFromLocation(Location currentLocation);
        public void setLocation(Location currentLocation);
        public void handleClickGo(Pharmacy pharmacy,Location currentLocation, String currentAddress);
        public void handleClickCall(String phone);
        public void handleClickShare(Pharmacy pharmacy);
        public void handleClickFavorite(Pharmacy pharmacy);
        public void onClickOpeningHours(String hour);






    }
}
