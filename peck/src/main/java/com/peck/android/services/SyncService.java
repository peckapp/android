package com.peck.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.peck.android.network.PeckSyncAdapter;

/**
 * Created by mammothbane on 7/15/2014.
 */
public class SyncService extends Service {

    private static PeckSyncAdapter adapter = null;

    private static final Object adapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (adapterLock) {
            if (adapter == null) {
                adapter = new PeckSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
