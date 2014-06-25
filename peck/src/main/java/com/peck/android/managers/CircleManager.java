package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;
import com.peck.android.models.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class CircleManager extends FeedManager<Circle> implements Singleton {

    private static CircleManager circleManager = new CircleManager();

    private CircleManager() {

    }

    @Override
    public FeedManager<Circle> initialize(final FeedAdapter<Circle> adapter, DataSource<Circle> dSource) {
        super.initialize(adapter, dSource);
        Circle p;
        ArrayList<Circle> circles = new ArrayList<Circle>();
        for (int i = 1; i < 21; i++) {
            p = new Circle();
            p.setTitle("Test Circle " + i);
            circles.add(p);
        }
        add(circles, new Callback<Collection<Circle>>() {
            @Override
            public void callBack(Collection<Circle> obj) {
                adapter.notifyDataSetChanged();
            }
        });
        return this;
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

