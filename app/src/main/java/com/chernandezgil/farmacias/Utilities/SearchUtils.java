package com.chernandezgil.farmacias.Utilities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.chernandezgil.farmacias.ui.activity.MainActivity;

/**
 * Created by Carlos on 08/09/2016.
 */
public class SearchUtils {

    public static void setUpAnimations(Activity activity, final CardView cardView, final View view, RecyclerView listView) {

        MainActivity myActivity = (MainActivity) activity;
        Context context = activity.getApplicationContext();

        ObjectAnimator fade_in = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fade_in.setDuration(200);
        ObjectAnimator fade_out = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fade_out.setDuration(200);

        if (cardView.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(cardView,
                        cardView.getWidth() - (int) Utils.convertDpToPixel(56, context),
                        (int) Utils.convertDpToPixel(23, context),
                        (float) Math.hypot(cardView.getWidth(), cardView.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                       cardView.setVisibility(View.GONE);
                        //the following statement will only apply when the view used to set the light mode be invisible,
                        //that is dissapears from the screen
                        Utils.clearLightStatusBar(myActivity);
                       //  Utils.clearNick(cardView);
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

           // toolbarMain.setBackgroundColor(Color.WHITE);
//            toolbarMain.getMenu().clear();
//            toolbarMain.setNavigationIcon(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                final Animator animatorShow = ViewAnimationUtils.createCircularReveal(cardView,
                        cardView.getWidth() - (int) Utils.convertDpToPixel(56, context),
                        (int) Utils.convertDpToPixel(23, context),
                        0,
                        (float) Math.hypot(cardView.getWidth(), cardView.getHeight()));
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
                cardView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);

                if (cardView.getVisibility() == View.VISIBLE) {
                    animatorShow.setDuration(300);
                    animatorShow.start();
                    cardView.setEnabled(true);
                }

            } else {
                cardView.setVisibility(View.VISIBLE);
                cardView.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }


}



