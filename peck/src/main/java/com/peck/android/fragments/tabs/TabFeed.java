package com.peck.android.fragments.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.peck.android.PeckApp;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.FeedManager;

import java.util.concurrent.CancellationException;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class TabFeed<T extends DBOperable & SelfSetup & HasFeedLayout> extends BaseTab {

    //generics, in order:
    // T: model
    // S: factory for model
    // V: datasource for model
    // Y: manager for model

    protected FeedAdapter<T> feedAdapter;
    protected DataSource<T> dataSource;
    protected FeedManager<T> feedManager;
    protected ListView lv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAdapter();
        congfigureManager();
        //TODO: loading bar

        getActivity().deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME); //TEST: remove before production

    }


    /**
     * call abstract getManagerClass(), returns subclass's manager.
     * pass manager class to Modelmanager's static method,
     * which uses reflection to invoke the manager's singleton get method.
     * set modelManager to the singleton instance of the modelmanager we want.
     */

    @SuppressWarnings("unchecked")
    protected void congfigureManager() {
        feedManager = ((FeedManager<T>) FeedManager.getManager(getManagerClass())).initialize(feedAdapter, dataSource);
    }

    public void onResume() {
        if (lv == null) ((ListView)getActivity().findViewById(getListViewRes())).setAdapter(feedAdapter);;
        feedAdapter.removeCompleted();
        super.onResume();
    }

    public void assignListView() {
        //lv = (ListView)getActivity().findViewById(getListViewRes());
        //lv.setAdapter(feedAdapter);
        new AsyncTask<Void, Void, ListView>() {
            //when we create a view, check for the listview asynchronously
            ListView lv = (ListView)getActivity().findViewById(getListViewRes());
            @Override
            protected ListView doInBackground(Void... voids) {
                long diff = System.nanoTime();
                Log.d(tag(), "executing number " );
                for (int i = 0; i < PeckApp.Constants.Database.RETRY && (lv == null); i++) {
                    Log.d(tag(), "executing number " + i );
                    lv = (ListView)getActivity().findViewById(getListViewRes());
                    try {
                        Thread.sleep(PeckApp.Constants.Database.UI_TIMEOUT);
                    } catch (InterruptedException e) { Log.e(tag(), "thread was interrupted"); }
                    if (lv == null) cancel(false); //if we can't get the listview, throw an exception
                }
                Log.d(tag(), "Completed in " + (System.nanoTime() - diff) + " ns.");
                return lv;
            }

            @Override
            protected void onPostExecute(ListView listView) {

                listView.setAdapter(feedAdapter);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onCancelled(ListView listView) {
                super.onCancelled();
                throw new CancellationException("Couldn't find the listview.\nTried " + PeckApp.Constants.Database.RETRY + " times over a duration of " +
                        Float.toString(((float)PeckApp.Constants.Database.RETRY)*((float)PeckApp.Constants.Database.UI_TIMEOUT)/((float)1000)) + " seconds.");
            }

        };

    }

    protected abstract TabFeed<T> setUpAdapter(); //set adapter and datasource
    public abstract int getListViewRes();

}

