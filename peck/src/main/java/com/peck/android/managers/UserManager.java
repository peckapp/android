package com.peck.android.managers;

import android.os.AsyncTask;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserManager extends Manager<User> implements Singleton {

    private static UserManager userManager = new UserManager();
    private static ArrayList<FeedAdapter<User>> adapters = new ArrayList<FeedAdapter<User>>();

    private void associate(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {



                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //update everything
            }
        }.execute();
    }

    private void associateAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (User user : data)



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
