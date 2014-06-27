package com.peck.android.join;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.Manager;
import com.peck.android.models.Event;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/27/2014.
 */
public class EventUserJoin extends Join<Event, User> implements Singleton {
    private static EventUserJoin join = new EventUserJoin();

    private EventUserJoin() {

    }

    private static EventUserJoin getJoin() {
        return join;
    }

    @Override
    public Manager<Event> getTManager() {
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
