package com.peck.android.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;

import com.peck.android.R;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;

import java.util.ArrayList;


public class LocaleActivity extends ActionBarActivity {
    private Locale closest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //TODO: sharedpreferences: check if user has picked a locale before

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                setContentView(R.layout.activity_locale);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                LocaleManager.populate();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyPopulated();
            }
        }.execute();

    }

    private void notifyPopulated() {
        new AsyncTask<Void, Void, Locale>() {
            @Override
            protected Locale doInBackground(Void... voids) {
                LocaleManager.findClosest();
                return null;
            }
        }.execute();
    }

}
