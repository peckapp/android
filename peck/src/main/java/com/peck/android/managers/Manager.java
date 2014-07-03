package com.peck.android.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.DBOperable;
import com.peck.android.network.ServerCommunicator;

import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/12/2014.
 *
 * handles arraylists of models, syncs with the server and the database
 *
 */

public abstract class Manager<T extends DBOperable> {

    public static String tag = "Manager";

    //todo: maybe we want this to be a heap based on localid
    protected final ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource = new DataSource<T>(getParameterizedClass());

    public static <S extends Manager & Singleton> Manager getManager(Class<S> clss) {
        try {
            return (Manager)clss.getMethod("getManager", null).invoke(null, null);}
        catch (Exception e) {
            Log.e(tag, "every implemented manager must be a singleton with a static getManager() method");
            e.printStackTrace();
            return null;
        }
    }

    public Manager<T> initialize(final Callback<ArrayList<T>> callback) {

        loadFromDatabase(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                downloadFromServer(new Callback<ArrayList<T>>() {
                    @Override
                    public void callBack(ArrayList<T> obj) { callback.callBack(obj); }
                }); }});

        return this;
    }


    /**
     *
     * load all items from the database.
     * clears data.
     *
     * @param callback a callback to execute once done loading
     */
    public void loadFromDatabase(final Callback<ArrayList<T>> callback) {
        dSource.getAll(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                data.addAll(obj);
                callback.callBack(obj);
            }
        });

    }

    public void downloadFromServer(final Callback<ArrayList<T>> callback) {
        ServerCommunicator.getAll(getParameterizedClass(), new Callback<ArrayList<T>>() {
            @Override
            public void callBack(final ArrayList<T> objs) {
                for (T i : objs) addFromNetwork(i);
                callback.callBack(objs);
            }
        });
    }

    @NonNull
    public String tag() {
        return getClass().getName();
    }

    @NonNull
    public ArrayList<T> getData() {
        return data;
    }

    @Nullable
    public T getByLocalId(Integer id) {
        for (T i : data) {
            if (!(i.getLocalId() == null) && i.getLocalId().equals(id)) return i;
        }

        return null;
    }

    @Nullable
    public T getByServerId(Integer id) {
        for (T i : data) {
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

    public void addNew(final T item) {
        dSource.create(item, new Callback<Integer>() {
            @Override
            public void callBack(final Integer dbObj) {
                data.add((T)item.setLocalId(dbObj));
                ServerCommunicator.postObject(item, getParameterizedClass(), new Callback<T>() {
                    @Override
                    public void callBack(T obj) {
                        dSource.update(obj);
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
    public void addFromNetwork(final T item) {
        if (getByServerId(item.getServerId()) != null && !getByServerId(item.getServerId()).getUpdated().after(item.getUpdated())) { //if we've got the item and it hasn't been updated more recently than the argument
            dSource.create(item, new Callback<Integer>() {
                @Override
                public void callBack(Integer obj) {
                    item.setLocalId(obj);
                    synchronized (data) {
                        data.remove(item); //we find the object that .equals() item, remove it, and re-add it, so we don't have to update it manually
                        data.add(item);
                    }
                }
            });

        } else {
            dSource.create(item, new Callback<Integer>() {
                @Override
                public void callBack(Integer id) {
                    item.setLocalId(id);
                    data.add(item);
                }
            });
        }
    }

    /**
     *
     * @return the class of the model that this manager handles.
     */

    public Class<T> getParameterizedClass() {
        Class<T> clss = (Class<T>) TypeResolver.resolveRawArgument(Manager.class, getClass());
        return clss;
    }


    /**
     *
     * updates the server and the database with item. this method should not be used to update the manager's data. use add instead.
     *
     * @param item the item to update, definitely has localid
     */
    public void updateFromUser(final T item) {

        if (data.contains(item)) {
            ServerCommunicator.patchObject(item, getParameterizedClass(), new Callback<T>() {
                @Override
                public void callBack(T obj) {
                    item.setServerId(obj.getServerId());
                    item.updated();
                    dSource.update(item);
                }
            });
        } else Log.w(tag(), item.toString() + " isn't in the dataset, so it couldn't be updated");
    }

}
