package com.chernandezgil.farmacias.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;

/**
 * Created by Carlos on 21/09/2016.
 */

public class Prueba extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    //   getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                    Transition enterTrans = new Slide(Gravity.RIGHT);
                    enterTrans.setDuration(300);
                    getWindow().setEnterTransition(enterTrans);
                }
    }


//    setContentView(R.layout.prueba);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            //   getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            Transition enterTrans = new Slide(Gravity.RIGHT);
//            enterTrans.setDuration(300);
//            getWindow().setEnterTransition(enterTrans);
//
//
//        }

//        TextView tv = (TextView) findViewById(R.id.texto);
//        tv.setText("hola adafadafdfadfa");
//        tv.setTextColor(ContextCompat.getColor(this,R.color.black));

}
