package com.peck.android.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.android.volley.VolleyError;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.models.DBOperable;
import com.peck.android.network.ServerCommunicator;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

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
    protected static final HashMap<Class<? extends DBOperable>, Pair<ArrayList<Integer>, ArrayList<Integer>>> pending =
            new HashMap<Class<? extends DBOperable>, Pair<ArrayList<Integer>, ArrayList<Integer>>>();

    public static class InitStart {} //when we start initializing
    public static class InitComplete {} //when we're done initializing

    public static class LoadStateWrapper { LoadState value = LoadState.NOT_LOADED;
        public LoadState getValue() { return value; }
    }

    public enum LoadState { NOT_LOADED, LOADING_NOW, DB_LOADED, LOAD_COMPLETE }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> DataSource<T> getDataSource(Class<T> tClass) {
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
     * @param tClass the class the data belongs to
     * @return reference to the data the manager holds
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
     * @param tClass the class the data belongs to
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
            if (bus == null) bus = new Bus(ThreadEnforcer.ANY);
            buses.put(tClass, bus);
        }
        return bus;
    }

    @NonNull
    private static <T extends DBOperable> Pair<ArrayList<Integer>, ArrayList<Integer>> getPending(Class<T> tClass) {
        Pair<ArrayList<Integer>, ArrayList<Integer>> pair;
        synchronized (pending) {
            pair = pending.get(tClass);
            if (pair == null) pair = new Pair<ArrayList<Integer>, ArrayList<Integer>>(new ArrayList<Integer>(), new ArrayList<Integer>());
            pending.put(tClass, pair);
        }
        return pair;
    }

    @NonNull
    public static <T extends DBOperable> ArrayList<Integer> getPendingServerIds(Class<T> tClass) {
        return getPending(tClass).second;
    }

    @NonNull
    public static <T extends DBOperable> ArrayList<Integer> getPendingLocalIds(Class<T> tClass) {
        return getPending(tClass).first;
    }


    /**
     *
     * forgiving initializer method. anyone can call this, and it will broadcast initstart
     * and initcomplete to the bus of tClass. updates tClass's persistent loadstate as it works.
     *
     * @param tClass the class being operated upon
     */

    public static <T extends DBOperable> void init(final Class<T> tClass) {
        synchronized (getLoadState(tClass).value) {
            switch (getLoadState(tClass).value) {
                case LOADING_NOW:
                    break;
                case LOAD_COMPLETE:
                    getBus(tClass).post(new InitStart());
                    getBus(tClass).post(new InitComplete());
                    downloadFromServer(tClass, new Callback<ArrayList<T>>() {
                        @Override
                        public void callBack(ArrayList<T> obj) {
                            for (T i : obj) put(tClass, i, false);
                        }
                    }, new Callback.NullCb());
                    break;
                case DB_LOADED:
                    getBus(tClass).post(new InitStart());
                    getLoadState(tClass).value = LoadState.LOADING_NOW;
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
                    getBus(tClass).post(new InitStart());
                    getLoadState(tClass).value = LoadState.LOADING_NOW;
                    loadFromDatabase(tClass, new Callback<ArrayList<T>>() {
                        @Override
                        public void callBack(ArrayList<T> obj) {
                            getDataRef(tClass).addAll(obj);
                            getLoadState(tClass).value = LoadState.DB_LOADED;
                            downloadFromServer(tClass, new Callback<ArrayList<T>>() {
                                @Override
                                public void callBack(ArrayList<T> obj) {
                                    for (T i : obj) put(tClass, i, false);
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
                for (T i : objs) put(tClass, i, false);
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
        synchronized (getDataRef(tClass)) {
            for (T i : getDataRef(tClass)) {
                if (!(i.getLocalId() == null) && i.getLocalId().equals(id)) return i;
            }
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> T getByServerId(Class<T> tClass, Integer id) {
        synchronized (getDataRef(tClass)) {
            for (T i : getDataRef(tClass)) {
                if (i.getServerId() != null && i.getServerId().equals(id)) return i;
            }
        }
        return null;
    }


    /**
     *
     * add or update the object passed in. propagates changes to the data stored in this handler,
     * the database, and the server.
     *
     *
     * @param tClass the class of the item being updated
     * @param item the item
     * @param serverUpdate determines whether the item will be patched up to the server. has no effect if the item does not have a serverId.
     */

    @SuppressWarnings("unchecked")
    public static <T extends DBOperable> void put(final Class<T> tClass, final T item, final boolean serverUpdate) {
        if (item.getLocalId() == null) {
            if (item.getServerId() == null) {
                getDataSource(tClass).save(item, new Callback<Integer>() {
                    @Override
                    public void callBack(final Integer dbObj) {
                        item.setLocalId(dbObj);
                        getDataRef(tClass).add(item);
                        getBus(tClass).post(item);
                        ServerCommunicator.postObject(item, tClass, new Callback<T>() {
                            @Override
                            public void callBack(T obj) {
                                item.setServerId(obj.getServerId());
                                getDataSource(tClass).save(item, new NullCb());
                                item.pending(false);
                            }
                        }, new Callback<VolleyError>() {
                            @Override
                            public void callBack(VolleyError obj) {
                                item.pending(false);
                            }
                        });
                    }
                });

            } else {
                getDataSource(tClass).save(item, new Callback<Integer>() {
                    @Override
                    public void callBack(@NonNull Integer obj) {
                        item.setLocalId(obj);
                        if (getByServerId(tClass, item.getServerId()) == null || getByServerId(tClass, item.getServerId()).getUpdated().before(item.getUpdated())) {
                            synchronized (getDataRef(tClass)) {
                                getDataRef(tClass).remove(item);
                                getDataRef(tClass).add(item);
                            }
                        }
                        if (serverUpdate) {
                            ServerCommunicator.patchObject(getByServerId(tClass, item.getServerId()), tClass, new Callback<T>() {
                                @Override
                                public void callBack(T obj) {
                                    item.pending(false);
                                }
                            }, new Callback<VolleyError>() {
                                @Override
                                public void callBack(VolleyError obj) {
                                    item.pending(false);
                                }
                            });
                        } else {
                            getBus(tClass).post(item);
                            item.pending(false);
                        }
                    }
                });
            }

        } else {

            if (item.getServerId() == null) {
                ServerCommunicator.postObject(item, tClass, new Callback<T>() {
                    @Override
                    public void callBack(T obj) {
                        item.setServerId(obj.getServerId());
                        getDataSource(tClass).save(item, new NullCb());
                        item.pending(false);
                    }
                }, new Callback<VolleyError>() {
                    @Override
                    public void callBack(VolleyError obj) {
                        item.pending(false);
                    }
                });

            } else { //item has both ids
                getDataSource(tClass).save(item, new Callback.NullCb());
                if (serverUpdate) {
                    ServerCommunicator.patchObject(item, tClass, new Callback<T>() {
                        @Override
                        public void callBack(T obj) {
                            item.pending(false);
                        }
                    }, new Callback<VolleyError>() {
                        @Override
                        public void callBack(VolleyError obj) {
                            item.pending(false);
                        }
                    });
                } else item.pending(false);
            }
        }
    }


    public static <T extends DBOperable> void register(Class<T> tClass, Object subscriber) {
        getBus(tClass).register(subscriber);
    }

    public static <T extends DBOperable> void unregister(Class<T> tClass, Object subscriber) {
        getBus(tClass).unregister(subscriber);
    }


}
