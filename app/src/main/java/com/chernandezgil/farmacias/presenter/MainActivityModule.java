package com.chernandezgil.farmacias.presenter;

import android.content.Context;

import com.chernandezgil.farmacias.view.MainActivityContract;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Carlos on 01/08/2016.
 */
@Module
public class MainActivityModule {
    private MainActivityContract mainActivityContract;

    public MainActivityModule(){

    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


    }


}
