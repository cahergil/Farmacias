package com.chernandezgil.farmacias.customwidget;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.chernandezgil.farmacias.R;

/**
 * Created by Carlos on 08/12/2016.
 */

public class DialogOpeningHoursPharmacy extends DialogFragment {

    private static final String LAYOUT_KEY="layout";
    int layoutId;

    public static DialogOpeningHoursPharmacy newInstance(int layoutId) {

        Bundle args = new Bundle();
        args.putInt(LAYOUT_KEY,layoutId);
        DialogOpeningHoursPharmacy fragment = new DialogOpeningHoursPharmacy();
        fragment.setArguments(args);
        return fragment;
    }

    public DialogOpeningHoursPharmacy(){


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        this.layoutId=bundle.getInt(LAYOUT_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(layoutId, null);
        View title = inflater.inflate(R.layout.dialog_openinghours_title,null);
        builder.setView(v);
        builder.setPositiveButton(R.string.dialog_ok_button,null);
        builder.setCustomTitle(title);
        builder.setIcon(R.drawable.clock);
        builder.setMessage(R.string.dialog_mensaje);

        return builder.create();
    }
}
