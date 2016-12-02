package com.chernandezgil.farmacias.customwidget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Utils;

/**
 * Created by Carlos on 01/12/2016.
 */

public class BottomNavigation extends LinearLayout implements View.OnClickListener {
    private static final int NONE = -1;
    private static final int CAPACITY = 3;
    private static final int ANIMATION_DURATION = 200;
    private LinearLayout llAround, llBuscar, llFavorites;
    private CheckableImageView ivAround, ivBuscar, ivFavorites;
    private TextView tvAround, tvBuscar, tvFavorites;
    private SparseArray<CheckableImageView> mHasMapImages;
    private SparseArray<TextView> mHasMapText;
    private int lastCheckedId = NONE;
    AnimatorSet animatorCheck = new AnimatorSet();
    AnimatorSet animatorUncheck = new AnimatorSet();

    public BottomNavigation(Context context) {
        super(context);
        //   init();
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.customview_bottom_navigation, this);
        llAround = (LinearLayout) findViewById(R.id.llAround);
        llBuscar = (LinearLayout) findViewById(R.id.llBuscar);
        llFavorites = (LinearLayout) findViewById(R.id.llFavorite);
        ivAround = (CheckableImageView) findViewById(R.id.bnv_around);
        ivBuscar = (CheckableImageView) findViewById(R.id.bnv_buscar);
        ivFavorites = (CheckableImageView) findViewById(R.id.bnv_favoritos);

        tvAround = (TextView) findViewById(R.id.text_around);
        tvBuscar = (TextView) findViewById(R.id.text_buscar);
        tvFavorites = (TextView) findViewById(R.id.text_favoritos);

        llAround.setOnClickListener(this);
        llBuscar.setOnClickListener(this);
        llFavorites.setOnClickListener(this);

        mHasMapImages = new SparseArray<>(CAPACITY);
        mHasMapImages.put(llAround.getId(), ivAround);
        mHasMapImages.put(llBuscar.getId(), ivBuscar);
        mHasMapImages.put(llFavorites.getId(), ivFavorites);

        mHasMapText = new SparseArray<>(CAPACITY);
        mHasMapText.put(llAround.getId(), tvAround);
        mHasMapText.put(llBuscar.getId(), tvBuscar);
        mHasMapText.put(llFavorites.getId(), tvFavorites);


    }


    @Override
    public void onClick(View view) {
        animatorCheck = new AnimatorSet();
        animatorUncheck = new AnimatorSet();
        int id = view.getId();

        if(id == lastCheckedId) {
            //implement scroll up in recyclerviews
            return;
        }
        moveAnimations(id,true);



    }

    private void moveAnimations(int id, boolean move) {
        ivAround.setChecked(id == R.id.llAround);
        ivBuscar.setChecked(id == R.id.llBuscar);
        ivFavorites.setChecked(id == R.id.llFavorite);

        ObjectAnimator textViewScaleXUpAnimator = ObjectAnimator.ofFloat(mHasMapText.get(id),
                View.SCALE_X, 0, 1).setDuration(ANIMATION_DURATION);
        ObjectAnimator textViewScaleYUpAnimator = ObjectAnimator.ofFloat(mHasMapText.get(id),
                View.SCALE_Y, 0, 1).setDuration(ANIMATION_DURATION);
        ObjectAnimator imageViewTranslateUpAnimator = ObjectAnimator.ofFloat(mHasMapImages.get(id), View.TRANSLATION_Y,
                -Utils.convertDpToPixel(10, getContext()))
                .setDuration(ANIMATION_DURATION);

        //play animation for checked AppCompatImageView
        animatorCheck.playTogether(textViewScaleXUpAnimator,
                textViewScaleYUpAnimator,
                imageViewTranslateUpAnimator
        );

        animatorCheck.addListener(new CheckedAnimatorListener(id));
        animatorCheck.setInterpolator(new DecelerateInterpolator());



        if (lastCheckedId != NONE) {
            //play reverse animation for last checked AppCompatImageView
            ObjectAnimator textViewScaleXDownpAnimator = ObjectAnimator.ofFloat(mHasMapText.get(lastCheckedId), View.SCALE_X, 1, 0).setDuration(move?ANIMATION_DURATION:0);
            ObjectAnimator textViewScaleYDownpAnimator = ObjectAnimator.ofFloat(mHasMapText.get(lastCheckedId), View.SCALE_Y, 1, 0).setDuration(move?ANIMATION_DURATION:0);
            ObjectAnimator imageViewTranslateDownAnimator = ObjectAnimator.ofFloat(mHasMapImages.get(lastCheckedId), View.TRANSLATION_Y, Utils.convertDpToPixel(1, getContext()))
                    .setDuration(move?ANIMATION_DURATION:0);
            animatorUncheck.playTogether(textViewScaleXDownpAnimator,
                    textViewScaleYDownpAnimator,
                    imageViewTranslateDownAnimator
            );
            animatorUncheck.setInterpolator(new AccelerateInterpolator());
            animatorUncheck.start();
        }
        animatorCheck.start();
    }
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.lastCheckedId = this.lastCheckedId;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.lastCheckedId = ss.lastCheckedId;
        moveAnimations(this.lastCheckedId,false);

    }





    private void setTextVisibility(int id, int visibility) {
        switch (id) {
            case R.id.llAround:
                tvAround.setVisibility(visibility);
                break;
            case R.id.llBuscar:
                tvBuscar.setVisibility(visibility);
                break;
            case R.id.llFavorite:
                tvFavorites.setVisibility(visibility);
                break;

        }

    }

    private class CheckedAnimatorListener implements Animator.AnimatorListener {
        int checkId;

        CheckedAnimatorListener(int id) {
            this.checkId = id;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            setTextVisibility(checkId, View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            lastCheckedId = checkId;

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    static class SavedState extends BaseSavedState {
        int lastCheckedId;

         SavedState(Parcelable superState) {
            super(superState);
        }
        private SavedState(Parcel in) {
            super(in);
            this.lastCheckedId = in.readInt();
        }
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.lastCheckedId);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>(){

                    @Override
                    public SavedState createFromParcel(Parcel parcel) {
                        return new SavedState(parcel);
                    }

                    @Override
                    public SavedState[] newArray(int i) {
                        return new SavedState[i];
                    }
                };

    }

}
