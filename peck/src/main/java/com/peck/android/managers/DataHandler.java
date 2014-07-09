package com.peck.android.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

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
    protected static final HashMap<Class<? extends DBOperable>, Pair<ArrayList<? extends DBOperable>, LoadStateWrapper>> data = new HashMap<Class<? extends DBOperable>, Pair<ArrayList<? extends DBOperable>, LoadStateWrapper>>();
    protected static final HashMap<Class<? extends DBOperable>, Bus> buses = new HashMap<Class<? extends DBOperable>, Bus>();
    protected static final HashMap<Class<? extends DBOperable>, DataSource<? extends DBOperable>> dataSources = new HashMap<Class<? extends DBOperable>, DataSource<? extends DBOperable>>();

    public static class InitStart {} //when we start initializing
    public static class InitComplete {} //when we're done initializing

    public static class LoadStateWrapper { LoadState value = LoadState.NOT_LOADED;
        public LoadState getValue() { return value; }
    }

    public enum LoadState { NOT_LOADED, DB_LOADED, LOAD_COMPLETE }

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

    /**
     *
     * for internal use only. returns a reference to the data held in the manager.
     *
     * @param tClass
     * @param <T>
     * @return
     */

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T extends DBOperable> ArrayList<T> getDataRef(Class<T> tClass) {
        return (ArrayList<T>)getPair(tClass).first;
    }

    /**
     *
     * hands back a copy of the data currently contained in the datahandler, not a reference.
     * to modify the data in the handler, use update/add/remove operations.
     *
     * @param tClass
     * @param <T>
     * @return a copy of the data contained in the manager
     */

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> ArrayList<T> getData(Class<T> tClass) {
        return new ArrayList<T>((ArrayList<T>)getPair(tClass).first);
    }

    @NonNull
    public static <T extends DBOperable> LoadStateWrapper getLoadState(Class<T> tClass) {
        return getPair(tClass).second;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T extends DBOperable> Pair<ArrayList<? extends DBOperable>, LoadStateWrapper> getPair(Class<T> tClass) {
        Pair<ArrayList<? extends DBOperable>, LoadStateWrapper> pair;
        synchronized (data) {
            pair = data.get(tClass);
            if (pair == null || pair.first == null) pair = new Pair<ArrayList<? extends DBOperable>, LoadStateWrapper>(new ArrayList<T>(), new LoadStateWrapper());
            data.put(tClass, pair);
        }
        return pair;
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

    public static <T extends DBOperable> void init(final Class<T> tClass) {
        getBus(tClass).post(new InitStart());

        switch (getLoadState(tClass).value) {
            case LOAD_COMPLETE:
                getBus(tClass).post(new InitComplete());
                break;
            case DB_LOADED:
                loadFromDatabase(tClass, new Callback<ArrayList<T>>() {
                    @Override
                    public void callBack(ArrayList<T> obj) {
                        getDataRef(tClass).addAll(obj);
                        getLoadState(tClass).value = LoadState.LOAD_COMPLETE;
                        getBus(tClass).post(new InitComplete());
                    }
                });
                break;
            case NOT_LOADED:
                loadFromDatabase(tClass, new Callback<ArrayList<T>>() {
                    @Override
                    public void callBack(ArrayList<T> obj) {
                        getDataRef(tClass).addAll(obj);
                        getLoadState(tClass).value = LoadState.DB_LOADED;
                        downloadFromServer(tClass, new Callback<ArrayList<T>>() {
                            @Override
                            public void callBack(ArrayList<T> obj) {
                                for (T i : obj) addFromNetwork(tClass, i);
                                getLoadState(tClass).value = LoadState.LOAD_COMPLETE;
                                getBus(tClass).post(new InitComplete());
                            }
                        }, new Callback() {
                            @Override
                            public void callBack(Object obj) {
                                getBus(tClass).post(new InitComplete());
                            }
                        });
                    }
                });
                break;
        }




    }



    /**
     *
     * hand back an arraylist of items loaded from the database.
     * does not merge automatically
     *
     * @param tClass the class of the objects to load
     * @param callback a callback that receives the data list
     */
    public static <T extends DBOperable> void loadFromDatabase(final Class<T> tClass, final Callback<ArrayList<T>> callback) {
        getDataSource(tClass).getAll(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj);
            }
        });
    }

    /**
     *
     * hands back an arraylist of items it downloaded from the server.
     * does not merge automatically.
     *
     * @param tClass the class of the objects to load
     * @param callback a callback that receives the data list
     * @param <T> the type of the objects being loaded
     */
    public static <T extends DBOperable> void downloadFromServer(final Class<T> tClass, final Callback<ArrayList<T>> callback, final Callback failure) {
        ServerCommunicator.getAll(tClass, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(final ArrayList<T> objs) {
                for (T i : objs) addFromNetwork(tClass, i);
                callback.callBack(objs);
            }
        }, failure);
    }

    @NonNull
    public static String tag() {
        return DataHandler.class.getName();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> T getByLocalId(Class<T> tClass, Integer id) {
        for (T i : getDataRef(tClass)) {
            if (!(i.getLocalId() == null) && i.getLocalId().equals(id)) return i;
        }

        return null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> T getByServerId(Class<T> tClass, Integer id) {
        for (T i : getDataRef(tClass)) {
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
                getDataRef(tClass).add((T) item.setLocalId(dbObj));
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
                synchronized (getDataRef(tClass)) {
                    getDataRef(tClass).remove(item); //we find the object that .equals() item, remove it, and re-add it, so we don't have to update it manually
                    getDataRef(tClass).add(item);
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
                    getDataRef(tClass).add(item);
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
        if (getDataRef(tClass).contains(item)) {
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
