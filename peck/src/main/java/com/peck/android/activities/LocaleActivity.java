package com.peck.android.activities;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.peck.android.R;
import com.peck.android.fragments.LocaleSelectionFeed;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;


public class LocaleActivity extends PeckActivity {
    private boolean loaded = false;
    private boolean locationServices = true;
    private static final String TAG = "LocaleActivity";
    private static final String fragmentTag = "locale selection feed";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocaleManager.initialize(this);
        setContentView(R.layout.activity_locale);

        //TODO: check if google play services are enabled, skip all of this if they're not
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!(servicesConnected() && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))) { //if we can't locate the user, for some reason
                //todo: catch errors from google play services
            locationServices = false;
            notifyMe();
            //todo: re-search when location services come up?
            //todo: search bar?

        } else {

            //locate the user
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    TextView tv = (TextView) findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
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

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {

            return false;
        }
    }

    private void notifyMe() {
        if (loaded) {
            //Log.d(TAG, "notified twice");

            if (locationServices) {
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
                    LocaleManager.calcDistances(); //this only gets called if we know where the user is *and* have the location list loaded
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    tv.setVisibility(View.GONE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);

                    addListFragment();

                }
            }.execute(); } else {
                findViewById(R.id.rl_locale).setVisibility(View.GONE);
                Toast.makeText(this, "Can't find you, please pick your location.", Toast.LENGTH_SHORT).show();
                addListFragment();
            }
        } else {
            loaded = true;
        }
    }

    private void addListFragment() {
        boolean b = false;

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

        try {
            if (getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) b = true; }
        catch ( Exception e ) { }

        if (b) trans.attach(new LocaleSelectionFeed());
        else trans.add(R.id.rl_loc_select, new LocaleSelectionFeed(), fragmentTag);

        trans.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();

        for (Locale l : LocaleManager.returnAll()) {
            LocaleManager.getManager().add(l);
        }

        ((ListView)findViewById(new LocaleSelectionFeed().getListViewRes())).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        LocaleSelectionFeed lsf = (LocaleSelectionFeed)getSupportFragmentManager().findFragmentByTag(fragmentTag);
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

}
