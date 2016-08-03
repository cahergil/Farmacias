package com.chernandezgil.farmacias.Utilities;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.chernandezgil.farmacias.BuildConfig;
import com.chernandezgil.farmacias.R;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Carlos on 06/07/2016.
 */
public class Util {

    public static void LOGD(final String tag,String message) {
        if(BuildConfig.DEBUG) {
            Log.d(tag,message);
        }
    }
   // http://stackoverflow.com/questions/8710719/generating-an-alphabetic-sequence-in-java
    public static String characterFromInteger(int i) {
        return i < 0 ? "" : characterFromInteger((i / 26) - 1) + (char)(65 + i % 26);
    }

    public void navigateTo(){

    }
    public static SupportMapFragment handleMapFragmentRecreation(FragmentManager fragmentManager, int fragmentId,
                                                                 String fragmentTag){
        SupportMapFragment mapFragment=(SupportMapFragment) fragmentManager.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            fragmentManager.beginTransaction()
                .add(R.id.mapFragmentContainer,mapFragment,fragmentTag)
                .commit();
            fragmentManager.executePendingTransactions();

        }
        return mapFragment;
    }
}
