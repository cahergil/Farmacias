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

    interface View {
        void showResults(List<Pharmacy> pharmacyList);
        void showNoResults();
        void showLoading();
        void showSnackBar(String message);
        void hideLoading();
        void setAddress(String address);
        void launchActivity(Intent intent);
        void showOpeningHours(int layout,int titleBackgroundColor);

    }

    interface Presenter<V> {

        void setView(V view);
        void detachView();
        void onStartLoader();
        void onGetAddressFromLocation(Location currentLocation);
        void setLocation(Location currentLocation);
        void handleClickGo(Pharmacy pharmacy,Location currentLocation, String currentAddress);
        void handleClickCall(String phone);
        void handleClickShare(Pharmacy pharmacy);
        void handleClickFavorite(Pharmacy pharmacy);
        void onClickOpeningHours(Pharmacy pharmacy);






    }
}
