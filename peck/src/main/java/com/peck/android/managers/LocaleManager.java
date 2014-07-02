package com.peck.android.managers;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager extends FeedManager<Locale> implements Singleton, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static LocaleManager manager = new LocaleManager();
    private static Location location;
    private static LocationClient client;
    private static Locale locale;

    private static final String LOCALE_ID = "locale id";
    private static final int RESOLUTION_REQUEST_FAILURE = 9000;

    public static LocaleManager getManager() {
        return manager;
    }

    @Override
    public Manager<Locale> initialize(Callback<ArrayList<Locale>> callback) {
        super.initialize(callback);
        client = new LocationClient(PeckApp.getContext(), manager, manager);
        getLocation();
        return this;
    }

    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        //TEST
        //Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();


        location = client.getLastLocation();
        //Log.d(tag, location.toString());
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        //location = client.getLastLocation();
        Toast.makeText(activeFeed.getActivity(), "Disconnected from location services. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        activeFeed.getActivity(),
                        RESOLUTION_REQUEST_FAILURE);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //activity.showErrorDialog(connectionResult.getErrorCode());
            //TODO: dialog
//            Toast.makeText(activity, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private LocaleManager() {

    }

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    public LocaleManager setLocale(@NonNull Locale l) {
        locale = l;
        SharedPreferences.Editor spEdit = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
        spEdit.putInt(LOCALE_ID, l.getLocalId());
        spEdit.apply();

        return this;
    }


    public static LocaleManager stopLocationServices() {
        client.disconnect();
        return manager;
    }

    public static LocaleManager getLocation() {
        if (!client.isConnected() || client.isConnecting()) {
            client.connect();
        }


        return manager;
    }

    public void calcDistances() {
        if (data.size() > 0) {
            Locale ret = data.get(0);
            for (int i = 0; i < PeckApp.Constants.Location.RETRY; i++) {
                if (location == null) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(tag, "[" + i + "] waiting for location, still null");
                } else {
                    for (Locale l : data) {
                        if (l.calcDist(location).getDist() < ret.getDist()) {
                            ret = l;
                        }
                    }

                    break;
                }
            }
            Log.d(tag, "closest: " + ret.toString());
        } else Log.e(tag(), "locale list is empty");
    }
}

