package com.peck.android.activities;

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
import com.peck.android.adapters.ViewAdapter;
import com.peck.android.fragments.Feed;
import com.peck.android.managers.DataHandler;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;
import com.squareup.otto.Subscribe;

import java.util.Collections;


public class LocaleActivity extends PeckActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private boolean locationServices = true;
    private static final String TAG = "LocaleActivity";
    private static final String fragmentTag = "locale selection feed";
    private Feed<Locale> localeSelectionFeed = new Feed<Locale>();
    private static final int RESOLUTION_REQUEST_FAILURE = 9000;
    private LocationClient client = new LocationClient(PeckApp.getContext(), this, this);

    {
        Bundle bundle = new Bundle();
        bundle.putString(Feed.CLASS_NAME, "com.peck.android.models.Locale");
        bundle.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.lvitem_locale);
        localeSelectionFeed.setArguments(bundle);
        localeSelectionFeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocaleManager.setLocale((Locale) adapterView.getItemAtPosition(i));
                finish();
            }
        });
        localeSelectionFeed.setViewAdapter(new ViewAdapter<Locale>() {
            @Override
            public void setUp(View view, Locale item) {
                ((TextView)view.findViewById(R.id.tv_locale_name)).setText(item.toString());
            }
        });
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

    private void loadLocales() {
        final TextView tv = (TextView)findViewById(R.id.rl_locale).findViewById(R.id.tv_progress);
        tv.setVisibility(View.VISIBLE);
        tv.setText(R.string.pb_loc);
        findViewById(R.id.rl_locale).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_network_error).setVisibility(View.GONE);

        DataHandler.register(Locale.class, this);
        DataHandler.init(Locale.class);
    }

    @Subscribe
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
