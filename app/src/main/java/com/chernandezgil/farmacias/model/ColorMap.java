package com.chernandezgil.farmacias.model;

import android.content.Context;
import android.graphics.Color;

import com.chernandezgil.farmacias.MyApplication;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;

import java.util.HashMap;

/**
 * Created by Carlos on 01/10/2016.
 */

public class ColorMap {


    private HashMap<String, Integer> colorHashMap;
    int[] colors;


    public ColorMap() {
        colorHashMap = new HashMap<>();
        loadColorsArray();
    }
    private void loadColorsArray() {

        colors = MyApplication.getContext().getResources().getIntArray(R.array.colors);
    }
    public void generate() {

        for (int i = 0; i < 26; i++) {
            colorHashMap.put(Utils.characterFromInteger(i), colors[i]);

        }

    }

    public int getColorForString(String letter) {
        return colorHashMap.get(letter);
    }



    private int generateRandomColor() {
        int red = ((int) (Math.random() * 255));
        int green = ((int) (Math.random() * 255));
        int blue = ((int) (Math.random() * 255));
        return Color.rgb(red, green, blue);
    }

    public HashMap<String, Integer> getColorHashMap() {
        return colorHashMap;
    }

    public void setColorHashMap(HashMap<String, Integer> colorHashMap) {
        this.colorHashMap = colorHashMap;
    }
}
