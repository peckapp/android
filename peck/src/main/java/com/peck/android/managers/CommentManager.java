package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Comment;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class CommentManager extends FeedManager<Comment> implements Singleton {
    private static CommentManager commentManager = new CommentManager();

    private CommentManager() {}

    public static CommentManager getManager() {
        return commentManager;
    }



}
