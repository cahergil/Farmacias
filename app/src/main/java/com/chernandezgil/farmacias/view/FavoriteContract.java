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
    public interface View {
        public void showResults(List<Pharmacy> pharmacyList);
        public void showNoResults();


        public void showLoading();
        public void hideLoading();
        public void showSchedule(boolean flag24h);
        public void launchActivity(Intent intent);
        public void showSnackBar(String message);




    }

    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onInitLoader();
        public void onRestartLoader();
        public void onClickPhone(String phone);
        public void onClickGo(Pharmacy pharmacy);
        public void onClickShare(Pharmacy pharmacy);
        public void onClickClock(String hour);

    }
}
