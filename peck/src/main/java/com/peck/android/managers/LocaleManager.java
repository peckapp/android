package com.peck.android.managers;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.peck.android.PeckApp;
import com.peck.android.activities.LocaleActivity;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager extends FeedManager<Locale> implements Singleton, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static LocaleManager manager = new LocaleManager();
    private static LocaleActivity activity;
    private static Location location;
    private static LocationClient client;
    private static Locale locale;

    private static final String LOCALE_ID = "locale local id";
    private static final int RESOLUTION_REQUEST_FAILURE = 9000;

    public static LocaleManager getManager() {
        return manager;
    }

    public static LocaleManager setActivity(LocaleActivity act) {
        Log.d(getManager().getClass().getName(), "localemanager setActivity");

        client = new LocationClient(act, manager, manager);
        activity = act;
        getLocation();
        return manager;
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
            //TODO: dialog
//            Toast.makeText(activity, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private LocaleManager() {

    }

    public Locale getLocale() throws NullPointerException {
        return getLocale(dSource, activity);
    }

    public Locale getLocale(DataSource<Locale> dataSource, Activity act) {
        if (locale != null) return locale;
        else {
            int i = act.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(LOCALE_ID, 0);
            if (i == 0) return null;
            else {
                dataSource.open();
                Locale ret = dataSource.get(i);
                dataSource.close();
                return ret;
            }
        }
    }

    public LocaleManager setLocale(Locale l) {
        locale = l;
        SharedPreferences.Editor spEdit = activity.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
        spEdit.putInt(LOCALE_ID, l.getLocalId());
        spEdit.commit();

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

    public static LocaleManager populate() {
        //TEST, TODO: database
        final ArrayList<Locale> locales = new ArrayList<Locale>();
        Location lo;
        Locale l;

        for (int i = 0; i < 40; i++) {
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getManager().add(locales);
            }
        });

        Log.d(tag, manager.data.toString());
        return manager;
    }

    public static Locale calcDistances() {
        for (int i = 0; i < PeckApp.Constants.Location.RETRY; i++) {
            if (location == null) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(tag, "[" + i + "] waiting for location, still null");
            } else {
                Locale ret = manager.data.get(0);

                for (Locale l : getManager().data) {
                    if (l.calcDist(location).getDist() < ret.getDist()) {
                        ret = l;
                    }
                }

                Log.d(tag, "closest: " + ret.toString());

                return ret; //return the closest item
            }
        }
        return null;

    }


    public static ArrayList<Locale> returnAll() {
        return manager.data;
    }




}

