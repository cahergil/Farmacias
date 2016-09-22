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
        setContentView(R.layout.activity_settings); //make this trick in so that the enter transitions appears
        //put inside android.R.id.content identification
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.settings_title));
        actionBar.setDisplayHomeAsUpEnabled(false);
        Transition enterTrans = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            enterTrans = new Slide(Gravity.RIGHT);
            enterTrans.setDuration(300);
            enterTrans.setInterpolator(AnimationUtils.loadInterpolator(this,android.R.interpolator.linear));

            enterTrans.excludeTarget(android.R.id.statusBarBackground,true);
            enterTrans.excludeTarget(android.R.id.navigationBarBackground,true);


            //   enterTrans.addTarget(android.R.id.content);
            getWindow().setEnterTransition(enterTrans);
        }



        SettingsFragment set=new SettingsFragment();
        //set.setEnterTransition(enterTrans);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,set)
                .commit();

    }


    @Override
    public void onBackPressed() {
        //the automatic exit transition doesn't work if i show the up button, therefore the up button is disabled
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}



