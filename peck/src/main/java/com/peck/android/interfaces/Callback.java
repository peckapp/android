package com.peck.android.interfaces;

import android.support.annotation.Nullable;

/**
 * Created by mammothbane on 6/19/2014.
 */
public interface Callback<T> {

    public class NullCb implements Callback {
        @Override
        public void callBack(@Nullable Object obj) {}
    }


    public void callBack(T obj);

}