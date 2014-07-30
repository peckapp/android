package com.peck.android.network;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.peck.android.PeckApp;
import com.peck.android.activities.LoginActivity;

/**
 * Created by mammothbane on 7/15/2014.
 */
public class PeckAccountAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public PeckAccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    public static final String ACCOUNT_TYPE = "peckapp.com";
    public static final String USER_ID_PREF = "user id preferences";

    public static final String TEMP_NAME = "Auxiliary Account";

    public static final String USER_ID = "userid";
    public static final String EMAIL = "email";
    public static final String API_KEY = "api_key";
    public static final String INSTITUTION = "institution_id";
    public static final String AUTH_TOKEN = "authentication_token";
    public static final String TOKEN_TYPE = "peck_internal";

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType, String authTokenType, String[] requiredFeatures, Bundle opts) throws NetworkErrorException {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
        Bundle b = new Bundle();
        b.putParcelable(AccountManager.KEY_INTENT, intent);
        return b;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }


    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    public static synchronized String getUserId() {
        incUserId();
        return Integer.toString(PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(USER_ID_PREF, 0));
    }

    private static synchronized void incUserId() {
        int id = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(USER_ID_PREF, 0);
        PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putInt(USER_ID_PREF, id + 1).apply();
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return TOKEN_TYPE;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
