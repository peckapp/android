package com.peck.android.managers;

import android.util.Log;

import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;

import java.util.ArrayList;

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

    public Manager<T> initialize(DataSource<T> dSource, final Callback<ArrayList<T>> callback) {

        this.dSource = dSource;

        loadFromDatabase(dSource,
                new Callback<ArrayList<T>>() {
                    @Override
                    public void callBack(ArrayList<T> obj) {
                        callback.callBack(obj);
                    }
                });

        downloadFromServer();
        //TODO: server communication and sync happens here


        //TEST
        T t;
        for (int i = 1; i < 21; i++) {
            t = dSource.generate();
            t.setServerId(i);
            add(t, new Callback<T>() {
                public void callBack(T obj) {
                }
            });
        }

        return this;
    }

    public ArrayList<T> downloadFromServer() {
        return null; //TODO: implement
    }

    public void loadFromDatabase(final DataSource<T> dataSource, final Callback<ArrayList<T>> callback) {
        dataSource.getAll(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj);
            }
        });

    }


    public String tag() {
        return getClass().getName();
    }

    public ArrayList<T> getData() {
        return data;
    }

    public T getByLocalId(int id) {
        //TODO: need to account for current data set not containing wanted item -- throw db req

        if (data.size() == 0) return null;

        for (T i: data) {
            if (i.getLocalId() == id) return i;
        }

        return null;
    }

    public T getByServerId(int id) {

        return null;
    }

    public void add(final T item, final Callback<T> callback) {
        //todo: check to see if we already have an item with this localid
        data.add(item);
        dSource.create(item, new Callback<T>() {
            @Override
            public void callBack(T obj) {
                item.setLocalId(obj.getLocalId());
                callback.callBack(obj);
            }
        });
    }

    public void update(T item) {
        //todo: ensure item has valid id
        for (T i : data) {
            if (i.getLocalId() == (item.getLocalId())) {
                item = i;
                dSource.update(i);
            }
        }
    }



}
