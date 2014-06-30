package com.peck.android.managers;

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
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
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
    private final static Object retLock = new Object();
    private static DataSource<Locale> dataSource = new DataSource<Locale>(new Locale());

    private static final String LOCALE_ID = "locale id";
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

    @Override
    public FeedManager<Locale> initialize(FeedAdapter<Locale> adapter, DataSource<Locale> dSource) {
        super.initialize(adapter, dSource);

        if (locale == null) {
            int i = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(LOCALE_ID, -1);

            if (i != -1) dataSource.get(i, new Callback<Locale>() {
                @Override
                public void callBack(Locale obj) {
                    locale = obj;
                }
            });
        }

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

    public Locale getLocale() {
        return locale;
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

    public void populate() {
        //TEST
        Location lo;
        Locale l;

        data = new ArrayList<Locale>();

        for (int i = 0; i < 40; i++) {
            lo = new Location("test");
            lo.setLongitude((double) i * 9);
            lo.setLatitude((double) i * 6);
            l = new Locale().setServerId(i).setLocation(lo).setName(Integer.toString(i));
            add(l, new Callback<Locale>() {
                @Override
                public void callBack(Locale obj) {

                }
            });
        }

        lo = new Location("test");
        lo.setLongitude(-73.11);
        lo.setLatitude(42.702);
        Log.d(tag, lo.toString());
        l = new Locale().setServerId(50).setLocation(lo).setName("my loc");
        add(l, new Callback<Locale>() {
            @Override
            public void callBack(Locale obj) {

            }
        });

    }

    public void calcDistances() {
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

    }
}

