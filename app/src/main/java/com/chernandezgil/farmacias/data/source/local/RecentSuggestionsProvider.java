package com.chernandezgil.farmacias.data.source.local;

import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;

/**
 * Created by Carlos on 07/09/2016.
 */
public class RecentSuggestionsProvider extends android.content.SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.chernandezgil.farmacias.data.source.local.RecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;


    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY,MODE);
    }

//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Cursor c= super.query(uri, projection, selection, selectionArgs, sortOrder);
//
//        return c;
//    }


}
