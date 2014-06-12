package com.peck.android.managers;

import android.content.Context;
import android.location.Location;
import android.util.SparseArray;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleManager extends Manager<Locale> implements Singleton {
    private static LocaleManager localeManager = new LocaleManager();
    private static ArrayList<Locale> locales;
    private static Location location;

    public static LocaleManager getLocaleManager() {
        return localeManager;
    }

    public static LocaleManager getLocaleManager(Context context) {
        return localeManager;
    }


    private LocaleManager() {

    }

    public static void populate() {


    }

    public static Locale findClosest() {

        return null;
    }





}

