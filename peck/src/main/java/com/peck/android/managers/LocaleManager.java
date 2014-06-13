package com.peck.android.managers;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.GpsSatellite;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

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
    private static LocaleActivity activity;
    private static ArrayList<Locale> locales;
    private static Location location;
    private static LocationClient client;

    public static LocaleManager getLocaleManager() {
        return localeManager;
    }

    public static LocaleManager initialize(LocaleActivity act) {
        client = new LocationClient(act, localeManager, localeManager);
        activity = act;
        return localeManager;
    }


    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(activity, "Disconnected. Please re-connect.",
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
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    private LocaleManager() {

    }

    public static LocaleManager update() {
        return localeManager;
    }

    public static LocaleManager stopLocationServices() {
        client.disconnect();
        return localeManager;
    }

    public static Location getLocation() {
        return null;

    }

    public static LocaleManager populate() {


        return localeManager;
    }

    public static Locale findClosest() {

        return null;
    }





}

