package com.peck.android.models;

import android.app.Activity;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.enums.CommentType;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class Comment extends DBOperable implements SelfSetup, HasFeedLayout {

    @Expose
    @SerializedName("comment_from")
    private Integer attachedTo;

    @Expose
    @SerializedName("user_id")
    private Integer userId;

    @Expose
    @SerializedName("content")
    private String text;

    @Expose
    @SerializedName("category")
    private CommentType type;


    @Override
    public void setUp(View v, Activity activity) {

    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_comment;
    }
}
