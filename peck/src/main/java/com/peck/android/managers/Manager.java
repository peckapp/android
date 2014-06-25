package com.peck.android.managers;

import android.util.Log;

import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/12/2014.
 */
public abstract class Manager<T extends DBOperable> {

    public static String tag = "Manager";
    protected ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource;

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

    public HashMap<Integer, T> downloadFromServer() {
        return null; //TODO: implement
    }

    public <V extends DBOperable> HashMap<Integer, V> loadFromDatabase(final DataSource<V> dataSource, final Callback callback) {
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
        return items; //TODO: doesn't work because the method's async. */
        callback.callBack(null);
        return new HashMap<Integer, V>();
    }


    public String tag() {
        return getClass().getName();
    }

    public HashMap<Integer, T> getData() {
        return data;
    }

    public T getById(int id) {
        //TODO: need to account for current data set not containing wanted item -- throw db req
        return data.get(id);
    }

    public void add(T item, final Callback<T> callback) {
        //todo: check to see if we already have an item with this localid

        dSource.create(item, new Callback<T>() {
            @Override
            public void callBack(T obj) {
                data.put(obj.getLocalId(), obj);
                callback.callBack(obj);
            }
        });
    }

    public void add(Collection<T> items, final Callback<Collection<T>> callback) {
        dSource.createMult(items, new Callback<Collection<T>>() {
            @Override
            public void callBack(Collection<T> obj) {
                for (T i : obj) {
                    data.put(i.getLocalId(), i);
                }
                callback.callBack(obj);
            }
        });


    }


    public void update(T item) {
        if (item.getLocalId() >= 0 && data.keySet().contains(item.getLocalId())) {
            dSource.update(item);
        } else {
            add(item, new Callback<T>() { public void callBack(T obj) {update(obj);} });

        }

    }



}
