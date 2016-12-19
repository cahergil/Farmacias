package com.chernandezgil.farmacias.view;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;

import java.util.List;

/**
 * Created by Carlos on 05/09/2016.
 */

public interface FindContract {
    interface View {
        void showResults(List<Pharmacy> pharmacyList);
        void showNoResults();
        void showResultsQuickSearch(List<SuggestionsBean> list);
        void showNoResultsQuickSearch();
        void hideEmptyView();
        void showEmptyView();
        void showLoading();
        void hideLoading();
        void hideQuickSearchRecyclerView();
        void launchActivity(Intent intent);
        void showSnackBar(String message);
        void showOpeningHours(int layout);


    }
    interface Presenter<V> {

        void setView(V view);
        void detachView();
        void onInitLoader();
        void onRestartLoader(String newText);
        void onInitLoaderQuickSearch();
        void onRestartLoaderQuickSearch(String text);
        void setLocation(Location currentLocation);
        void onClickGo(Pharmacy pharmacy,Location currentLocation);
        void onClickFavorite(Pharmacy pharmacy);
        void onClickPhone(String phone);
        void onClickShare(Pharmacy pharmacy);
        void onClickOpeningHours(String hour);


    }
}
