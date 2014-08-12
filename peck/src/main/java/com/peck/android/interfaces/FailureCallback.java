/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.interfaces;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by mammothbane on 8/5/2014.
 *
 * a class to allow success and failure on a callback. failure takes all exceptions.
 * runs on the main thread by default.
 *
 */
public abstract class FailureCallback<T> {
    private Handler myHanlder = new Handler(Looper.getMainLooper());
    private boolean mainThread = true;

    protected abstract void success(T item);
    protected abstract void failure(Throwable cause);

    public FailureCallback<T> onCurrentThread() {
        mainThread = false;
        return this;
    }

    /**
     * called by the method calling back if execution was successful.
     *
     * @param item the item returned
     */
    public final void succeed(final T item) {
        if (mainThread) myHanlder.post(new Runnable() {
            @Override
            public void run() {
                success(item);
            }
        }); else success(item);
    }

    public final void fail(final Throwable t) {
        if (mainThread) myHanlder.post(new Runnable() {
            @Override
            public void run() {
                fail(t);
            }
        }); else failure(t);
    }
}
