package com.peck.android.managers;

import android.util.Log;

import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.Singleton;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/12/2014.
 */
public abstract class Manager<T extends DBOperable> {

    public static String tag = "Manager";

    public static Manager getManager(Class<? extends Singleton> clss) {
        try {
            return (Manager)clss.getMethod("getManager", null).invoke(null, null); }
        catch (Exception e) {
            Log.e(tag, "every implemented manager must be a singleton with a getManager() method");
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource;

    public String tag() {
        return getClass().getName();
    }

    public ArrayList<T> getData() {
        return data;
    }

}
