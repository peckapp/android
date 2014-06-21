package com.peck.android.managers;

import android.util.Log;

import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
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

    public Manager<T> initialize(DataSource<T> dSource, Callback callback) {

        this.dSource = dSource;

        data = loadFromDatabase(dSource, callback);

        downloadFromServer();
        //TODO: server communication and sync happens here

        return this;
    }

    ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource;

    public ArrayList<T> downloadFromServer() {
        return null; //TODO: implement
    }

    public <V extends DBOperable> ArrayList<V> loadFromDatabase(final DataSource<V> dataSource, final Callback callback) {
        /*//META: what else do we want to do here? obviously don't want to loadFromDatabase *everything*
        //META: sharedpreferences for subscriptions to different things? going to want a filter somewhere
        final ArrayList<V> items = new ArrayList<V>();
        new AsyncTask<Void, Void, ArrayList<V>>() {
            @Override
            protected ArrayList<V> doInBackground(Void... voids) {
                try {
                    dataSource.open();
                    dataSource.getAll(items);
                } catch (SQLException e) { e.printStackTrace(); }
                finally {
                    dataSource.close();
                }
                return items;
            }

            @Override
            protected void onPostExecute(ArrayList<V> items) {
                callback.callBack(null);
            }
        }.execute();
        return items; //TODO: doesn't work, because the method's async. */
        return new ArrayList<V>();
    }


    public String tag() {
        return getClass().getName();
    }

    public ArrayList<T> getData() {
        return data;
    }

    public T getById(int id) {
        for (T t : data) {
            if (t.getLocalId() == id) return t;
        }
        return null;
    }

    public Manager<T> add(T item) { //use for a single item
        data.add(item);
        //TODO: dSource.create(item);
        return this;
    }

    public Manager<T> add(ArrayList<T> items) {
        for (T i : items) {
            data.add(i);
            //TODO: dSource.create
        }
        return this;
    }



}
