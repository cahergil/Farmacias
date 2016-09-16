package com.chernandezgil.farmacias.customwidget;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.chernandezgil.farmacias.ui.activity.MainActivity;

/**
 * Created by Carlos on 26/08/2016.
 */
public  class TouchableWrapper extends FrameLayout {


    private UpdateMapUserClick updateMapUserClick;

    public TouchableWrapper(Context context) {
        super(context);

        try {
            updateMapUserClick = (MainActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UpdateMapUserClick");
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateMapUserClick.onClickMap(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // Map Activity must implement this interface
    public interface UpdateMapUserClick {
        public void onClickMap(MotionEvent event);
    }
}