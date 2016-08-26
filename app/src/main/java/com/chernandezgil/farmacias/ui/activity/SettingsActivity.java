package com.chernandezgil.farmacias.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.customwidget.SeekBarPreference;

import butterknife.BindView;

/**
 * Created by Carlos on 11/08/2016.
 */
public class SettingsActivity extends PreferenceActivity implements  Preference.OnPreferenceChangeListener
,Preference.OnPreferenceClickListener {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.toolbar_activity_settings)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpToolBar();
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.seek_bar_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_rate_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_email_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_share_app_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_version_key)));

    }

    private void setUpToolBar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        mToolbar.setTitle(getString(R.string.settings_title));
        mToolbar.setTitleTextColor(Color.WHITE);

        //http://developer.android.com/intl/es/reference/android/support/v7/appcompat/R.drawable.html
        //by default the color is pale grey
      //  final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha); //23.3
        //  final Drawable upArrow= ContextCompat.getDrawable(this,R.drawable.ic_action_back);
        final Drawable upArrow= ContextCompat.getDrawable(this,R.drawable.abc_ic_ab_back_material); //23.2,24.0
        //tint the arrow to white
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            }
        });

    }
    @Override
    protected void onResume() {

        super.onResume();
    }


    protected void onPause() {

        super.onPause();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        preference.setOnPreferenceClickListener(this);

        if (preference instanceof CheckBoxPreference) {
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        } else if (preference instanceof SeekBarPreference){
            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 0));
        } else {
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
            if(preference instanceof Preference) {
                Preference pref=(Preference) preference;
                //scale the vector drawable R.drawable.google_play to 24f
                if(getString(R.string.pref_rate_key).equals(pref.getKey())) {
                    VectorDrawableCompat drawable = VectorDrawableCompat.create(getApplicationContext().getResources(),
                            R.drawable.google_play, null);
                    Bitmap bitmap = Util.createScaledBitMapFromVectorDrawable(getApplicationContext(), drawable, 24f);
                    Drawable rateIcon = new BitmapDrawable(getResources(), bitmap);
                    pref.setIcon(rateIcon);
                }
            }
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof SeekBarPreference) {
            preference.setSummary(("" + stringValue + " Km"));


        } else if(preference instanceof  Preference) {
            String key = preference.getKey();
            //we take the versionName and assign it to the pref var.
            if (key == getString(R.string.pref_version_key)) {
                String versionName;
                try {
                    versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                } catch (PackageManager.NameNotFoundException e) {
                    versionName = null;
                }
                preference.setSummary(versionName);

            }
        }
        return true;
    }





    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_email_key))) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                    getString(R.string.mailto), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        }  else if (key.equals(getString(R.string.pref_share_app_key))) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            //https://play.google.com/store/apps/details?id=com.carlos.capstone
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_play_store_address));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        return true;
    }
}
