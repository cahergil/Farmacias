package com.chernandezgil.farmacias.ui.adapter.touch_helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Carlos on 28/09/2016.
 */

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}