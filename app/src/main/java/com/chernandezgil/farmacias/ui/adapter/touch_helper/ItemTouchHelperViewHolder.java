package com.chernandezgil.farmacias.ui.adapter.touch_helper;

/**
 * Created by Carlos on 28/09/2016.
 */

/**
 * Notifies a View Holder of relevant callbacks from
 *  ItemTouchHelper.Callback}.
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the  ItemTouchHelper first registers an
     * item as being moved or swiped.
     * Implementations should update the item view to indicate
     * it's active state.
     */
    void onItemSelected();


    /**
     * Called when the ItemTouchHelper has completed the
     * move or swipe, and the active item state should be cleared.
     */
    void onItemClear();
}