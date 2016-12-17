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

    public SharedPreferences getSharedPreferences();
    public String getLocationKey();
    int retrieveRadioBusquedaFromSp();
    boolean isFirstExecution();
    void setFirstExecutionFalse();
    public int getCurrentItemTabLayout();
    public void setCurrentItemTabLayout(int currentItemTabLayout);
    public void saveLocation(Location location);
    public Location getLocation();
    public int getRadio();
    public void saveFavoriteList(List<Pharmacy> list);
    public List<Pharmacy> getFavorites();
    public void saveColorMap(HashMap<String,Integer> colorMap);
    public HashMap<String,Integer> getColorMap();
    public void saveStreet(String street);
    public String getStreet();

}
