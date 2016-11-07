package com.chernandezgil.farmacias.data.source.local;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Carlos on 09/07/2016.
 */
public class DbContract {
    public static final String SCHEME ="content://";
    public static final String AUTHORITY = "com.chernandezgil.farmacias";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final String PATH_FARMACIAS="farmacias";
    public static final String PATH_QUICK_SEARCH ="quick_search";
    public static final String PATH_FAVORITES= "favorites";

    public static final class FarmaciasEntity implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FARMACIAS).build();
        public static final Uri CONTENT_URI_QUICK_SEARCH =BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUICK_SEARCH).build();
        public static final Uri CONTENT_URI_PATH_FAVORITES =BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_FARMACIAS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_FARMACIAS;

        public static final String TABLE_NAME="farmacias";
        public static final String NAME="name";
        public static final String ADDRESS="address";
        public static final String LOCALITY="locality";
        public static final String PROVINCE="province";
        public static final String POSTAL_CODE="postal_code";
        public static final String PHONE="phone";
        public static final String LAT="lat";
        public static final String LON="lon";
        public static final String HOURS="hours";
        public static final String FAVORITE="favorite";

        public static Uri buildFarmaciasUriByPhone(long id){

            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildFarmaciasUriByPhone(String phone) {
            return CONTENT_URI.buildUpon().appendPath(phone).build();
        }
//        public static Uri buildFarmaciasUriByName(String name) {
//            return CONTENT_URI.buildUpon().appendPath(name).build();
//        }
        public static Uri buildFarmaciasUriByNameQuickSearch(String name) {
            return CONTENT_URI_QUICK_SEARCH.buildUpon().appendPath(name).build();
        }
        public static Uri buildFavoritesUriByPhone(String phone) {
            return CONTENT_URI_PATH_FAVORITES.buildUpon().appendPath(phone).build();
        }

    }

}
