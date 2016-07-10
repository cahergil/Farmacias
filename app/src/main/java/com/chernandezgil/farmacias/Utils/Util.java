package com.chernandezgil.farmacias.Utils;

import android.util.Log;

import com.chernandezgil.farmacias.BuildConfig;

/**
 * Created by Carlos on 06/07/2016.
 */
public class Util {

    public static void LOGD(final String tag,String message) {
        if(BuildConfig.DEBUG) {
            Log.d(tag,message);
        }
    }
}
