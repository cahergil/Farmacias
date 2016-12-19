package com.chernandezgil.farmacias.view;

import android.content.Intent;
import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;

import java.util.List;

/**
 * Created by Carlos on 28/09/2016.
 */

public interface FavoriteContract {
    interface View {

        void showResults(List<Pharmacy> pharmacyList);
        void showNoResults();
        void showLoading();
        void hideLoading();
        void showOpeningHours(int layout);
        void launchActivity(Intent intent);
        void showSnackBar(String message);

    }

    interface Presenter<V> {

       void setView(V view);
       void detachView();
       void onInitLoader();
       void onRestartLoader();
       void onClickPhone(String phone);
       void onClickGo(Pharmacy pharmacy);
       void onClickShare(Pharmacy pharmacy);
       void onClickOpeningHours(String hour);

    }
}
