package com.peck.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.peck.android.R;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class CirclesListItem extends LinearLayout {
    private GestureDetector gestureDetector;


    public CirclesListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) && gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        findViewById(R.id.hlv_users).dispatchTouchEvent(ev);
        return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }




    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return (Math.abs(distanceY) > Math.abs(distanceX)); }
    }

}
