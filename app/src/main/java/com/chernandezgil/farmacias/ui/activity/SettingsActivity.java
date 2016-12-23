package com.chernandezgil.farmacias.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.ui.fragment.SettingsFragment;

/**
 * Created by Carlos on 22/09/2016.
 */

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
 //   private ActionBar actionBar;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.logD(LOG_TAG,"onCreate");


        setContentView(R.layout.activity_settings);
        // with the addition of an image, toolbar is no longer needed.
        // actionBar = getSupportActionBar();
        // actionBar.setTitle(getString(R.string.settings_title));
        // actionBar.setDisplayHomeAsUpEnabled(false);

        getFragmentManager().beginTransaction()
                .replace(R.id.contenido,new SettingsFragment())
                .commit();

    }


    @Override
    public void onBackPressed() {
        //the automatic exit transition doesn't work if i show the up button, therefore the up button is disabled
        //the automatic exit transition works with back button
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_reenter, R.anim.slide_out);
    }
}



