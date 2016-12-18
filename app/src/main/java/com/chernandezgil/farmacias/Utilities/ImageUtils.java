package com.chernandezgil.farmacias.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

/**
 * Created by Carlos on 18/12/2016.
 */

public class ImageUtils {


    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    //    @SuppressLint("NewApi")
    public  Bitmap getBitmap(int drawableResId,Context context,int color) {
      //  VectorDrawableCompat vectorDrawable=VectorDrawableCompat.create(getResources(),drawableResId,null);
      //  Drawable vectorDrawable = AppCompatDrawableManager.get().getDrawable(getActivity(), drawableResId);
        Drawable vectorDrawable= ContextCompat.getDrawable(context,drawableResId);
       // vectorDrawable.setColorFilter(color_pharmacy_open, PorterDuff.Mode.SRC_ATOP);
        vectorDrawable.setTint(color);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


}
