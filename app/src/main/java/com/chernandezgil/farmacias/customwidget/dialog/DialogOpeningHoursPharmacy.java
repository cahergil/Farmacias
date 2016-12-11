package com.chernandezgil.farmacias.customwidget.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chernandezgil.farmacias.R;

/**
 * Created by Carlos on 08/12/2016.
 */

public class DialogOpeningHoursPharmacy extends DialogFragment {

    private static final String LAYOUT_KEY = "layout";
    private static final String COLOR_KEY = "color";
    int layoutId;
    int backgroundTitleColor;

    public static DialogOpeningHoursPharmacy newInstance(int layoutId) {

        Bundle args = new Bundle();
        args.putInt(LAYOUT_KEY, layoutId);
        DialogOpeningHoursPharmacy fragment = new DialogOpeningHoursPharmacy();
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogOpeningHoursPharmacy newInstance(int layoutId, int colorInt) {

        Bundle args = new Bundle();
        args.putInt(LAYOUT_KEY, layoutId);
        args.putInt(COLOR_KEY, colorInt);
        DialogOpeningHoursPharmacy fragment = new DialogOpeningHoursPharmacy();
        fragment.setArguments(args);
        return fragment;
    }

    public DialogOpeningHoursPharmacy() {


    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        this.layoutId = bundle.getInt(LAYOUT_KEY);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(layoutId, null);
        RelativeLayout title = (RelativeLayout) v.findViewById(R.id.titleDialog);
        if (bundle.containsKey(COLOR_KEY)) {
            this.backgroundTitleColor = bundle.getInt(COLOR_KEY);
            title.setBackgroundColor(backgroundTitleColor);
        }


        //    View title = inflater.inflate(R.layout.dialog_openinghours_title,null);
        builder.setView(v);
        builder.setPositiveButton(R.string.dialog_ok_button, null);
        //    builder.setCustomTitle(title);
        builder.setIcon(R.drawable.clock);
        //  builder.setMessage(R.string.dialog_mensaje);


        return builder.create();
    }
}
