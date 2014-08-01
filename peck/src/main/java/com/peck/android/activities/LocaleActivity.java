package com.peck.android.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Locale;
import com.peck.android.network.PeckSyncAdapter;


public class LocaleActivity extends PeckActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private Location location;
    private static final String fragmentTag = "locale selection feed";
    private static final int RESOLUTION_REQUEST_FAILURE = 9000;
    private LocationClient client = new LocationClient(PeckApp.getContext(), this, this);
    private boolean syncing = false;

    public static final long LOCATION_TIMEOUT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locale);

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

        LoginManager.cleanInvalid();

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        if ((servicesConnected() && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))) {
            if (!client.isConnected() || !client.isConnecting()) {
                client.connect();
            }
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (LoginManager.isValid(LoginManager.getActive())) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
            finish();
        } else {
            loadLocales();


            /**
             * wait for location from location client, then build the selection fragment
             */
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {

                    long started = System.currentTimeMillis();
                    while (location == null && System.currentTimeMillis() - started < LOCATION_TIMEOUT) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return location != null;
                }

                @Override
                protected void onPostExecute(Boolean bool) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.rl_loc_select, new Feed.Builder(DBUtils.buildLocalUri(Locale.class), R.layout.lvitem_locale)
                            .withBindings(new String[]{Locale.NAME}, new int[]{R.id.tv_title})
                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                            if (LoginManager.getActive() != null) {
                                                                Cursor cursor = getContentResolver().query(DBUtils.buildLocalUri(Locale.class),
                                                                        new String[] { DBOperable.SV_ID, DBOperable.LOCAL_ID }, DBOperable.LOCAL_ID + " = ?", new String[] {Long.toString(l)}, null);
                                                                cursor.moveToFirst();
                                                                long id = cursor.getLong(cursor.getColumnIndex(DBOperable.SV_ID));

                                                                LoginManager.setLocale(LoginManager.getActive().name, id);

                                                                Intent intent = new Intent(LocaleActivity.this, FeedActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else Log.e(LocaleActivity.class.getSimpleName(), "LoginManager didn't have an active account to assign a locale to");
                                                        }
                                                    }
                            )
                            .withProjection(new String[]{DBOperable.LOCAL_ID, Locale.NAME, (bool
                                    ? "(" + location.getLatitude() + " - " + Locale.LATITUDE + ")*(" + location.getLatitude() + " - " + Locale.LATITUDE + ")" + " + " +
                                    "(" + location.getLongitude() + " - " + Locale.LONGITUDE + ")*(" + location.getLongitude() + " - " + Locale.LONGITUDE + ")" : "null") + " as dist"})
                            .orderedBy("dist asc, " + Locale.NAME)
                            .layout(R.layout.localeselectionfeed)
                            .build(), fragmentTag);
                    ft.commit();
                }
            }.execute();
        }
    }


    private void loadLocales() {

        synchronized (this) {
            if (syncing) return;
            else syncing = true;
        }

        NetworkInfo info = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {

            //tell the user we're loading in
            final TextView tv = (TextView) findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);a
            tv.setVisibility(View.VISIBLE);
            tv.setText(R.string.pb_loc);
            findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_network_error).setVisibility(View.GONE);


            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    int counter = 1;
                    while (!LoginManager.hasTemp() && counter < 30) {
                        LoginManager.createTemp();
                        Log.v(LocaleActivity.class.getSimpleName(), "try #" + counter++);
                        try {
                            Thread.sleep(400L);
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    bundle.putString(PeckSyncAdapter.SYNC_TYPE, "com.peck.android.models.Locale");
                    ContentResolver.requestSync(LoginManager.getTemp(), PeckApp.AUTHORITY, bundle);


                    long startTime = System.currentTimeMillis();
                    while (getContentResolver().query(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Locale.class)).build(),
                            new String[]{DBOperable.LOCAL_ID}, null, null, null).getCount() == 0 && System.currentTimeMillis() - startTime < PeckApp.Constants.Network.CONNECT_TIMEOUT) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            syncing = false;
                            e.printStackTrace();
                        }
                    }
                    return getContentResolver().query(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Locale.class)).build(),
                            new String[]{DBOperable.LOCAL_ID}, null, null, null).getCount() != 0;
                }

                @Override
                protected void onPostExecute(Boolean ret) {
                    if (ret) {
                        findViewById(R.id.tv_progress).setVisibility(View.GONE);
                        findViewById(R.id.rl_locale).setVisibility(View.GONE);
                        findViewById(R.id.rl_loc_select).setVisibility(View.VISIBLE);
                    } else displayError();
                    syncing = false;

                }
            }.execute();
        } else {
            syncing = false;
            displayError();
        }
    }

    private void displayError() {
        findViewById(R.id.rl_network_error).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_locale).setVisibility(View.GONE);
        findViewById(R.id.rl_loc_select).setVisibility(View.GONE);
    }


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
        location = client.getLastLocation();
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
