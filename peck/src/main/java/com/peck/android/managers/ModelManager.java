package com.peck.android.managers;

import android.graphics.AvoidXfermode;
import android.os.AsyncTask;
import android.util.Log;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public abstract class ModelManager<T extends WithLocal & SelfSetup & HasFeedLayout,
        S extends DataSourceHelper<T>> {

    //every modelmanager **must** implement a static version of getManager and be a singleton

    protected FeedAdapter<T> adapter;
    ArrayList<T> data = new ArrayList<T>();
    protected DataSource<T, S> dSource;
    public final static String tag = "ModelManager";

    public static ModelManager getModelManager(Class clss) {
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

    public ModelManager<T, S> initialize(FeedAdapter<T> adapter, DataSource<T, S> dSource) {
        this.adapter = adapter;
        this.dSource = dSource;

        data = loadFromDatabase(dSource);

        downloadFromServer();//TODO: server communication and project sync happens here

        adapter.update(data);

        return this;
    }

    public <V extends WithLocal, G extends DataSourceHelper<V>>
    ArrayList<V> loadFromDatabase(final DataSource<V, G> dataSource) {
        //TODO: what else do we want to do here? obviously don't want to loadFromDatabase *everything*
        //TODO: sharedpreferences for subscriptions to different things? going to want a filter somewhere
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

    public ModelManager<T, S> add(T item) {
        data.add(item);
        adapter.update(data);
        adapter.notifyDataSetChanged();
        return this;
    }


}
