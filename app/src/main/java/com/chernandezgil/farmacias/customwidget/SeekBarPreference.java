package com.chernandezgil.farmacias.customwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;

/**
 * Created by Carlos on 11/08/2016.
 */

//see https://developer.android.com/guide/topics/ui/settings.html#Custom
public class SeekBarPreference extends Preference
        implements SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_VALUE = 5;
    private static final String LOG_TAG = SeekBarPreference.class.getSimpleName();
    private SeekBar mSeekBar;
    private int mProgress;
    private int mMax;
    private TextView tvSummary;


    public SeekBarPreference(Context context) {
        super(context);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SeekBarPreference,
                0, 0);

        try {

            mMax = a.getInteger(R.styleable.SeekBarPreference_max, 50);
        } finally {
            a.recycle();
        }
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        Utils.logD(LOG_TAG, "onCreateView");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.preference_seekbar, parent, false);
        tvSummary = (TextView) view.findViewById(android.R.id.summary);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setProgress(mProgress);
        mSeekBar.setMax(mMax);
        mSeekBar.setOnSeekBarChangeListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Utils.logD(LOG_TAG, "onProgressChanged");

        if (!fromUser)
            return;


        mSeekBar.setProgress(progress);
        tvSummary.setText(getContext().getString(R.string.sbp_distance, progress));
        if (shouldPersist()) {
            persistInt(progress);
        }

       //not call this, freezes the seekbar
      //callChangeListener(progress);


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("", "onStartTrackingTouch");

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("", "onStopTrackingTouch");
        //mSeekBar.setSecondaryProgress(seekBar.getProgress());

    }

    //the following two methods are called before onCreateView.

    //if we specify android:defaultValue in xml this code will run.
    // El motivo por el que hay implementar este método para extraer el
    // valor predeterminado del atributo es que debes especificar un valor
    // predeterminado local para el atributo en caso de que el valor no esté definido
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        Utils.logD(LOG_TAG, "onGetDefaultValue:index:" + index);
        Utils.logD(LOG_TAG, "onGetDefaultValue:a.getInt(index,0):" + a.getInt(index, 0));

        return a.getInt(index, DEFAULT_VALUE);
    }

    //see https://developer.android.com/guide/topics/ui/settings.html#Custom
    // if there is already a persistent value then restoreValue returns true,
    // else returns false and defaultValue takes the default value from xml
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Utils.logD(LOG_TAG, "onSetInitialValue:" + restoreValue + ",defaultvalue:" + (Integer) defaultValue);

        //getPersistedInt in theory doesn't need a value,
        // because it simply gets the persisted value
        // whichever it be, but it has a parameter
        // in case it can't found it

        setValue(restoreValue ? getPersistedInt(DEFAULT_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        Utils.logD(LOG_TAG, "setValue");
        if (shouldPersist()) {
            persistInt(value);
        }

        if (value != mProgress) {
            mProgress = value;
            notifyChanged();
        }
    }


    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}