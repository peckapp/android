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
import com.peck.android.interfaces.WithLocal;
import com.peck.android.managers.ModelManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends WithLocal & SelfSetup & HasFeedLayout,
        S extends GenericFactory<T>, V extends DataSourceHelper<T>> extends Fragment implements HasTabTag {

    protected FeedAdapter<T> feedAdapter;
    protected DataSource<T, V> dataSource;
    protected ModelManager<T, V> modelManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAdapter();
        congfigureManager();

        getActivity().deleteDatabase(PeckApp.Constants.DATABASE_NAME);



//        Log.e(tag(), (lv == null) ? "list view is null" :
//                (feedAdapter == null) ? "feed adapter null" :
//                        "no problem..?");

    }




    protected void congfigureManager() {
        modelManager = ModelManager.getModelManager(getManagerClass()).initialize(feedAdapter, dataSource); //TODO: loading bar
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new AsyncTask<Void, Void, ListView>() {
            ListView lv = (ListView)getActivity().findViewById(getListViewRes());
            @Override
            protected ListView doInBackground(Void... voids) {
                for (int i = 0; i < PeckApp.Constants.RETRY && (lv == null); i++) {
                    lv = (ListView)getActivity().findViewById(getListViewRes());
                    try {
                        Thread.sleep(PeckApp.Constants.UI_TIMEOUT);
                        //Log.e(tag(), Integer.toString(i));
                    } catch (InterruptedException e) { Log.e(tag(), "was interrupted"); }
                }
                return lv;
            }

            @Override
            protected void onPostExecute(ListView listView) {
                //Log.e(tag(), (listView == null) ? "list view null" : "lv not null");
                //Log.e(tag(), (feedAdapter == null) ? "adapter null" : "adapter not null");

                listView.setAdapter(feedAdapter);
            }

        }.execute();
        return inflater.inflate(getLayoutRes(), container, false);
    }

    public void onResume() {
        feedAdapter.removeCompleted();
        super.onResume();
    }

    protected abstract Feed<T, S, V> setUpAdapter(); //should set adapter and datasource

    protected abstract String tag();

    protected abstract GenericFactory<T> getFactory();

    public abstract int getLayoutRes();

    public abstract int getListViewRes();

    public abstract Class getManagerClass();

}

