/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
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
 *
 * @since 1.0
 * @author mammothbane
 */
public class GcmRegistrar {
    public static final String GCM_SENDER_ID = "651374007309";
    static GoogleCloudMessaging gcm;
    private final static String PROPERTY_REG_ID = "registration_id";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * check to see if google play services are installed. notify the given activity if not.
     * @param context display an alertdialog here if GPS aren't installed. can be null.
     * @return true if google play services are available. false if not.
     * @since 1.0
     */
    public static boolean checkPlayServices(@Nullable Activity context) {
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

    /**
     * blocking method to register with gcm servers.
     *
     * @return the registration token received from gcm. null on failure.
     * @since 1.0
     */
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

    /**
     * get the current registration id. makes no network access.
     * @return whatever's in the local shared preferences. empty string if nothing present.
     * @since 1.0
     */
    private static String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        if (prefs.getFloat("version", 0f) < PeckApp.version) { //if we've upgraded, re-register. gcm might not work/work the same on a new version
            prefs.edit().putString(PROPERTY_REG_ID, "").apply();
            return "";
        }
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(GcmRegistrar.class.getSimpleName(), "Registration not found.");
            return "";
        }

        return registrationId;
    }


    /**
     * @return the sharedprefs for gcm
     * @since 1.0
     */
    private static SharedPreferences getGCMPreferences() {
        return PeckApp.getContext().getSharedPreferences(PeckApp.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }



}
