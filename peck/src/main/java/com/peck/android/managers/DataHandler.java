package com.peck.android.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.models.DBOperable;
import com.peck.android.network.ServerCommunicator;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/12/2014.
 *
 * handles arraylists of models, syncs with the server and the database
 *
 */

public abstract class DataHandler {

    public static String tag = "Manager";

    //todo: maybe we want this to be a heap based on localid
    protected static final HashMap<Class<? extends DBOperable>, ArrayList<? extends DBOperable>> data = new HashMap<Class<? extends DBOperable>, ArrayList<? extends DBOperable>>();
    protected static final HashMap<Class<? extends DBOperable>, Bus> buses = new HashMap<Class<? extends DBOperable>, Bus>();
    protected static final HashMap<Class<? extends DBOperable>, DataSource<? extends DBOperable>> dataSources = new HashMap<Class<? extends DBOperable>, DataSource<? extends DBOperable>>();


    @NonNull
    @SuppressWarnings("unchecked")
    private static <T extends DBOperable> DataSource<T> getDataSource(Class<T> tClass) {
        DataSource<T> dataSource;
        synchronized (dataSources) {
            dataSource = (DataSource<T>) dataSources.get(tClass);
            if (dataSource == null) dataSource = new DataSource<T>(tClass);
            dataSources.put(tClass, dataSource);
        }
        return dataSource;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T extends DBOperable> ArrayList<T> getData(Class<T> tClass) {
        ArrayList<T> ret;
        synchronized (data) {
            ret = (ArrayList<T>) data.get(tClass);
            if (ret == null) ret = new ArrayList<T>();
            data.put(tClass, ret);
        }
        return ret;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T extends DBOperable> Bus getBus(Class<T> tClass) {
        Bus bus;
        synchronized (buses) {
            bus = buses.get(tClass);
            if (bus == null) bus = new Bus();
            buses.put(tClass, bus);
        }
        return bus;
    }

    /**
     *
     * load all items from the database.
     * clears data.
     *
     * @param callback a callback to execute once done loading
     */
    public static <T extends DBOperable> void loadFromDatabase(final Class<T> tClass, final Callback<ArrayList<T>> callback) {
        getDataSource(tClass).getAll(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                getData(tClass).addAll(obj);
                callback.callBack(obj);
            }
        });

    }

    public static <T extends DBOperable> void downloadFromServer(final Class<T> tClass, final Callback<ArrayList<T>> callback) {
        ServerCommunicator.getAll(tClass, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(final ArrayList<T> objs) {
                for (T i : objs) addFromNetwork(tClass, i);
                callback.callBack(objs);
            }
        });
    }

    @NonNull
    public static String tag() {
        return DataHandler.class.getName();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> T getByLocalId(Class<T> tClass, Integer id) {
        for (T i : getData(tClass)) {
            if (!(i.getLocalId() == null) && i.getLocalId().equals(id)) return i;
        }

        return null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> T getByServerId(Class<T> tClass, Integer id) {
        for (T i : getData(tClass)) {
            if (i.getServerId() != null && i.getServerId().equals(id)) return i;
        }
        return null;
    }

    /**
     *
     * add a new item to the dataset, the database, and the server
     *
     * @param item
     */
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> void addNew(final Class<T> tClass, final T item) {
        getDataSource(tClass).create(item, new Callback<Integer>() {
            @Override
            public void callBack(final Integer dbObj) {
                getData(tClass).add((T) item.setLocalId(dbObj));
                getBus(tClass).post(item);
                ServerCommunicator.postObject(item, tClass, new Callback<T>() {
                    @Override
                    public void callBack(T obj) {
                        item.setServerId(obj.getServerId());
                        getDataSource(tClass).update(item);
                    }
                });
            }
        });
    }



    /**
     *
     * add an item from the network; picks up
     * called only for updates from the server
     *
     * @param item the new item
     */
    public static <T extends DBOperable> void addFromNetwork(final Class<T> tClass, final T item) {
        if (getByServerId(tClass, item.getServerId()) != null) {
            if (!getByServerId(tClass, item.getServerId()).getUpdated().after(item.getUpdated())) { //if we've got the item and it hasn't been updated more recently than the argument
                getDataSource(tClass).update(item);
                synchronized (getData(tClass)) {
                    getData(tClass).remove(item); //we find the object that .equals() item, remove it, and re-add it, so we don't have to update it manually
                    getData(tClass).add(item);
                    getBus(tClass).post(item);
                }
            } else {
                ServerCommunicator.patchObject(item, tClass, new Callback.NullCb());
            }
        } else {
            getDataSource(tClass).create(item, new Callback<Integer>() {
                @Override
                public void callBack(Integer obj) {
                    item.setLocalId(obj);
                    getData(tClass).add(item);
                    getBus(tClass).post(item);
                }
            });
        }
    }


    /**
     *
     * updates the server and the database with item. this method should not be used to update the manager's data. use add instead.
     *
     * @param item the item to update, definitely has localid
     */
    public static <T extends DBOperable> void updateFromUser(final Class<T> tClass, final T item) {
        if (getData(tClass).contains(item)) {
            ServerCommunicator.patchObject(item, tClass, new Callback<T>() {
                @Override
                public void callBack(T obj) {
                    item.setServerId(obj.getServerId());
                    item.updated();
                    getBus(tClass).post(item);
                    getDataSource(tClass).update(item);
                }
            });
        } else Log.w(tag(), item.toString() + " isn't in the dataset, so it couldn't be updated");
    }

    public static <T extends DBOperable> void register(Class<T> tClass, Object subscriber) {
        getBus(tClass).register(subscriber);
    }

    public static <T extends DBOperable> void unregister(Class<T> tClass, Object subscriber) {
        getBus(tClass).unregister(subscriber);
    }


}
