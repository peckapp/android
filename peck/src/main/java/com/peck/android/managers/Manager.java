package com.peck.android.managers;

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
    protected ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource;

    public static Manager getManager(Class<? extends Singleton> clss) {
        try {
            return (Manager)clss.getMethod("getManager", null).invoke(null, null);}
        catch (Exception e) {
            Log.e(tag, "every implemented manager must be a singleton with a getManager() method");
            e.printStackTrace();
            return null;
        }
    }

    public Manager<T> initialize(DataSource<T> dSource, final Callback<ArrayList<T>> callback) {

        this.dSource = dSource;

        loadFromDatabase(dSource,
                new Callback<ArrayList<T>>() {
                    @Override
                    public void callBack(ArrayList<T> obj) {

                        downloadFromServer(new Callback<ArrayList<T>>() {
                            @Override
                            public void callBack(ArrayList<T> obj) {


                                callback.callBack(obj);
                            }
                        });
                    }
                });


        //TODO: server communication and sync happens here


        //TEST

       /* T t;
        for (int i = 1; i < 10; i++) {
            try {
                t = dSource.generate();
                t.setServerId(i);
                addNetwork(t, new Callback<T>() {
                    public void callBack(T obj) {
                    }
                });
            } catch (Exception e) {
                Log.e(tag(), "all dboperables must have public, nullary constructors\n" + e.toString());
            }
        }*/

        return this;
    }

    public void loadFromDatabase(final DataSource<T> dataSource, final Callback<ArrayList<T>> callback) {
        dataSource.getAll(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj);
            }
        });

    }

    public void downloadFromServer(final Callback<ArrayList<T>> callback) {
        ServerCommunicator.getAll(getParameterizedClass(), new Callback<ArrayList<T>>() {
            @Override
            public void callBack(final ArrayList<T> objs) {
                for (T i : objs) addNetwork(i, new Callback<T>() {
                    @Override
                    public void callBack(T obj) {

                    }
                });
                callback.callBack(objs);
            }
        });
    }


    public String tag() {
        return getClass().getName();
    }

    public ArrayList<T> getData() {
        return data;
    }

    @Nullable
    public T getByLocalId(Integer id) {
       for (T i: data) {
            if (i.getLocalId() == id) return i;
        }

        return null;
    }

    public T getByServerId(int id) {
        //TODO: throw db request

        for (T i : data) {
            if (i.getServerId() == id) return i;
        }

        return null;
    }

    /**
     *
     * add a new item to the dataset, the database, and the server
     *
     * @param item the new item
     */

    public void add(final T item) {
        data.add(item);
        dSource.create(item, new Callback<T>() {
            @Override
            public void callBack(T obj) {
                item.setLocalId(obj.getLocalId());
            }
        });
    }



    /**
     *
     * add an item from the network
     * called only for updates from the server
     *
     * @param item the new item
     * @param callback a callback for when the update is finished
     */
    public void addNetwork(final T item, final Callback<T> callback) {
        if (data.contains(item)) update(item);
        else {
            data.add(item);
            dSource.create(item, new Callback<T>() {
                @Override
                public void callBack(T obj) {
                    item.setLocalId(obj.getLocalId());
                    callback.callBack(obj);
                }
            });
        }
    }

    /**
     *
     * @return the class of the model that this manager handles.
     */

    public Class<T> getParameterizedClass() {
        return (Class<T>)TypeResolver.resolveRawArgument(Manager.class, getClass());
    }


    /**
     *
     * updates the dataset with item.
     *
     * this method should get called iff the object is already in the list.
     * primarily for user-sourced updates.
     *
     * @param item the item to update, definitely has localid
     */
    public void update(final T item) {
        if (data.contains(item) && !data.get(data.indexOf(item)).getUpdated().after(item.getUpdated())) { //if we've got the item, and if it hasn't been updated more recently than the argument
            data.remove(item);
            data.add(item);
            if (item.getServerId() != null) dSource.update(item);
            else ServerCommunicator.postObject(item, getParameterizedClass(), new Callback<T>() {
                @Override
                public void callBack(T obj) {
                    item.setServerId(obj.getServerId());
                    item.updated();
                    dSource.update(item);
                }
            });
        } else Log.w(tag(), item.toString() + "isn't in the dataset.");
    }

}
