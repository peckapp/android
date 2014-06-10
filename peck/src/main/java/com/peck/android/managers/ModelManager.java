package com.peck.android.managers;

import android.os.AsyncTask;

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
public class ModelManager<T extends WithLocal & SelfSetup & HasFeedLayout,
        S extends DataSourceHelper<T>> {

    protected FeedAdapter<T> adapter;
    ArrayList<T> data;
    protected DataSource<T, S> dSource;

    public void initialize(FeedAdapter<T> adapter, DataSource<T, S> dSource) {
        this.adapter = adapter;
        this.dSource = dSource;

        loadFromDatabase(dSource, data);
    }

    public <V extends WithLocal, G extends DataSourceHelper<V>>
    void loadFromDatabase(final DataSource<V, G> dataSource, ArrayList<V> delta) {
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
        delta = items;
    }

    void update(ArrayList<T> list) {
        this.data = list;
    }


}
