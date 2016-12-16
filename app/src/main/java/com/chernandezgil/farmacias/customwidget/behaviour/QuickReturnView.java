package com.chernandezgil.farmacias.customwidget.behaviour;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.chernandezgil.farmacias.customwidget.BottomNavigation;

/**
 * Created by Carlos on 16/12/2016.
 */

public class QuickReturnView extends CoordinatorLayout.Behavior<View> {

    //scrolling in Recyclerview causes AppBarLayout to scroll, because this last one is
    // always watching this kind of
    //scrolling from views with the default behavior attached
    //So, Recyclerview->AppBarLayout->View

    public QuickReturnView() {
    }

    public QuickReturnView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   View child,
                                   View dependency) {

        //watching changes in AppBarLayout
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,
                                          View child,
                                          View dependency) {

        int offset = -dependency.getTop();
        child.setTranslationY(offset);
        return true;

    }
}
