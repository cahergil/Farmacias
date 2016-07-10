package com.chernandezgil.farmacias.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Carlos on 09/07/2016.
 */

@SuppressWarnings("ConstantConditions") //to delete lint warning getContext().getContentResolver() may produce npe...
public class DbProvider extends ContentProvider {

    private  DbHelper mDbHelper;

    private static UriMatcher mUriMatcher=buildUriMatcher();

    private static final int FARMACIAS=100;
    private static final int FARMACIAS_ID=101;

    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority=DbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,DbContract.PATH_FARMACIAS + "/*", FARMACIAS_ID);


        matcher.addURI(authority,DbContract.PATH_FARMACIAS,FARMACIAS);

        return matcher;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match=mUriMatcher.match(uri);
        switch(match) {

            case FARMACIAS_ID:
                return DbContract.FarmaciasEntity.CONTENT_ITEM_TYPE;
            case FARMACIAS:
                return DbContract.FarmaciasEntity.CONTENT_TYPE;

            default:throw new UnsupportedOperationException("Unknown uri :" + uri );
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
                retCursor= mDbHelper.getReadableDatabase().query(
                        DbContract.FarmaciasEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FARMACIAS_ID:
                retCursor= mDbHelper.getReadableDatabase().query(
                        DbContract.FarmaciasEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


            default: throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int rowsDeleted=0;
        final int match = mUriMatcher.match(uri);
        switch (match){
            case FARMACIAS:
                rowsDeleted=db.delete(DbContract.FarmaciasEntity.TABLE_NAME,selection,selectionArgs);
                break;

            default:throw new UnsupportedOperationException("Unknown ur:"+uri);
        }
        if(rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values)
    {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int returnCount=0;
        switch (match)
        {
            case FARMACIAS:
                db.beginTransaction();

                try
                {
                    for(ContentValues value : values)
                    {
                        long _id=db.insertWithOnConflict(DbContract.FarmaciasEntity.TABLE_NAME,null,value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;

            default:
                return super.bulkInsert(uri,values);
        }


    }
}
