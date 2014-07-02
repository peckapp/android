package com.peck.android.activities;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.fragments.LocaleSelectionFeed;
import com.peck.android.managers.LocaleManager;

import java.util.Collections;


public class LocaleActivity extends PeckActivity {
    private boolean loaded = false;
    private boolean locationServices = true;
    private static final String TAG = "LocaleActivity";
    private static final String fragmentTag = "locale selection feed";
    private LocaleSelectionFeed localeSelectionFeed = new LocaleSelectionFeed();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locale);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.rl_loc_select, localeSelectionFeed, fragmentTag);
        ft.commit();

        findViewById(R.id.bt_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLocales();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!(servicesConnected() && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))) { //if we can't locate the user
            //todo: catch errors from google play services
            locationServices = false;
            //todo: search bar?

        } else {
            LocaleManager.locate();
        }

        loadLocales();

    }

    private void loadLocales() {
        final TextView tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
        tv.setVisibility(View.VISIBLE);
        tv.setText(R.string.pb_loc);
        findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_network_error).setVisibility(View.GONE);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                long startTime = System.currentTimeMillis();
                while (LocaleManager.getManager().getData().size() == 0 && (System.currentTimeMillis() - startTime) < PeckApp.Constants.Network.TIMEOUT) {
                    try {
                        Thread.sleep(PeckApp.Constants.Network.RETRY_INTERVAL);
                    } catch (InterruptedException e) { Log.e(TAG, "waiting was interrupted"); }
                }
                if (locationServices) Collections.sort(LocaleManager.getManager().getData());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (LocaleManager.getManager().getData().size() == 0) {
                    findViewById(R.id.rl_network_error).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);
                    findViewById(R.id.rl_loc_select).setVisibility(View.GONE);
                } else {
                    //if (!locationServices) Toast.makeText(LocaleActivity.this, "Can't find you, please pick your location.", Toast.LENGTH_SHORT).show();
                    localeSelectionFeed.notifyDatasetChanged();
                    tv.setVisibility(View.GONE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);
                    findViewById(R.id.rl_loc_select).setVisibility(View.VISIBLE);
                }
            }
        }.execute(); //this only gets called if we know where the user is *and* have the location list loaded
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocaleManager.stopLocationServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleManager.locate();
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


}
