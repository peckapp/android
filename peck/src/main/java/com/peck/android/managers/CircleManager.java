package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;
import com.peck.android.models.User;

import java.util.Collection;

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

    protected void associate(Collection<User> users) {
        for ( User u : users ) {
            for ( Circle c: data.values()) {
                //dSource.
            }
        }
    }
}

