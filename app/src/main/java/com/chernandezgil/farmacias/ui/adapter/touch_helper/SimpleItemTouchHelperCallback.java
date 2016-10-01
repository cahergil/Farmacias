package com.chernandezgil.farmacias.ui.adapter.touch_helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;

/**
 * Created by Carlos on 28/09/2016.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter mAdapter;
    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    boolean initiated;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter){
        mAdapter = adapter;

    }

    private void init(){
        background = new ColorDrawable(Utils.getColor(R.color.colorAccent));
        xMark = Utils.getDrawable(R.drawable.ic_clear_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) Utils.getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
      //  int swipeFlags = ItemTouchHelper.START;  //| ItemTouchHelper.END;
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        Utils.logD("onMove","onMove");
        return true;
    }



    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

         mAdapter.pendingRemoval(position);
     //   mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        Utils.logD("onSwiped","onSwiped");
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {

        return 0.5f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue*6.6f;
    }

//    @Override
//    public float getSwipeVelocityThreshold(float defaultValue) {
//        return defaultValue* 15;
//    }



    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        Utils.logD("clearView","clearView");
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemViewHolder =
                    (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        Utils.logD("onSelectedChanged","onSelectedChanged");
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ItemTouchHelperViewHolder itemViewHolder =
                        (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        Utils.logD("onChildDraw","dX:"+dX + ",dY:"+dY+",isCurrentlyActive:"+isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

//            float width = (float) viewHolder.itemView.getWidth();
//            float alpha = 1.0f - Math.abs(dX) / width;
//            viewHolder.itemView.setAlpha(alpha);
//            viewHolder.itemView.setTranslationX(dX);
            View itemView = viewHolder.itemView;
            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
                // not interested in those
                return;
            }
            if (!initiated) {
                init();
            }

            // draw red background
            //background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.setBounds(itemView.getLeft(), itemView.getTop(), (int)dX, itemView.getBottom());
            background.draw(c);
            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

//            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
//            int xMarkRight = itemView.getRight() - xMarkMargin;
//            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
//            int xMarkBottom = xMarkTop + intrinsicHeight;
             int xMarkLeft =itemView.getLeft()+ xMarkMargin;
             int xMarkRight =itemView.getLeft()+ xMarkMargin+intrinsicWidth;
             int xMarkTop = itemView.getTop()+(itemHeight - intrinsicHeight)/2;
             int xMarkBottom = xMarkTop + intrinsicHeight;
             xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
             xMark.draw(c);
             super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }
}
