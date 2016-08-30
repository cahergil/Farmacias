package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Carlos on 11/08/2016.
 */
public class AndroidPrefsManager implements PreferencesManager {
    private final SharedPreferences prefs;
    private final Context context;

    public AndroidPrefsManager(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }


    @Override
    public int retrieveRadioBusquedaFromSp() {
        return prefs.getInt("seekbar_key",4);

    }

    @Override
    public boolean isFirstExecution() {
        return prefs.getBoolean("first_execution_key",true);

    }

    @Override
    public void setFirstExecutionFalse() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("first_execution_key",false);
        editor.apply();
    }

    public int getCurrentItemTabLayout(){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("current_item_key",0);

    }

    public void setCurrentItemTabLayout(int currentItemTabLayout){
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt("current_item_key",currentItemTabLayout);
        editor.apply();
    }
}
