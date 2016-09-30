package com.chernandezgil.farmacias.Utilities;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;

import com.chernandezgil.farmacias.MyApplication;

/**
 * Created by Carlos on 30/09/2016.
 */

public class ColorUtils {
    /**
     * For use in methods like Color.parseColor("")
     * @param originalColor color, without alpha
     * @param alpha         from 0.0 to 1.0
     * @return
     */
    public static String addAlpha(String originalColor, double alpha) {
        long alphaFixed = Math.round(alpha * 255);
        String alphaHex = Long.toHexString(alphaFixed);
        if (alphaHex.length() == 1) {
            alphaHex = "0" + alphaHex;
        }
        originalColor = originalColor.replace("#", "#" + alphaHex);


        return originalColor;
    }

    /**
     *
     * http://robusttechhouse.com/tutorial-how-to-use-android-support-annotations-library/
     */
    public static @CheckResult @ColorInt int modifyAlpha(@ColorInt int color,
                    @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     *
     * http://robusttechhouse.com/tutorial-how-to-use-android-support-annotations-library/
     */
    public static @CheckResult @ColorInt int modifyAlpha(@ColorInt int color,
                                                         @FloatRange(from = 0f, to = 1f) float alpha) {
        return modifyAlpha(color, (int) (255f * alpha));
    }
    /**
     * R.color.color_id
     */
    public static @CheckResult @ColorInt int modifyAlpha(@ColorRes long color,
                                                         @FloatRange(from = 0f, to = 1f) float alpha) {
        int colorInt;
        colorInt = ContextCompat.getColor(MyApplication.getContext(),(int)color);
        return modifyAlpha(colorInt, (int) (255f * alpha));
    }

}
