package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 11/08/2016.
 */
public class PreferencesManagerImp implements PreferencesManager {

    private final SharedPreferences mPrefs;
    private final SharedPreferences.Editor mEditor;
    private final Context context;
    private static final String FAVORITE_LIST_KEY ="favorite_list_key";
    private static final String COLOR_MAP_KEY ="color_map_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String STREET_KEY ="street_key";


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

            if (jsonString != null) {
                Gson gsonObject = new Gson();
                Pharmacy[] arrayFavorite = gsonObject.fromJson(jsonString, Pharmacy[].class);
                //the list returned by Arrays.asList is inmmutable, we need to create another list based on this one
                return new ArrayList<>(Arrays.asList(arrayFavorite));

            }
        }
        return new ArrayList<>();
    }

    @Override
    public void saveColorMap(HashMap<String, Integer> colorMap) {
        mEditor.putString(COLOR_MAP_KEY,new Gson().toJson(colorMap));
        mEditor.apply();

    }

    @Override
    public HashMap<String, Integer> getColorMap() {
        if(mPrefs.contains(COLOR_MAP_KEY)) {
            String jsonString = mPrefs.getString(COLOR_MAP_KEY, null);
            if(jsonString !=null) {
                java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
                return  new Gson().fromJson(jsonString,type);
            }
        }
        return null;
    }

    @Override
    public void saveStreet(String street) {
        mEditor.putString(STREET_KEY,street);
        mEditor.apply();
    }

    @Override
    public String getStreet() {
        return mPrefs.getString(STREET_KEY,Constants.EMPTY_STRING);
    }


}
