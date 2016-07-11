package com.chernandezgil.farmacias.Utilities;

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
   // http://stackoverflow.com/questions/8710719/generating-an-alphabetic-sequence-in-java
    public static String characterFromInteger(int i) {
        return i < 0 ? "" : characterFromInteger((i / 26) - 1) + (char)(65 + i % 26);
    }
}
