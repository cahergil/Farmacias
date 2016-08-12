package com.chernandezgil.farmacias.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chernandezgil.farmacias.data.source.local.DbContract;

/**
 * Created by Carlos on 09/07/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "farmacias.db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FARMACIAS_TABLE="CREATE TABLE "+ DbContract.FarmaciasEntity.TABLE_NAME + " ("+
                DbContract.FarmaciasEntity._ID + " INTEGER PRIMARY_KEY," +
                DbContract.FarmaciasEntity.NAME + " TEXT NOT NULL," +
                DbContract.FarmaciasEntity.HOURS + " TEXT," +
                DbContract.FarmaciasEntity.ADDRESS + " TEXT," +
                DbContract.FarmaciasEntity.LOCALITY + " TEXT," +
                DbContract.FarmaciasEntity.PROVINCE + " TEXT," +
                DbContract.FarmaciasEntity.POSTAL_CODE + " TEXT," +
                DbContract.FarmaciasEntity.PHONE + " TEXT NOT NULL,"+
                DbContract.FarmaciasEntity.LAT + " REAL NOT NULL,"+
                DbContract.FarmaciasEntity.LON + " REAL NOT NULL," +
                DbContract.FarmaciasEntity.FAVORITE + " INTEGER DEFAULT 0," +
                " UNIQUE (" + DbContract.FarmaciasEntity.PHONE + ") ON CONFLICT IGNORE)";

        Log.d("sql-statments",SQL_CREATE_FARMACIAS_TABLE);

        db.execSQL(SQL_CREATE_FARMACIAS_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.FarmaciasEntity.TABLE_NAME);
        onCreate(db);
    }
}
