package com.peck.android.managers;

import android.os.AsyncTask;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserManager extends FeedManager<User> implements Singleton {

    private static UserManager userManager = new UserManager();
    private static ArrayList<FeedAdapter<User>> adapters = new ArrayList<FeedAdapter<User>>();


    @Override
    public FeedManager<User> initialize(FeedAdapter<User> adapter, DataSource<User> dSource) {
        //we need to override several methods in manager to use the list of adapters we're storing


        return super.initialize(adapter, dSource);
    }

    private void associate() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                CircleManager.getManager().associate(data);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //update everything
            }
        }.execute();
    }

    private UserManager() {

    }

    public static UserManager getManager() {
        return userManager;
    }



}
