package com.chernandezgil.farmacias.Utilities;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Carlos on 04/08/2016.
 */
public class Constants {

    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String CR = "\n";
    public static final String EMPTY_STRING = "";
    public static final String SEMI_COLON=":";
    public static final int BOTTOM_NAVIGATION_HEIGHT=168;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SCROLL_DOWN, SCROLL_UP})
    public @interface ScrollDirection {
    }

    public static final int SCROLL_DOWN = 0;
    public static final int SCROLL_UP = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MAP_TAB,FAVORITE})
    public @interface LayoutType {
    }

    public static final int MAP_TAB = 0;
    public static final int FAVORITE = 1;
}
