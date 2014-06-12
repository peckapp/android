package com.peck.android.managers;

import android.os.AsyncTask;
import android.util.Log;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.Singleton;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public abstract class ModelManager<T extends DBOperable & SelfSetup & HasFeedLayout> {

    //every modelmanager **must** implement a static version of getManager and be a singleton

    protected FeedAdapter<T> adapter;
    ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T> dSource;
    public final static String tag = "ModelManager";

    public static ModelManager getModelManager(Class<? extends Singleton> clss) {
        try {
            return (ModelManager)clss.getMethod("getManager", null).invoke(null, null); }
        catch (Exception e) {
            Log.e(tag, "every implemented manager must be a singleton with a getManager() method");
            e.printStackTrace();
            return null;
        }
    }

    ModelManager() {

    }

    public ModelManager<T> initialize(FeedAdapter<T> adapter, DataSource<T> dSource) {
        this.adapter = adapter;
        this.dSource = dSource;

        data = loadFromDatabase(dSource);

        downloadFromServer();//TODO: server communication and sync happens here

        adapter.update(data);

        return this;
    }

    public <V extends DBOperable> ArrayList<V> loadFromDatabase(final DataSource<V> dataSource) {
        //META: what else do we want to do here? obviously don't want to loadFromDatabase *everything*
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
                adapter.notifyDataSetChanged();
            }
        }.execute();
        return items;
    }

    public ArrayList<T> downloadFromServer() {
        return null; //TODO: implement
    }

    public ArrayList<T> getData() {
        return data;
    }

    public ModelManager<T> add(T item) {
        data.add(item);
        dSource.create(item);
        adapter.update(data);
        adapter.notifyDataSetChanged();
        return this;
    }


}
