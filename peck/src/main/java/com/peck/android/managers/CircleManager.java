package com.peck.android.managers;

import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class CircleManager extends FeedManager<Circle> implements Singleton {

    private static CircleManager circleManager = new CircleManager();

    private CircleManager() {

    }

    public static CircleManager getManager() {
        return circleManager;
    }

    @Override
    public void downloadFromServer(Callback<ArrayList<Circle>> callback) {
        super.downloadFromServer(callback);

    }
}

