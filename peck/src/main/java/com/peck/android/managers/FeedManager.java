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
public abstract class FeedManager<T extends DBOperable & SelfSetup & HasFeedLayout> extends Manager<T> {

    //every FeedManager **must** implement a static version of getManager/be a singleton

    protected FeedAdapter<T> adapter;

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public FeedManager<T> initialize(FeedAdapter<T> adapter, DataSource<T> dSource) {
        this.adapter = adapter;
        this.dSource = dSource;

        data = loadFromDatabase(dSource);

        downloadFromServer();

        //TODO: server communication and sync happens here

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
        return items; //TODO: doesn't work, because the method's async.
    }

    public ArrayList<T> downloadFromServer() {
        return null; //TODO: implement
    }


    public FeedManager<T> add(T item) { //use for a single item
        data.add(item);
        //TODO: dSource.create(item);
        adapter.update(data);
        adapter.notifyDataSetChanged();
        return this;
    }

    public FeedManager<T> add(ArrayList<T> items) {
        for (T i : items) {
            data.add(i);
            //TODO: dSource.create
        }
        adapter.update(data);
        adapter.notifyDataSetChanged();
        return this;
    }




}
