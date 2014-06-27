package com.peck.android.join;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.Manager;
import com.peck.android.models.Circle;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/27/2014.
 */
public class CircleUserJoin extends Join<Circle, User> implements Singleton {
    private static CircleUserJoin join = new CircleUserJoin();
    private CircleUserJoin() {}

    private static CircleUserJoin getJoin() { return join; }

    @Override
    public Manager<Circle> getTManager() {
        return null;
    }

    @Override
    public Manager<User> getRManager() {
        return null;
    }

    @Override
    public int getServerId() {
        return 0;
    }

    @Override
    public DBOperable setServerId(int serverId) {
        return null;
    }
}
