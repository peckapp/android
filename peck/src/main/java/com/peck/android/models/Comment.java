package com.peck.android.models;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.makeramen.RoundedImageView;
import com.peck.android.R;
import com.peck.android.enums.CommentType;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.UserManager;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class Comment extends DBOperable implements SelfSetup, HasFeedLayout {

    @Expose
    @SerializedName("comment_from")
    private Integer parent;

    @Expose
    @SerializedName("user_id")
    private Integer userId;

    @Expose
    @SerializedName("content")
    private String text = "";

    @Expose
    @SerializedName("category")
    private CommentType type;

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    @Override
    public void setUp(final View v) {
        User user = UserManager.getManager().getByServerId(userId);
        if (user == null) {} //throw an exception/force an update
        else {
            ((TextView) v.findViewById(R.id.tv_text)).setText(text);
            ((TextView) v.findViewById(R.id.tv_title)).setText(user.getFullName());
            user.getProfilePicture(new Callback<Bitmap>() {
                @Override
                public void callBack(Bitmap obj) {
                    ((RoundedImageView) v.findViewById(R.id.riv_user)).setImageBitmap(obj);
                }
            });
        }
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_comment;
    }
}
