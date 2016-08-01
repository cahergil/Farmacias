package com.chernandezgil.farmacias;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Carlos on 01/08/2016.
 */
@Module
public class ApplicationModule {
    private final Context mContext;

    ApplicationModule(Context context) {
        this.mContext=context;
    }
    @Provides
    Context provideContext(){
        return mContext;
    }
}

