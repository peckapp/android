package com.peck.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class EventTracker extends Service {

    //TODO: notifications, clear old events from db

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    public class LocalBinder extends Binder {
        EventTracker getService() {
            return EventTracker.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
}
