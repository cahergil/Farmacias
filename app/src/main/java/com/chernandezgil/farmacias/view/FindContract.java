package com.chernandezgil.farmacias.view;

import android.database.Cursor;
import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.model.SuggestionsBean;

import java.util.List;

/**
 * Created by Carlos on 05/09/2016.
 */

public interface FindContract {
    public interface View {
        public void showResults(List<Pharmacy> pharmacyList);
        public void showNoResults();
        public void showResultsQuickSearch(List<SuggestionsBean> list);
        public void showNoResultsQuickSearch();
        public void hideNoResults();;
        public void showLoading();
        public void hideLoading();
        public void hideQuickSearchRecyclerView();



    }
    public interface Presenter<V> {


        void setView(V view);

        void detachView();

        public void onInitLoader();
        public void onRestartLoader(String newText);
        public void onInitLoaderQuickSearch();
        public void onRestartLoaderQuickSearch(String text);



    }
}
