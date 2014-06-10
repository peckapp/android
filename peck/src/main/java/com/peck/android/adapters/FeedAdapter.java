package com.peck.android.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.database.DataSource;
import com.peck.android.database.DataSourceHelper;
import com.peck.android.database.EventDataSource;
import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;
import com.peck.android.models.Event;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class FeedAdapter<T extends WithLocal & SelfSetup & HasFeedLayout> extends BaseAdapter {
    private ArrayList<T> objs;
    private GenericFactory factory;
    private Context context;
    private int resourceId;

    public FeedAdapter(Context context, GenericFactory<T> factory) {
        this.context = context;
        this.factory = factory;
        this.resourceId = factory.generate().getResourceId();
    }

    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int i) {
        return objs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return objs.get(i).getLocalId();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceId, null);
        }

        T res = (T)getItem(i);
        res.setUp(view);
        return view;
    }

    public <S extends DataSourceHelper<T>> FeedAdapter<T> load(ArrayList<T> data, final DataSource<T, S> dataSource) {
        //TODO: what else do we want to do here? obviously don't want to load *everything*
        //TODO: sharedpreferences for subscriptions to different things? going to want a filter somewhere
        new AsyncTask<Void, Void, ArrayList<T>>() {
            @Override
            protected ArrayList<T> doInBackground(Void... voids) {
                ArrayList<T> events = new ArrayList<T>();
                try {
                    dataSource.open();
                    events = dataSource.getAll();
                } catch (SQLException e) { e.printStackTrace(); }
                finally {
                    dataSource.close();
                }
                return events;
            }

            @Override
            protected void onPostExecute(ArrayList<T> events) {
                //super.onPostExecute(events);
                notifyDataSetChanged();
            }
        }.execute();
        return this;
    }

    public FeedAdapter<T> removeCompleted() {
        //TODO: implement
        return this;
    }

}