package com.peck.android.models;

import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class Peck extends DBOperable implements SelfSetup, HasFeedLayout {
    private String title = "";
    private String text = "";
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public Peck setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Peck setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Peck setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int getResourceId()
    {
        return R.layout.lvitem_peck;
    }

    @Override
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_text)).setText(text);
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
    }
}
