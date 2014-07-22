package com.peck.android.network;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.activities.LoginActivity;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

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
    public static final String TEMPORARY_USER = "temporary user";

    public static final String ACCOUNT_NAME = "acct_name";

    public static final String USER_ID = "userid";
    public static final String API_KEY = "api_key";
    public static final String INSTITUTION = "institution_id";
    public static final String TOKEN_TYPE = "peck_internal";

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s2, String[] strings, Bundle bundle) throws NetworkErrorException {
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
        AccountManager actMgr = AccountManager.get(context);
        String cachedToken = actMgr.peekAuthToken(account, TOKEN_TYPE);
        Bundle ret = new Bundle();
        ret.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        ret.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        ret.putString(AccountManager.KEY_AUTH_TOKEN_LABEL, TOKEN_TYPE);

        if (cachedToken != null) {
            ret.putString(AccountManager.KEY_AUTHTOKEN, cachedToken);
            return ret;
        }

        if (actMgr.getUserData(account, USER_ID) != null) {
            try {
                JsonObject user = new JsonObject();
                user.addProperty("email", (String) null);
                user.addProperty("password", (String) null);

                JsonObject auth = new JsonObject();
                auth.addProperty("api_key", actMgr.getUserData(account, API_KEY));
                auth.addProperty("user_id", actMgr.getUserData(account, USER_ID));

                JsonObject post = new JsonObject();
                post.add("user", user);
                post.add("authentication", auth);

                ret.putString(AccountManager.KEY_AUTHTOKEN, ServerCommunicator.post(PeckApp.Constants.Network.ENDPOINT + "access", post).get("user").getAsJsonObject().get("authentication_token").getAsString());

                return ret;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (VolleyError e) {
                throw new NetworkErrorException(e);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
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
