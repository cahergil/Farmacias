package com.chernandezgil.farmacias.data.source.local;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.adapter.QuickSearchAdapter;

/**
 * Created by Carlos on 09/07/2016.
 */

@SuppressWarnings("ConstantConditions")
//to delete lint warning getContext().getContentResolver() may produce npe...
public class DbProvider extends ContentProvider {

    private DbHelper mDbHelper;

    private static UriMatcher mUriMatcher = buildUriMatcher();

    private static final int FARMACIAS = 100;
    private static final int FARMACIAS_ID = 101;
    private static final int QUICK_SEARCH = 200;
    String[] suggestionsColumnNames = {DbContract.FarmaciasEntity._ID,
            DbContract.FarmaciasEntity.NAME
    };
    private static final int RECENT_SEARCH_ORIGIN = 0;
    private static final int DATABASE_SEARCH_ORIGIN = 1;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DbContract.PATH_FARMACIAS + "/*", FARMACIAS_ID);
        matcher.addURI(authority, DbContract.PATH_FARMACIAS, FARMACIAS);

        matcher.addURI(authority, DbContract.PATH_QUICK_SEARCH, QUICK_SEARCH);
        matcher.addURI(authority, DbContract.PATH_QUICK_SEARCH + "/*", QUICK_SEARCH);
        return matcher;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = mUriMatcher.match(uri);
        switch (match) {

            case FARMACIAS_ID:
                return DbContract.FarmaciasEntity.CONTENT_ITEM_TYPE;
            case FARMACIAS:
                return DbContract.FarmaciasEntity.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final int match = mUriMatcher.match(uri);
        switch (match) {

            case FARMACIAS:
                retCursor = mDbHelper.getReadableDatabase().query(
                        DbContract.FarmaciasEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FARMACIAS_ID:
                retCursor = mDbHelper.getReadableDatabase().query(
                        DbContract.FarmaciasEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case QUICK_SEARCH:
                Uri uri1 = RecentSuggestionsProvider.BASE_CONTENT_URI.buildUpon().appendPath(SearchManager.SUGGEST_URI_PATH_QUERY).build();
                //               retCursor = getContext().getContentResolver().query(uri1,new String[]{"query"},"query" + " like ?",
                //                       selectionArgs,sortOrder);
//                if (selectionArgs[0].replaceAll("\\%","").equals("")) {
//                    retCursor = getContext().getContentResolver().query(uri1, new String[]{"suggest_text_1"}, null,
//                            null, sortOrder);
//                } else {
//                    retCursor = getContext().getContentResolver().query(uri1, new String[]{"suggest_text_1"}, "suggest_text_1" + " like ?",
//                            selectionArgs, sortOrder);
//                }

                Cursor recentSearch = getContext().getContentResolver().query(uri1, new String[]{SearchManager.SUGGEST_COLUMN_QUERY}, SearchManager.SUGGEST_COLUMN_QUERY + " like ?",
                        selectionArgs, sortOrder);
                if (Util.isEmptyRequest(selectionArgs)) {
                    if(recentSearch.getCount()>0) {
                        retCursor = createMatrixCursor(recentSearch, RECENT_SEARCH_ORIGIN);;
                    } else {
                        retCursor = null;
                    }
                } else {
                    Cursor searchDatabase = mDbHelper.getReadableDatabase().query(
                            DbContract.FarmaciasEntity.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);

                    MatrixCursor matrixCursorA = createMatrixCursor(searchDatabase,DATABASE_SEARCH_ORIGIN);
                    if(recentSearch.getCount()>0) {
                        MatrixCursor  matrixCursorB = createMatrixCursor(recentSearch,RECENT_SEARCH_ORIGIN);
                        MergeCursor mergeCursor =new MergeCursor(new Cursor[]{matrixCursorB,matrixCursorA});
                        retCursor =mergeCursor;
                    } else {
                        retCursor = matrixCursorA;
                    }



                    Util.logD("log", "log");
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private MatrixCursor createMatrixCursor(Cursor recentSearch, int cursorOrigin) {
        MatrixCursor matrixCursor = new MatrixCursor(suggestionsColumnNames, recentSearch.getCount());
        MatrixCursor.RowBuilder builder;
        if (recentSearch.moveToFirst()) {
            do {
                builder = matrixCursor.newRow();
                if (cursorOrigin == RECENT_SEARCH_ORIGIN) {

                    builder.add(QuickSearchAdapter.HISTORY_ROW);
                    builder.add(recentSearch.getString(recentSearch.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY)));

                } else {

                    builder.add(QuickSearchAdapter.DATABASE_ROW);
                    builder.add(recentSearch.getString(recentSearch.getColumnIndex(DbContract.FarmaciasEntity.NAME)));
                }
            } while (recentSearch.moveToNext());
        }
        return matrixCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case FARMACIAS:
                rowsDeleted = db.delete(DbContract.FarmaciasEntity.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown ur:" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case FARMACIAS_ID:
                rowsUpdated = db.update(DbContract.FarmaciasEntity.TABLE_NAME, contentValues, s, strings);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case FARMACIAS:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DbContract.FarmaciasEntity.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }


    }
}
