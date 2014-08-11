package com.peck.android.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.peck.android.PeckApp;

import java.io.IOException;

/**
 * Created by mammothbane on 8/8/2014.
 *
 * a class to register with gcm.
 */
public class GcmRegistrar {
    public static final String GCM_SENDER_ID = "651374007309";
    static GoogleCloudMessaging gcm;
    private final static String PROPERTY_REG_ID = "registration_id";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(PeckApp.getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (context != null) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, context,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Log.i(GcmRegistrar.class.getSimpleName(), "This device is not supported.");
                    context.finish();
                }
            }
            return false;
        }
        return true;
    }

    public static String register() {
        if (checkPlayServices(null)) {
            gcm = GoogleCloudMessaging.getInstance(PeckApp.getContext());
            if (getRegistrationId().isEmpty()) {
                try {
                    String token = gcm.register(GCM_SENDER_ID);
                    getGCMPreferences().edit().putString(PROPERTY_REG_ID, token).putFloat("version", (float)PeckApp.version).apply();
                    return token;
                } catch (IOException e) {
                    Log.e(GcmRegistrar.class.getSimpleName(), "couldn't register.");
                    return null;
                }
            } else return getRegistrationId();
        } else {
            //todo: prompt for valid play services download
            return null;
        }
    }


    private static String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(GcmRegistrar.class.getSimpleName(), "Registration not found.");
            return "";
        }

        return registrationId;
    }


    private static SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return PeckApp.getContext().getSharedPreferences(PeckApp.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }



}
