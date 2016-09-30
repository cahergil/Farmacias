package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carlos on 11/08/2016.
 */
public class PreferencesManagerImp implements PreferencesManager {
    private static final String FAVORITE_LIST_KEY ="favorite_list_key";
    private final SharedPreferences mPrefs;
    private final SharedPreferences.Editor mEditor;
    private final Context context;

    private static final String LOCATION_KEY = "location_key";


    public PreferencesManagerImp(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
        this.context = context;
    }



    @Override
    public SharedPreferences getSharedPreferences() {
        return mPrefs;
    }

    @Override
    public String getLocationKey() {
        return LOCATION_KEY;
    }

    @Override
    public int retrieveRadioBusquedaFromSp() {
        return mPrefs.getInt("seekbar_key", 4);

    }

    @Override
    public boolean isFirstExecution() {
        return mPrefs.getBoolean("first_execution_key", true);

    }

    @Override
    public void setFirstExecutionFalse() {

        mEditor.putBoolean("first_execution_key", false);
        mEditor.apply();
    }

    public int getCurrentItemTabLayout() {

        return mPrefs.getInt("current_item_key", 0);

    }

    public void setCurrentItemTabLayout(int currentItemTabLayout) {

        mEditor.putInt("current_item_key", currentItemTabLayout);
        mEditor.apply();
    }

    @Override
    public void saveLocation(Location location) {
        //http://stackoverflow.com/questions/3604849/where-to-save-android-gps-latitude-longitude-points
//        mEditor.putLong(LAT_KEY, Double.doubleToLongBits(location.getLatitude()));
//        mEditor.putLong(LON_KEY,Double.doubleToLongBits(location.getLongitude()));
        String locationObject = location.getLatitude() + Constants.SEMI_COLON + location.getLongitude();
        mEditor.putString(LOCATION_KEY, locationObject);
        mEditor.apply();
    }

    @Override
    public Location getLocation() {
        Location location;
        String locationObject = mPrefs.getString(LOCATION_KEY, "0:0");
        String [] coordinates = locationObject.split(":");
        double latitud = Double.parseDouble(coordinates[0]);
        double longitud = Double.parseDouble(coordinates[1]);
        location = new Location(Constants.EMPTY_STRING);
        location.setLatitude(latitud);
        location.setLongitude(longitud);
        return location;

    }

    @Override
    public int getRadio() {
        return mPrefs.getInt(context.getString(R.string.seek_bar_key),2);
    }

    @Override
    public void saveFavoriteList(List<Pharmacy> list) {
        if(list == null) return;

        mEditor.putString(FAVORITE_LIST_KEY,new Gson().toJson(list));
        mEditor.apply();

    }

    @Override
    public List<Pharmacy> getFavorites() {

        if(mPrefs.contains(FAVORITE_LIST_KEY)) {
            String jsonString = mPrefs.getString(FAVORITE_LIST_KEY, null);
            Gson gsonObject = new Gson();
            if (jsonString != null) {
                Pharmacy[] arrayFavorite = gsonObject.fromJson(jsonString, Pharmacy[].class);
                //the list returned by Arrays.asList is inmmutable, we need to create another list based on this one
                return new ArrayList<>(Arrays.asList(arrayFavorite));

            }
        }
        return new ArrayList<>();
    }
}
