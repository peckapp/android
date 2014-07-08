package com.peck.android.fragments;

import com.peck.android.R;
import com.peck.android.enums.CommentType;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CommentManager;
import com.peck.android.managers.Manager;
import com.peck.android.models.Comment;

import java.util.ArrayList;

import static ch.lambdaj.Lambda.*;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class CommentFeed extends Feed<Comment> {
    private CommentType type;
    private Integer parentId;

    @Override
    public void notifyDatasetChanged() {
        super.notifyDatasetChanged();
        if (type != null && parentId != null) {
        data = new ArrayList<Comment>(select(feedManager.getData(), having(on(Comment.class).getParent().equals(parentId) && type == on(Comment.class).getType())));
        }
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public void setParent(int id) {
        this.parentId = id;
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
