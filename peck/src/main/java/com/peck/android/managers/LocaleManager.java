package com.peck.android.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.Nullable;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Singleton;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager implements Singleton {
    private static int locale;
    private static Location location;

    private static final String LOCALE_ID = "locale id";

    private LocaleManager() {}

    public static int getLocale() {
        return locale;
    }

    public static void setLocale(int l) {
        locale = l;
        SharedPreferences.Editor spEdit = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
        spEdit.putInt(LOCALE_ID, l);
        spEdit.apply();
    }

    public static void setLocation(Location location) {
        LocaleManager.location = location;
    }

    @Nullable
    public static Location getLocation() {
        return location;
    }

}

