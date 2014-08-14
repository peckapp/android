/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peck.android.R;

/**
 * a self-setup event class
 */
public class EventView extends LinearLayout {
    private int type;
    private int svId;
    private String imageUrl;
    private String userImageUrl;

    private TextView title;
    private TextView time;
    private TextView text;
    private ImageView image;

    public EventView(Context context) {
        super(context);
        init(null, 0);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EventView, defStyle, 0);

        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        inflater.inflate(R.layout.lvitem_event, this, true);

        svId = a.getInt(R.styleable.EventView_event_id, -1);
        type = a.getInt(R.styleable.EventView_eventType, -1);
        imageUrl = a.getString(R.styleable.EventView_image_url);
        userImageUrl = a.getString(R.styleable.EventView_user_image_url);
        a.recycle();

        title = ((TextView) findViewById(R.id.tv_title));
        text = ((TextView) findViewById(R.id.tv_text));
        time = ((TextView) findViewById(R.id.tv_time));
        image = ((ImageView) findViewById(R.id.iv_event));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        invalidate();
        requestLayout();
    }

    public int getSvId() {
        return svId;
    }

    public void setSvId(int id) {
        this.svId = id;
        invalidate();
        requestLayout();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        invalidate();
        requestLayout();
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
        invalidate();
        requestLayout();
    }
}
