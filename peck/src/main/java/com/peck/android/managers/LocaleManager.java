package com.peck.android.managers;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.peck.android.activities.LocaleActivity;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager extends Manager<Locale> implements Singleton, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static LocaleManager localeManager = new LocaleManager();
    private static ArrayList<Locale> locales;
    private static Location location;
    private static LocationClient client;

    public static LocaleManager getLocaleManager(LocaleActivity act) {
        client = new LocationClient(act, localeManager, localeManager);
        return localeManager;
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private LocaleManager() {

    }

    public static Location getLocation() {
        return null;

    }

    public static void populate() {

    }

    public static Locale findClosest() {

        return null;
    }





}

