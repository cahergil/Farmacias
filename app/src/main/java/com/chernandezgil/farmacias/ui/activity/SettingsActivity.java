package com.chernandezgil.farmacias.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.animation.AnimationUtils;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.ui.fragment.SettingsFragment;

/**
 * Created by Carlos on 22/09/2016.
 */

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logD(LOG_TAG,"onCreate");
        //comment: no more necessary because I am no longer using Transitions api v21
        //setContentView(R.layout.activity_settings);
        //make this trick in so that the enter transitions appears
        //put inside android.R.id.content identification


        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.settings_title));
        actionBar.setDisplayHomeAsUpEnabled(false);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingsFragment())
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



