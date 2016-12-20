package com.chernandezgil.farmacias.customwidget;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;

import com.chernandezgil.farmacias.R;
import com.github.andrewlord1990.snackbarbuilder.SnackbarBuilder;
import com.github.andrewlord1990.snackbarbuilder.callback.SnackbarCallback;

/**
 * Created by Carlos on 14/09/2016.
 */

public class SnackBarWrapper {
    private Snackbar snackbar;
    SnackbarBuilder builder;
    CoordinatorLayout rootLayout;

    public SnackBarWrapper(Activity context) {
        builder = new SnackbarBuilder(context)
                .message(R.string.sbw_nuevas_localizaciones)
                .duration(Snackbar.LENGTH_INDEFINITE)
                .actionText(R.string.sbw_actualizar)
                .actionTextColorRes(R.color.blue_200);

        setRootLayout(context);
    }

    public SnackBarWrapper(Activity context, String message,int duration) {

        snackbar = new SnackbarBuilder(context)
                .message(message)
                .duration(duration)
                .build();
        setRootLayout(context);
    }

    private void setRootLayout(Activity context) {
        rootLayout = (CoordinatorLayout) context.findViewById(R.id.coordinatorSnackContainer);
    }
    public void show() {
        rootLayout.setVisibility(View.VISIBLE);
        snackbar.show();

    }
    public void dismiss(){
        rootLayout.setVisibility(View.GONE);
        snackbar.dismiss();
    }
    public void addCallback(SnackbarCallback snackbarCallback) {
        snackbar = builder.snackbarCallback(snackbarCallback).build();
    }

}
