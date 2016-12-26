package com.chernandezgil.farmacias.customwidget;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.chernandezgil.farmacias.R;

import static clojure.lang.RT.init;

/**
 * A special variation of ImageView that can be used as a checkable object.
 * Inherits from AppCompatImageView instead of ImageView, else the scrCompat property in xml
 * doesn't work. AppCompat library injects its own views(preceded with AppCompat) in
 * order to provide functionality like tinting and others backporting thins.
 */
public class CheckableImageViewv2 extends AppCompatImageView implements Checkable {
    private boolean mChecked;
    private OnCheckStateListener mOnCheckStateListener;
    private ColorStateList tint;


    public CheckableImageViewv2(Context context) {
        super(context);
    }
    public CheckableImageViewv2(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckableImageViewv2);
        tint = a.getColorStateList(R.styleable.CheckableImageViewv2_tin);
        a.recycle();
    }

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };


    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
//        if(Build.VERSION.SDK_INT>19) {
//            return ;
//        }
        if (tint != null && tint.isStateful())
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        int color = tint.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }


    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            if (mOnCheckStateListener != null)
                mOnCheckStateListener.onCheckStateChanged(checked);
        }
    }


    public interface OnCheckStateListener {
        void onCheckStateChanged(boolean checked);
    }
    /**
     *  Better this one. Interface definition for a callback to be invoked when the checked state of this View is
     * changed.
     */
    //    public static interface OnCheckedChangeListener {
    //
    //        /**
    //         * Called when the checked state of a compound button has changed.
    //         *
    //         * @param checkableView The view whose state has changed.
    //         * @param isChecked     The new checked state of checkableView.
    //         */
    //        void onCheckedChanged(View checkableView, boolean isChecked);
    //    }

    /**
     * Register a callback to be invoked when the checked state of this view(ImageView changes.
     *
     * param: the callback to call on checked state change
     */
    public void setOnCheckStateListener(OnCheckStateListener onCheckStateListener) {
        mOnCheckStateListener = onCheckStateListener;
    }
}