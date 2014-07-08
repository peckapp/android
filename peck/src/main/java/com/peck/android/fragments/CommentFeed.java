package com.peck.android.fragments;

import com.peck.android.R;
import com.peck.android.enums.CommentType;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CommentManager;
import com.peck.android.managers.Manager;
import com.peck.android.models.Comment;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class CommentFeed extends Feed<Comment> {
    private CommentType type;
    private int id;

    @Override
    public void notifyDatasetChanged() {
        super.notifyDatasetChanged();
        feedManager.getData()
    }

    public void setType(CommentType type) {
        this.type = type;
        notifyDatasetChanged();
    }

    public void setParent(int id) {
        this.id = id;
    }

    @Override
    public int getListViewRes() {
        return R.id.lv_comments;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.feed_comment;
    }

    @Override
    public <S extends Manager & Singleton> Class<S> getManagerClass() {
        return (Class<S>)CommentManager.class;
    }
}
