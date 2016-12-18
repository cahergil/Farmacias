package com.chernandezgil.farmacias.Utilities;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bettervectordrawable.utils.BitmapUtil;
import com.chernandezgil.farmacias.BuildConfig;
import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.customwidget.CustomSupporMapFragment;
import com.chernandezgil.farmacias.data.source.local.DbContract;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Carlos on 06/07/2016.
 */
public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    static int screenHeight = 0;
    static int oldStatusBarFlags;

    public static void logD(final String tag, String message) {
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

    public static CustomSupporMapFragment handleMapFragmentRecreation(FragmentManager fragmentManager, int fragmentId,
                                                                      String fragmentTag) {
        CustomSupporMapFragment mapFragment = (CustomSupporMapFragment) fragmentManager.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new CustomSupporMapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, mapFragment, fragmentTag)
                    .commit();
            fragmentManager.executePendingTransactions();

        }
        return mapFragment;
    }

    //  address, postalCode City, Province
    public static String formatAddress(String address, String postalCode, String city, String province) {

        return address
                + Constants.COMMA + Constants.SPACE + postalCode + Constants.SPACE + city
                + Constants.COMMA + Constants.SPACE + province;
    }

    public static String formatPhoneNumber(String phoneNumber) {

        return phoneNumber.substring(0, 3) + Constants.SPACE + phoneNumber.substring(3, 5)
                + Constants.SPACE + phoneNumber.substring(5, 7)
                + Constants.SPACE + phoneNumber.substring(7, 9);


    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

    public static boolean is24HoursPharmacy(String hours) {

        if (hours == null) return false;
        return hours.equals("24H");
    }

    public static boolean isPharmacyOpen(String hours) {
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

    public static Intent startPhoneIntent(Context context, String telephone) {
        String uri = "tel:" + telephone;
        ;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        return intent;
    }

    public static Intent getShareIntent(String name, double dist, String aformatted, String tel) {
        Context context = MyApplication.getContext();
        final String nombre = "Farmacia:";
        final String distancia = "distancia:";
        final String direccion = "direccion:";
        final String telefono = "tef:";

        String textToShare = nombre + name + Constants.CR
                + distancia + context.getString(R.string.format_distance, dist) + Constants.CR
                + direccion + aformatted + Constants.CR
                + telefono + tel;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        return sendIntent;

    }

    public static Intent getGoodleDirectionsIntent(LatLng sourceLatLng,
                                                   String sourceAddress,
                                                   LatLng destinationLatLng,
                                                   String destinationAddress) {

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                sourceLatLng.latitude, sourceLatLng.longitude, sourceAddress,
                destinationLatLng.latitude, destinationLatLng.longitude, destinationAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        return intent;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, @DrawableRes int drawableResId) {


        Drawable drawable = VectorDrawableCompat.create(context.getResources(), drawableResId, null);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Bitmap bitmap = BitmapUtil.toBitmap(drawable, metrics, 48f, 0);
        return bitmap;
    }

    public static Bitmap createScaledBitMapFromVectorDrawable(Context context, VectorDrawableCompat vd, float dimension) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return BitmapUtil.toBitmap(vd, metrics, dimension, 0);


    }

    public static int getDefaultBitmapTextSize() {

        Context context = MyApplication.getContext();
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (14 * scale);
    }


    public static int getColor(@ColorRes int resId) {

        int color = ContextCompat.getColor(MyApplication.getContext(), resId);
        return color;
    }

    public static boolean isEmptyRequest(String[] selectionArgs) {
        if (selectionArgs[0].replaceAll("\\%", "").equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Called on dismiss in favorite fragment
     *
     * @param phone
     * @return
     */
    public static int changeFavoriteInDb(String phone) {
        Context context = MyApplication.getContext();
        Uri uri = DbContract.FarmaciasEntity.buildFavoritesUriByPhone(phone);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FarmaciasEntity.FAVORITE, 0);
        int rowsUpdated = context.getContentResolver().update(uri, contentValues,
                DbContract.FarmaciasEntity.PHONE + " LIKE '%" + phone + "%'",
                null);
        if (rowsUpdated == 1) {
            Utils.logD("changeFavoriteinDb", "rows updates: " + rowsUpdated);

        }
        return rowsUpdated;
    }

    public static String changeFavoriteInDb(boolean oldFavoriteValue, String phone) {
        String snackMessage;
        Context context = MyApplication.getContext();
        if (oldFavoriteValue) {
            snackMessage = context.getString(R.string.ltf_removed_from_favorite);

        } else {
            snackMessage = context.getString(R.string.ltf_added_to_favorite);

        }
        final boolean newFavoriteValue = !oldFavoriteValue;
        Uri uri = DbContract.FarmaciasEntity.buildFarmaciasUriByPhone(phone);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FarmaciasEntity.FAVORITE, newFavoriteValue);

//        int rowsUpdated = context.getContentResolver().update(uri, contentValues,
//                DbContract.FarmaciasEntity.PHONE + " LIKE '%" + phone + "%'",
//                null);

        int rowsUpdated = context.getContentResolver().update(uri, contentValues,
                DbContract.FarmaciasEntity.PHONE + " LIKE ? ",
                new String[]{"%" + phone + "%"});

        if (rowsUpdated == 1) {
            Utils.logD("changeFavoriteinDb", "rows updates: " + rowsUpdated);
            return snackMessage;
        }
        return null;


    }

    public static int getScreenHeight(Context c) {

        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

     public static void setLightStatusBar(View view, Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            oldStatusBarFlags = decor.getSystemUiVisibility();
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }


    }

    //the view used in setting the light status bar needs to dissapear so that the changes takes effect.
    public static void clearLightStatusBar(Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            decor.setSystemUiVisibility(oldStatusBarFlags);
            Window window = activity.getWindow();
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));


        }
    }

    //works for changing only the colors of the icons in status bar, the view doesn't need to dissapear from screen
    public static void clearNick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    //extracted from materials-intro, not working as expected
    public static void anotherMethod(Activity activity, View someView) {
//        int systemUiVisibility =activity.getWindow().getDecorView().getSystemUiVisibility();
//        int flagLightStatusBar = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//        if (flag) {
//            //Light background
//            systemUiVisibility |= flagLightStatusBar;
//        } else {
//            //Dark background
//            systemUiVisibility &= ~flagLightStatusBar;
//        }
//        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        //  someView.setSystemUiVisibility(0); not working
        //  rootView.setSystemUiVisibility(rootView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); not working
//        View decorView = activity.getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        decorView.setSystemUiVisibility(uiOptions);
//        Window window = activity.getWindow();
//        window.setStatusBarColor(ContextCompat.getColor(activity,R.color.colorPrimaryDark));
//
//// Calling setSystemUiVisibility() with a value of 0 clears
//// all flags.
//        decorView.setSystemUiVisibility(0);


    }


    public static boolean contains(LatLng location, ArrayList<LatLng> polyLoc) {
        if (location == null)
            return false;

        LatLng lastPoint = polyLoc.get(polyLoc.size() - 1);
        boolean isInside = false;
        double x = location.longitude;

        for (LatLng point : polyLoc) {
            double x1 = lastPoint.longitude;
            double x2 = point.longitude;
            double dx = x2 - x1;

            if (Math.abs(dx) > 180.0) {
                // we have, most likely, just jumped the dateline (could do further validation to this effect if needed).  normalise the numbers.
                if (x > 0) {
                    while (x1 < 0)
                        x1 += 360;
                    while (x2 < 0)
                        x2 += 360;
                } else {
                    while (x1 > 0)
                        x1 -= 360;
                    while (x2 > 0)
                        x2 -= 360;
                }
                dx = x2 - x1;
            }

            if ((x1 <= x && x2 > x) || (x1 >= x && x2 < x)) {
                double grad = (point.latitude - lastPoint.latitude) / dx;
                double intersectAtLat = lastPoint.latitude + ((x - x1) * grad);

                if (intersectAtLat > location.latitude)
                    isInside = !isInside;
            }
            lastPoint = point;
        }

        return isInside;
    }


    public static Drawable getDrawable(@DrawableRes int drawableResId) {
        Drawable d = ContextCompat.getDrawable(MyApplication.getContext(), drawableResId);
        return d;
    }

    public static float getDimension(@DimenRes int dimensionResId) {
        return MyApplication.getContext().getResources().getDimension(dimensionResId);
    }


    public static int getStatusPharmacyColor(boolean isOpen) {
        Context context = MyApplication.getContext();
        return isOpen ? ContextCompat.getColor(context, R.color.pharmacy_open) :
                ContextCompat.getColor(context, R.color.pharmacy_close);
    }

}
