package com.peck.android.fragments.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.peck.android.PeckApp;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.Singleton;
import com.peck.android.interfaces.WithLocal;
import com.peck.android.managers.ModelManager;

import java.util.concurrent.CancellationException;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<model extends WithLocal & SelfSetup & HasFeedLayout,
        modelFactory extends GenericFactory<model>, dbHelper extends DataSourceHelper<model>, manager extends Singleton> extends Fragment implements HasTabTag {

    //generics, in order:
    // T: model
    // S: factory for model
    // V: datasource for model
    // Y: manager for model

    protected FeedAdapter<model> feedAdapter;
    protected DataSource<model, dbHelper> dataSource;
    protected ModelManager<model, dbHelper> modelManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAdapter();
        congfigureManager();
        //TODO: loading bar


        getActivity().deleteDatabase(PeckApp.Constants.DATABASE_NAME); //TEST: remove before production

    }


    /**
     * call abstract getManagerClass(), returns subclass's manager.
     * pass manager class to Modelmanager's static method,
     * which uses reflection to invoke the manager's singleton get method.
     * set modelManager to the singleton instance of the modelmanager we want.
     */

    @SuppressWarnings("unchecked")
    protected void congfigureManager() {
        modelManager = ((ModelManager<model, dbHelper>)ModelManager.getModelManager(getManagerClass())).initialize(feedAdapter, dataSource);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new AsyncTask<Void, Void, ListView>() {
            //when we create a view, check for the listview asynchronously
            ListView lv = (ListView)getActivity().findViewById(getListViewRes());
            @Override
            protected ListView doInBackground(Void... voids) {
                for (int i = 0; i < PeckApp.Constants.RETRY && (lv == null); i++) {
                    lv = (ListView)getActivity().findViewById(getListViewRes());
                    try {
                        Thread.sleep(PeckApp.Constants.UI_TIMEOUT);
                    } catch (InterruptedException e) { Log.e(tag(), "thread was interrupted"); }
                    if (lv == null) cancel(false); //if we can't get the listview, throw an exception
                }
                return lv;
            }

            @Override
            protected void onPostExecute(ListView listView) {
                //once we have the
                listView.setAdapter(feedAdapter);
            }

            @Override
            protected void onCancelled(ListView listView) {
                super.onCancelled();
                throw new CancellationException("Couldn't find the listview, tried " + PeckApp.Constants.RETRY + " times, over a duration of " +
                        PeckApp.Constants.RETRY*PeckApp.Constants.UI_TIMEOUT + " seconds.");
            }

        }.execute();
        return inflater.inflate(getLayoutRes(), container, false);
    }

    public void onResume() {
        feedAdapter.removeCompleted();
        super.onResume();
    }

    protected abstract Feed<model, modelFactory, dbHelper, manager> setUpAdapter(); //set adapter and datasource

    protected abstract String tag();

    protected abstract GenericFactory<model> getFactory(); //don't remove this method; it gets called in subclasses

    public abstract int getLayoutRes();

    public abstract int getListViewRes();

    public abstract Class<manager> getManagerClass();

}

