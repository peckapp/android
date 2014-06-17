package com.peck.android.models.postItems;

import android.support.v4.app.Fragment;
import android.view.View;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/17/2014.
 */
public abstract class Post<T> extends DBOperable {

    protected T data;

    public T getData() {
        return data;
    }

    abstract class PostBuilder {

    }

    public void setUpPost(View v) {


    }


}
