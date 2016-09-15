package com.chernandezgil.farmacias.Utilities;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.github.andrewlord1990.snackbarbuilder.SnackbarBuilder;
import com.github.andrewlord1990.snackbarbuilder.callback.SnackbarCallback;

/**
 * Created by Carlos on 14/09/2016.
 */

public class SnackBarWrapper {
    private Activity context;
    private Snackbar snackbar;
    SnackbarBuilder builder;

    public SnackBarWrapper(Activity context) {
        this.context = context;
        builder = new SnackbarBuilder(context)
                .message("Nuevas localizacion detectada")
                .duration(Snackbar.LENGTH_INDEFINITE)
                .actionText("ACTUALIZAR");



    }

    public void show() {
        snackbar.show();
    }

    public void addCallback(SnackbarCallback snackbarCallback) {
        snackbar = builder.snackbarCallback(snackbarCallback).build();
    }

}
