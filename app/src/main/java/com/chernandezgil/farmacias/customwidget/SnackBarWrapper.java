package com.chernandezgil.farmacias.customwidget;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;

import com.chernandezgil.farmacias.R;
import com.github.andrewlord1990.snackbarbuilder.SnackbarBuilder;
import com.github.andrewlord1990.snackbarbuilder.callback.SnackbarCallback;

/**
 * Created by Carlos on 14/09/2016.
 */

public class SnackBarWrapper {
    private Activity context;
    private Snackbar snackbar;
    SnackbarBuilder builder;
    CoordinatorLayout frameLayout;

    public SnackBarWrapper(Activity context) {

        this.context = context;
        builder = new SnackbarBuilder(context)

                .message("Nueva localizacion detectada")
                .duration(Snackbar.LENGTH_INDEFINITE)
                .actionText("ACTUALIZAR");

        frameLayout = (CoordinatorLayout) context.findViewById(R.id.snackContainer);

    }

    public void show() {
        frameLayout.setVisibility(View.VISIBLE);
        snackbar.show();

    }
    public void dismiss(){
        frameLayout.setVisibility(View.GONE);
        snackbar.dismiss();
    }
    public void addCallback(SnackbarCallback snackbarCallback) {
        snackbar = builder.snackbarCallback(snackbarCallback).build();
    }

}
