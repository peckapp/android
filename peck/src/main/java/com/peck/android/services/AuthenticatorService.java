package com.peck.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.peck.android.network.PeckAccountAuthenticator;

/**
 * Created by mammothbane on 7/15/2014.
 */
public class AuthenticatorService extends Service {

    private PeckAccountAuthenticator peckAccountAuthenticator;

    @Override
    public void onCreate() {
        peckAccountAuthenticator = new PeckAccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return peckAccountAuthenticator.getIBinder();
    }
}
