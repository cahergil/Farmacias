package com.chernandezgil.farmacias.ui.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Carlos on 19/09/2016.
 */

public class CustomItemAnimator extends SlideInItemAnimator {
    private boolean animateMoves = false;
    public CustomItemAnimator() {
        super();
    }

    void setAnimateMoves(boolean animateMoves) {
        this.animateMoves = animateMoves;
    }

    @Override
    public boolean animateMove(
            RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        if (!animateMoves) {
            dispatchMoveFinished(holder);
            return false;
        }
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }
}
