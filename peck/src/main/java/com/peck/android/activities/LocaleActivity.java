package com.peck.android.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;


public class LocaleActivity extends ActionBarActivity {
    private Locale closest;
    private boolean loaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: sharedpreferences: check if user has picked a locale before

        //load all locales into localemanager
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                setContentView(R.layout.activity_locale);
                //start the progress tracker
                findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                LocaleManager.populate();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyMe();
            }
        }.execute();


        //locate the user
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                TextView tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
                tv.setVisibility(View.VISIBLE);
                tv.setText(R.string.pb_loc);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                LocaleManager.getLocation();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyMe();
            }
        }.execute();
    }


    private void notifyMe() {
        if (loaded) {
            new AsyncTask<Void, Void, Void>() {
                TextView tv;
                @Override
                protected void onPreExecute() {
                    tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(R.string.pb_loc);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    LocaleManager.findClosest();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    tv.setVisibility(View.GONE);
                }
            }.execute();
        } else loaded = true;
    }

}
