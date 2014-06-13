package com.peck.android.managers;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.peck.android.PeckApp;
import com.peck.android.activities.LocaleActivity;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager extends FeedManager<Locale> implements Singleton, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static LocaleManager manager = new LocaleManager();
    private static LocaleActivity activity;
    private static ArrayList<Locale> locales = new ArrayList<Locale>();
    private static Location location;
    private static LocationClient client;

    private static final int RESOLUTION_REQUEST_FAILURE = 9000;

    public static LocaleManager getManager() {
        return manager;
    }

    public static LocaleManager initialize(LocaleActivity act) {
        client = new LocationClient(act, manager, manager);
        activity = act;
        getLocation();
        return manager;
    }


    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        //TEST
        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();


        location = client.getLastLocation();
        Log.d(tag, location.toString());
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        //location = client.getLastLocation();
        Toast.makeText(activity, "Disconnected from location services. Please re-connect.",
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
                        activity,
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
            Toast.makeText(activity, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private LocaleManager() {

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

    public static LocaleManager populate() {
        //TEST, TODO
        Locale l;
        Location lo;
        for (int i = 0; i < 10; i++) {
            lo = new Location("test");
            lo.setLongitude((double) i * 9);
            lo.setLatitude((double)i*6);
            l = new Locale().setLocalId(i).setLocation(lo).setName(Integer.toString(i));
            locales.add(l);
        }

        lo = new Location("test");
        lo.setLongitude(-73.11);
        lo.setLatitude(42.702);
        Log.d(tag, lo.toString());
        l = new Locale().setLocalId(50).setLocation(lo).setName("my loc");
        locales.add(l);

        Log.d(tag, locales.toString());
        return manager;
    }

    public static Locale findClosest() {
        for (int i = 0; location == null; i++) {
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            Log.d(tag, "[" + i + "] waiting for location, still null");
        }
        Locale ret = locales.get(0);
        double dist = ret.calcDist(location).getDist();

        for (Locale l : locales) {
            if (l.calcDist(location).getDist() < dist) {
                dist = l.getDist();
                ret = l;
            }
        }

        Log.d(tag, "closest: " + ret.toString());

        return ret;
    }





}

