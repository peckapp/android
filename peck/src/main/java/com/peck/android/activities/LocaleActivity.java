package com.peck.android.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.fragments.LocaleSelectionFeed;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;


public class LocaleActivity extends FragmentActivity {
    private Locale closest;
    private boolean loaded = false;
    private static final String TAG = "LocaleActivity";
    private LocaleSelectionFeed lsf = new LocaleSelectionFeed();
    private static final String fragmentTag = "locale selection feed";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocaleManager.initialize(this);
        setContentView(R.layout.activity_locale);

        //load all locales into localemanager
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
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

        //TODO: check if google play services are enabled, skip all of this if they're not
    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onStop() {
        super.onStop();
        LocaleManager.stopLocationServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleManager.getLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void notifyMe() {
        if (loaded) {
            //Log.d(TAG, "notified twice");
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
                    closest = LocaleManager.findClosest();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    tv.setVisibility(View.GONE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);

                    boolean b = false;

                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

                    try {
                        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) b = true; }
                    catch ( Exception e ) { }

                    if (b) trans.attach(lsf);
                    else trans.add(R.id.rl_loc_select, lsf, fragmentTag);

                    trans.commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    for (Locale l : LocaleManager.returnAll()) {
                        LocaleManager.getManager().add(l);
                    }

                    ((ListView)findViewById(new LocaleSelectionFeed().getListViewRes())).setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    LocaleManager.getManager().setLocale((Locale) lsf.getAdapter().getItem(i));
                                    Log.d(getClass().getName(),
                                            (lsf.getAdapter().getItem(i)).toString());
//                                    Intent intent = new Intent(LocaleActivity.this, FeedActivity.class);
//                                    startActivity(intent);
                                    finish();
                                }
                            }
                    );


                }
            }.execute();
        } else {
            loaded = true;
        }
    }

}
