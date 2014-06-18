package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class CircleManager extends FeedManager<Circle> implements Singleton {

    private CircleManager circleManager = new CircleManager();

    private CircleManager() {

    }

    public CircleManager getCircleManager() {
        return circleManager;
    }


}
