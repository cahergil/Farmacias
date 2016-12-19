package com.chernandezgil.farmacias.ui.adapter;

import android.content.SharedPreferences;
import android.location.Location;

import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 11/08/2016.
 */
public interface PreferencesManager {

    SharedPreferences getSharedPreferences();
    String getLocationKey();
    int retrieveRadioBusquedaFromSp();
    boolean isFirstExecution();
    void setFirstExecutionFalse();
    int getCurrentItemTabLayout();
    void setCurrentItemTabLayout(int currentItemTabLayout);
    void saveLocation(Location location);
    Location getLocation();
    int getRadio();
    void saveFavoriteList(List<Pharmacy> list);
    List<Pharmacy> getFavorites();
    void saveColorMap(HashMap<String,Integer> colorMap);
    HashMap<String,Integer> getColorMap();
    void saveStreet(String street);
    String getStreet();

}
