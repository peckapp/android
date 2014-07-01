package com.peck.android.activities;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.peck.android.R;
import com.peck.android.fragments.LocaleSelectionFeed;
import com.peck.android.managers.LocaleManager;


public class LocaleActivity extends PeckActivity {
    private boolean loaded = false;
    private boolean locationServices = true;
    private static final String TAG = "LocaleActivity";
    private static final String fragmentTag = "locale selection feed";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locale);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.rl_loc_select, new LocaleSelectionFeed(), fragmentTag);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!(servicesConnected() && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))) { //if we can't locate the user
            //todo: catch errors from google play services
            locationServices = false;
            notifyMe();
            //todo: re-search when location services come up?
            //todo: search bar?

        } else {
            LocaleManager.getLocation();
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
            final TextView tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
            tv.setVisibility(View.VISIBLE);
            tv.setText(R.string.pb_loc);

            if (locationServices) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        LocaleManager.getManager().calcDistances();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        tv.setVisibility(View.GONE);
                        findViewById(R.id.rl_locale).setVisibility(View.GONE);

                        findViewById(R.id.rl_loc_select).setVisibility(View.VISIBLE);
                    }
                }.execute(); //this only gets called if we know where the user is *and* have the location list loaded
            } else {
                findViewById(R.id.rl_locale).setVisibility(View.GONE);
                Toast.makeText(this, "Can't find you, please pick your location.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.rl_loc_select).setVisibility(View.VISIBLE);
            }
        } else {
            loaded = true;
        }
    }


}
