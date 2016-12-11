package com.chernandezgil.farmacias.customwidget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by Carlos on 11/12/2016.
 */

public class ScrollerLinearLayoutManager extends LinearLayoutManager {

    private Context mContext;
    public ScrollerLinearLayoutManager(Context mContext) {
        super(mContext);
        this.mContext =mContext;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        //
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext){
            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return ScrollerLinearLayoutManager.this.computeScrollVectorForPosition
                        (targetPosition);
            }

            //1 pixel -> 0.05 ms
            //1000 pixel -> x=50 ms to go over the height of the screen
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.05f;
                //return x /displayMetrics.densityDpi;
            }
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
