package com.chernandezgil.farmacias.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.database.DbContract;
import com.chernandezgil.farmacias.model.FarmaciasCsvBean;
import com.chernandezgil.farmacias.supercsv.CsvReader;

import java.util.List;
import java.util.Vector;

/**
 * Created by Carlos on 09/07/2016.
 */
public class DownloadFarmacias extends IntentService {
    private static String LOG_TAG=DownloadFarmacias.class.getSimpleName();
    public DownloadFarmacias(String name) {
        super(name);
    }
    public DownloadFarmacias(){
        super(LOG_TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        List<FarmaciasCsvBean> listFarmacias= CsvReader.readWithCsvBeanReader();
        Vector<ContentValues> vector=new Vector<>();
        if(listFarmacias!=null) {
            getContentResolver().delete(DbContract.FarmaciasEntity.CONTENT_URI,null,null);
        }
        for(int i = 0; i<listFarmacias.size();i++) {
            FarmaciasCsvBean farmaciasCsvBean=listFarmacias.get(i);
            ContentValues contentValues=new ContentValues();
            contentValues.put(DbContract.FarmaciasEntity.NAME,farmaciasCsvBean.getName());
            contentValues.put(DbContract.FarmaciasEntity.HOURS,farmaciasCsvBean.getHorario());
            contentValues.put(DbContract.FarmaciasEntity.ADDRESS,farmaciasCsvBean.getAddress());
            contentValues.put(DbContract.FarmaciasEntity.LOCALITY,farmaciasCsvBean.getLocality());
            contentValues.put(DbContract.FarmaciasEntity.PROVINCE,farmaciasCsvBean.getProvince());
            contentValues.put(DbContract.FarmaciasEntity.POSTAL_CODE,farmaciasCsvBean.getPostal_code());
            contentValues.put(DbContract.FarmaciasEntity.PHONE,farmaciasCsvBean.getPhone());
            contentValues.put(DbContract.FarmaciasEntity.LAT,farmaciasCsvBean.getLat());
            contentValues.put(DbContract.FarmaciasEntity.LON,farmaciasCsvBean.getLon());
            vector.add(contentValues);


        }
        ContentValues[] contentValues=new ContentValues[vector.size()];
        vector.toArray(contentValues);
        Uri uri= DbContract.FarmaciasEntity.CONTENT_URI;
        int inserted=getContentResolver().bulkInsert(uri,contentValues);
        Util.LOGD(LOG_TAG,String.format("successfully inserted %d registers in farmacias",inserted));
    }
}
