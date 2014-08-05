package com.peck.android.interfaces;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by mammothbane on 8/5/2014.
 *
 * a class to allow success and failure on a callback. failure handles any exceptions.
 * runs on the main thread by default.
 *
 */
public abstract class FailureCallback<T> {
    private Handler myHanlder = new Handler(Looper.getMainLooper());
    private boolean mainThread = true;

    protected abstract void success(T item);
    protected abstract void failure(Throwable t);

    public FailureCallback<T> onThisThread() {
        mainThread = false;
        return this;
    }

    public void succeed(final T item) {
        if (mainThread) myHanlder.post(new Runnable() {
            @Override
            public void run() {
                success(item);
            }
        }); else success(item);
    }

    public void fail(final Throwable t) {
        if (mainThread) myHanlder.post(new Runnable() {
            @Override
            public void run() {
                fail(t);
            }
        }); else fail(t);
    }
}
