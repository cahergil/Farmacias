package com.chernandezgil.farmacias.ui.adapter;

/**
 * Created by Carlos on 11/08/2016.
 */
public interface PreferencesManager {


    int retrieveRadioBusquedaFromSp();
    boolean isFirstExecution();
    void setFirstExecutionFalse();
}
