package com.peck.android.models;


/**
 * Created by mammothbane on 6/16/2014.
 */
public class Peck extends DBOperable {
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

}
