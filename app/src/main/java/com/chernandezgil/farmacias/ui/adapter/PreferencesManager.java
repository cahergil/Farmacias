package com.chernandezgil.farmacias.ui.adapter;

import android.content.SharedPreferences;
import android.location.Location;

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
}
