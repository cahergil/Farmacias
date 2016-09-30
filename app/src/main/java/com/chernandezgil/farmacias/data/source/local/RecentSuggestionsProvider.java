package com.chernandezgil.farmacias.data.source.local;

import android.app.SearchManager;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Carlos on 07/09/2016.
 */
public class RecentSuggestionsProvider extends android.content.SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.chernandezgil.farmacias.data.source.local.RecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;


    private UriMatcher matcher;
    private static final int SUGGESTIONS_CODE = 5;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
 //   public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath()

    public RecentSuggestionsProvider() {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS_CODE);


        setupSuggestions(AUTHORITY,MODE);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int code = matcher.match(uri);
        switch (code) {
            case SUGGESTIONS_CODE:
                if (selectionArgs == null || selectionArgs.length == 0 || selectionArgs[0].trim().length() == 0)
                {
                    return super.query(uri, projection, selection, selectionArgs, sortOrder);
                }
                else
                {
                    return super.query(uri, projection, selection, selectionArgs, sortOrder);


                }


            default: return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        //Cursor c= super.query(uri, projection, selection, selectionArgs, sortOrder);
        //return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return super.delete(uri, selection, selectionArgs);
    }
}
