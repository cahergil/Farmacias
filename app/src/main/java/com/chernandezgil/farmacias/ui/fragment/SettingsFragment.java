package com.chernandezgil.farmacias.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.customwidget.SeekBarPreference;
import com.chernandezgil.farmacias.ui.activity.ActivitySettings2;

/**
 * Created by Carlos on 22/09/2016.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener{

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logD(LOG_TAG, "onCreate");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.seek_bar_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_rate_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_email_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_share_app_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_version_key)));
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
        } else if (preference instanceof SeekBarPreference) {
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
            if (preference instanceof Preference) {
                Preference pref = (Preference) preference;
                //scale the vector drawable R.drawable.google_play to 24f
                if (getString(R.string.pref_rate_key).equals(pref.getKey())) {
                    VectorDrawableCompat drawable = VectorDrawableCompat.create(getActivity().getApplicationContext().getResources(),
                            R.drawable.google_play, null);
                    Bitmap bitmap = Util.createScaledBitMapFromVectorDrawable(getActivity().getApplicationContext(), drawable, 24f);
                    Drawable rateIcon = new BitmapDrawable(getResources(), bitmap);
                    pref.setIcon(rateIcon);
                }
            }
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof SeekBarPreference) {
            preference.setSummary(("" + stringValue + " Km"));


        } else if (preference instanceof Preference) {
            String key = preference.getKey();
            //we take the versionName and assign it to the pref var.
            if (key == getString(R.string.pref_version_key)) {
                String versionName;
                try {
                    versionName = getActivity().getPackageManager().getPackageInfo(
                            getActivity().getPackageName(), 0).versionName;

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
