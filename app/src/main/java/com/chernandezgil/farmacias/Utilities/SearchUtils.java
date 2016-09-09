package com.chernandezgil.farmacias.Utilities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Carlos on 08/09/2016.
 */
public class SearchUtils {

    public static void setUpAnimations(Context context, final CardView search, final View view, RecyclerView listView) {


        ObjectAnimator fade_in = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fade_in.setDuration(200);
        ObjectAnimator fade_out = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fade_out.setDuration(200);

        if (search.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(search,
                        search.getWidth() - (int) Util.convertDpToPixel(56, context),
                        (int) Util.convertDpToPixel(23, context),
                        (float) Math.hypot(search.getWidth(), search.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        search.setVisibility(View.GONE);
                        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                        listView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorHide.setDuration(300);
                animatorHide.start();

            }
        } else {
//            toolbarMain.setTitle("");
//            toolbarMain.getMenu().clear();
//            toolbarMain.setNavigationIcon(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                final Animator animatorShow = ViewAnimationUtils.createCircularReveal(search,
                        search.getWidth() - (int) Util.convertDpToPixel(56, context),
                        (int) Util.convertDpToPixel(23, context),
                        0,
                        (float) Math.hypot(search.getWidth(), search.getHeight()));
                animatorShow.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                search.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);

                if (search.getVisibility() == View.VISIBLE) {
                    animatorShow.setDuration(300);
                    animatorShow.start();
                    search.setEnabled(true);
                }

            } else {
                search.setVisibility(View.VISIBLE);
                search.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }


}



