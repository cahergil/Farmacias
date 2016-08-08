package com.chernandezgil.farmacias.Utilities;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;

import com.chernandezgil.farmacias.BuildConfig;
import com.chernandezgil.farmacias.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Carlos on 06/07/2016.
 */
public class Util {

    public static void LOGD(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    // http://stackoverflow.com/questions/8710719/generating-an-alphabetic-sequence-in-java
    public static String characterFromInteger(int i) {
        return i < 0 ? "" : characterFromInteger((i / 26) - 1) + (char) (65 + i % 26);
    }

    public void navigateTo() {

    }

    public static SupportMapFragment handleMapFragmentRecreation(FragmentManager fragmentManager, int fragmentId,
                                                                 String fragmentTag) {
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, mapFragment, fragmentTag)
                    .commit();
            fragmentManager.executePendingTransactions();

        }
        return mapFragment;
    }

    //  address, postalCode City, Province
    public static String formatAddress(String address, String postalCode, String city, String province) {

        return  address
                + Constants.COMMA + Constants.SPACE + postalCode + Constants.SPACE + city
                + Constants.COMMA + Constants.SPACE + province;
    }

    public static String formatPhoneNumber(String phoneNumber) {

        return phoneNumber.substring(0,3) + Constants.SPACE + phoneNumber.substring(3,5)
                                               + Constants.SPACE + phoneNumber.substring(5,7)
                                               + Constants.SPACE + phoneNumber.substring(7,9);


    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public static Calendar buildCalendar(Date date, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    public static  boolean isPharmacyOpen(String hours) {
        Date now = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(now);
        int day = date.get(Calendar.DAY_OF_WEEK);
        if (hours.equals("24H")) {
            return true;
        } else if (Calendar.SATURDAY == day) {
            Calendar morningOpen = buildCalendar(now, 9, 0);
            Calendar morningClose = buildCalendar(now, 14, 0);
            if (date.after(morningOpen) && date.before(morningClose)) {
                return true;
            }
            return false;

        } else if (Calendar.SUNDAY == day) {
            return false;
        } else {
            Calendar morningOpen = buildCalendar(now, 9, 0);
            Calendar morningClose = buildCalendar(now, 14, 0);
            Calendar eveningOpen = buildCalendar(now, 18, 30);
            Calendar eveningClose = buildCalendar(now, 20, 30);
            if ((date.after(morningOpen) && date.before(morningClose))
                    || (date.after(eveningOpen) && date.before(eveningClose))) {
                return true;
            }
            return false;

        }

    }

    public static void startGoogleDirections(Context context, LatLng sourceLatLng,
                                             String sourceAddress,
                                             LatLng destinationLatLng,
                                             String destinationAddress){
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                sourceLatLng.latitude,sourceLatLng.longitude , sourceAddress,
                destinationLatLng.latitude, destinationLatLng.longitude,destinationAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        context.startActivity(intent);
    }

    }
