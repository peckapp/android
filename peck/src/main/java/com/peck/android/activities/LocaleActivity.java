package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.fragments.Feed;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;


public class LocaleActivity extends PeckActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private boolean locationServices = true;
    private static final String TAG = "LocaleActivity";
    private static final String fragmentTag = "locale selection feed";
    private Feed<Locale> localeSelectionFeed;
    private static final int RESOLUTION_REQUEST_FAILURE = 9000;
    private LocationClient client = new LocationClient(PeckApp.getContext(), this, this);

    public static final String AUTHORITY = "com.peck.android.provider.all";
    public static final String ACCOUNT_TYPE = "peckapp.com";
    public static final String ACCOUNT = "dummy";

    private Account account = new Account(ACCOUNT, ACCOUNT_TYPE);

    {
        localeSelectionFeed = new Feed.Builder(PeckApp.buildLocalUri(Locale.class), R.layout.lvitem_locale)
                .withTextBindings(new String[] { Locale.NAME }, new int[] { R.id.tv_title })
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                //LocaleManager.setLocale((Locale) adapterView.getItemAtPosition(i));
                                                //todo: temporary
                                                LocaleManager.setLocale(1);


                                                Intent intent = new Intent(LocaleActivity.this, FeedActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                ).build();
    }

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

        if (((AccountManager)getSystemService(ACCOUNT_SERVICE)).addAccountExplicitly(account, null, null)) {
            Log.v(getClass().getSimpleName(), "account added");
        } else {
            Log.e(getClass().getSimpleName(), "account wasn't created.");
        }

        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        //ContentResolver.requestSync(new SyncRequest.Builder().setManual(true).setSyncAdapter(account, AUTHORITY).syncOnce().build());

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!client.isConnected() || !client.isConnecting()) {
            client.connect();
        }

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!(servicesConnected() && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))) { //if we can't locate the user
            //todo: catch errors from google play services
            locationServices = false;
            //todo: search bar?

        }

        loadLocales();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (LocaleManager.getLocale() > 0) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
            finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(account, AUTHORITY, bundle);
        }
    }

    private void loadLocales() {
        final TextView tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
        tv.setVisibility(View.VISIBLE);
        tv.setText(R.string.pb_loc);
        findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_network_error).setVisibility(View.GONE);
    }

   /* @Subscribe
    public void respondToLocaleLoad(DataHandler.InitComplete complete) {
        DataHandler.unregister(Locale.class, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DataHandler.getLoadState(Locale.class).getValue() != DataHandler.LoadState.LOAD_COMPLETE || DataHandler.getData(Locale.class).size() == 0) {
                    findViewById(R.id.rl_network_error).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);
                    findViewById(R.id.rl_loc_select).setVisibility(View.GONE);
                } else {
                    if (locationServices) Collections.sort(DataHandler.getData(Locale.class));
                    findViewById(R.id.tv_progress).setVisibility(View.GONE);
                    findViewById(R.id.rl_locale).setVisibility(View.GONE);
                    findViewById(R.id.rl_loc_select).setVisibility(View.VISIBLE);
                }
            }
        });
    }*/


    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
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

    public void onConnected(Bundle dataBundle) {
        LocaleManager.setLocation(client.getLastLocation());
    }

    /**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        //location = client.getLastLocation();
        Toast.makeText(this, "Disconnected from location services. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, RESOLUTION_REQUEST_FAILURE);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            //todo: dialog
        }
    }

}
