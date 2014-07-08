package com.peck.android.fragments;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.Manager;
import com.peck.android.models.Comment;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class CommentFeed extends Feed<Comment> {

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
        return null;
    }
}
