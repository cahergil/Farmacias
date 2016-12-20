package com.chernandezgil.farmacias.customwidget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.data.source.local.RecentSuggestionsProvider;

/**
 * Created by Carlos on 20/12/2016.
 */

public class DialogBorrarHistorialBusqueda extends DialogFragment {


    public DialogBorrarHistorialBusqueda() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.borrar_historial_confirmar_accion))
                .setMessage(getString(R.string.borrar_historial_mensaje))
                .setIcon(R.drawable.alert_box)
                .setNegativeButton(getString(R.string.borrar_historial_no_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(getString(R.string.borrar_historial_yes_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // RecentSuggestionsProvider.BASE_CONTENT_URI.buildUpon().appendPath("suggestions") segun
                        // el codigo de la clase para los insert hay que agregar suggestions
                        Uri uri = RecentSuggestionsProvider.BASE_CONTENT_URI.buildUpon().appendPath("suggestions").build();
                        int deletedRows = getActivity().getContentResolver().delete(uri,null,null);
                        if(deletedRows>0) {
                            Toast.makeText(getActivity(),getString(R.string.pref_delete_toast_message),Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();

        return dialog;
    }
}
