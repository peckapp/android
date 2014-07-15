package com.peck.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.peck.android.network.LoginAuthenticator;

/**
 * Created by mammothbane on 7/15/2014.
 */
public class AuthenticatorService extends Service {

    private LoginAuthenticator loginAuthenticator;

    @Override
    public void onCreate() {
        loginAuthenticator = new LoginAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return loginAuthenticator.getIBinder();
    }
}
