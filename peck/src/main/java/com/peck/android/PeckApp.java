package com.peck.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.peck.android.annotations.Header;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.FacebookSessionHandler;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;
import com.squareup.picasso.Picasso;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 5/28/2014.
 *
 * the base application, created when the app starts.
 *
 */
public class PeckApp extends Application implements Singleton{

    public static Context getContext() {
        return AppContext.mContext;
    }
    private static Account account;
    public static final String AUTHORITY = "com.peck.android.provider.all";

    private static final Class[] MODELS = { Circle.class, Event.class, Locale.class, Peck.class, Comment.class, User.class };


    /**
     * @param account the account to set active
     */
    public static void setActiveAccount(@Nullable Account account) {
        if (account == null) {
            if (PeckApp.account != null && PeckApp.account.name != null) {
                Account[] accounts = AccountManager.get(getContext()).getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE);
                if (accounts != null) {
                    for (Account acct : accounts) {
                        if (acct.name.equals(PeckApp.account.name)) {
                            AccountManager.get(getContext()).removeAccount(acct, null, null);
                        }
                    }
                }
            }
            getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).edit().putString(PeckAccountAuthenticator.ACCOUNT_NAME, null).apply();
            PeckApp.account = null;
        } else {
            String name = account.name;
            if (name == null) throw new NullPointerException("Accounts must have names");
            Account[] accounts = AccountManager.get(getContext()).getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE);
            boolean exists = false;
            for (Account acct : accounts) {
                if (acct.name.equals(account.name)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) AccountManager.get(getContext()).addAccountExplicitly(account, null, null);

            getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).edit().putString(PeckAccountAuthenticator.ACCOUNT_NAME, account.name).apply();
            PeckApp.account = account;
        }
    }


    /**
     * checks AccountManager for an active, valid account
     * @return the account, if it exists. null if not.
     */
    @Nullable
    public static Account peekValidAccount()
    {
        if (account == null) {
            Account[] accounts = AccountManager.get(getContext()).getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE);
            if (accounts.length == 1) {
                setActiveAccount(accounts[0]);
            } else {
                String name = getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).getString(PeckAccountAuthenticator.ACCOUNT_NAME, null);
                if (name != null) for (Account acct : accounts) {
                    if (acct.name.equals(name)) {
                        setActiveAccount(acct);
                    }
                }
            }
        }
        else if (AccountManager.get(getContext()).getUserData(account, PeckAccountAuthenticator.API_KEY) == null ||
                AccountManager.get(getContext()).getUserData(account, PeckAccountAuthenticator.INSTITUTION) == null) setActiveAccount(null);
        return account;
    }

    public static void logAccount(@Nullable Account account) {
        if (account == null) {
            Log.d("Account Log", "Account is null");
            return;
        }
        final AccountManager accountManager = AccountManager.get(getContext());
        String name = account.name;
        String type = account.type;
        String id = accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID);
        String inst = accountManager.getUserData(account, PeckAccountAuthenticator.INSTITUTION);
        String email = accountManager.getUserData(account, PeckAccountAuthenticator.EMAIL);
        String temp = accountManager.getUserData(account, PeckAccountAuthenticator.IS_TEMP);
        String password = (accountManager.getPassword(account) == null) ? "null" : "set";
        String api_key = (accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) == null) ? "null" : "set";
        Log.d("Account Log", String.format(" \nAccount %s \n" +
                "type            %s\n" +
                "user_id         %s\n" +
                "institution_id  %s\n" +
                "email           %s\n" +
                "password        %s\n" +
                "api_key         %s\n" +
                "temp            %s", name, type, id, inst, email, password, api_key, temp));
    }


    /**
     * blocking method to create a temporary account with the server and add to accounts stored on device.
     * @return the account; null if not created
     */
    public static Account createTempAccount() {
        final AccountManager manager = AccountManager.get(getContext());
        Account tmp = new Account(PeckAccountAuthenticator.getUserId(), PeckAccountAuthenticator.ACCOUNT_TYPE);
        JsonObject object = new JsonObject();
        object.addProperty(User.FIRST_NAME, (String)null);
        object.addProperty(User.LAST_NAME, (String)null);

        try {
            final JsonObject ret = ServerCommunicator.post(buildEndpointURL(User.class), JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), null).get("user").getAsJsonObject();
            if (manager.addAccountExplicitly(tmp, null, null)) {
                manager.setUserData(tmp, PeckAccountAuthenticator.API_KEY, ret.get("api_key").getAsString());
                manager.setUserData(tmp, PeckAccountAuthenticator.USER_ID, ret.get("id").getAsString());
                manager.setUserData(tmp, PeckAccountAuthenticator.IS_TEMP, "true");
                return tmp;
            } else return null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        }

        return null;
    }


    public static Class[] getModelArray() {
        return MODELS;
    }

    public static String buildEndpointURL(Class tClass) {
        Header header = (Header)tClass.getAnnotation(Header.class);
        if (BuildConfig.DEBUG && (header == null || header.singular() == null || header.plural() == null)) throw new IllegalArgumentException(tClass.getSimpleName() + " does not have a header");
        return Constants.Network.API_ENDPOINT + header.plural() + "/";
    }


    public static class AppContext {
        private static Context mContext;
        private AppContext() {}
        protected static void init(Context context) {
            mContext = context;
        }
    }

    public void onCreate() {

        //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        AppContext.init(this);
        Crashlytics.start(this);

        if (BuildConfig.DEBUG) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext()).setLoggingEnabled(true);

            SharedPreferences.Editor edit = getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
            edit.clear();
            edit.apply();
            Log.d("PeckApp", "cleared USER_PREFS SharedPreferences");
        }

        FacebookSessionHandler.init();
    }



    public static class Constants {

        public final static class Network {

            public final static int RETRY_INTERVAL = 200;
            public final static int CONNECT_TIMEOUT = 10000;
            public final static int READ_TIMEOUT = 6000;

            /**
             * API strings
             */
            public final static String BASE_URL = "http://loki.peckapp.com:3500";
            public final static String API_ENDPOINT = BASE_URL + "/api/";

        }

        public final static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String LOCALE_ID = "persistent locale id";
        }


        public final static class Database {
            public static final Uri BASE_AUTHORITY_URI = Uri.parse("content://com.peck.android.provider.all");
            public static final String DATABASE_NAME = "peck.db";
        }

        public final static class Location {

            public final static int INTERVAL = 300;
            public final static int RETRY = 33;

        }

        public final static class Graphics {

            public final static int FILLER = R.drawable.ic_peck;
            public final static int CACHE_SIZE = 5*1024*1024; //5MB cache maximum
            public final static int INT_CACHE_SIZE = 50;
            public final static int PNG_COMPRESSION = 90;

        }

    }


    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(getContext(),
                new HttpClientStack(HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build()));
        return requestQueue;
    }
}
