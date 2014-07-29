package com.peck.android.managers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 7/29/2014.
 */
public class LoginManager {
    private static AccountManager accountManager = AccountManager.get(PeckApp.getContext());

    private static String ACTIVE_ACCOUNT = "active_account";

    public static class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException() {
        }

        public InvalidCredentialsException(String detailMessage) {
            super(detailMessage);
        }
    }
    public static class InvalidEmailException extends InvalidCredentialsException {
        public InvalidEmailException() {
            super();
        }

        public InvalidEmailException(String detailMessage) {
            super(detailMessage);
        }
    }
    public static class InvalidPasswordException extends InvalidCredentialsException {
        public InvalidPasswordException() {
        }

        public InvalidPasswordException(String detailMessage) {
            super(detailMessage);
        }
    }
    public static class AccountAlreadyExistsException extends Exception {}

    /**
     *
     * blocking method to login
     * need to ensure that there's a temporary account for authentication before calling this method
     *
     * @param email the email to login with
     * @param password the password to login with
     * @throws OperationCanceledException
     * @throws InvalidEmailException
     * @throws com.peck.android.managers.LoginManager.InvalidPasswordException
     */
    public synchronized static boolean login(String email, String password) throws OperationCanceledException, InvalidEmailException, InvalidPasswordException, IOException {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException();
        } else if (password.length() < 4 || password.length() > 20) {
            throw new InvalidPasswordException();
        }

        Account temp;
        HashMap<String, Account> accounts = getAccounts();

        if (!hasTemp()) throw new OperationCanceledException("temp account didn't exist");

        if (email.equals(PeckAccountAuthenticator.TEMP_NAME)) throw new InvalidEmailException("Email can't be the same as the temporary account name");
        if (accounts.keySet().contains(email)) {
            temp = accounts.get(email);
            accountManager.setPassword(temp, password);
        } else {
            temp = new Account(email, PeckAccountAuthenticator.ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(temp, password, null)) throw new OperationCanceledException();
        }

        String token = accountManager.peekAuthToken(temp, PeckAccountAuthenticator.TOKEN_TYPE);
        if (token != null) accountManager.invalidateAuthToken(PeckAccountAuthenticator.ACCOUNT_TYPE, token);

        token = null;
        try {
            token = accountManager.blockingGetAuthToken(temp, PeckAccountAuthenticator.TOKEN_TYPE, true);
        } catch (AuthenticatorException e) {
            throw new OperationCanceledException("Authenticator failed to respond.");
        }

        if (token == null) {
            accountManager.clearPassword(temp);
            accountManager.removeAccount(temp, null, null);
            return false;
        } else {
            accountManager.setAuthToken(temp, PeckAccountAuthenticator.TOKEN_TYPE, token);
            setActiveAccount(temp);
            return true;
        }

    }

    public static synchronized boolean create(String email, String password, String firstName, String lastName) throws InvalidEmailException, InvalidPasswordException, AccountAlreadyExistsException {

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException();
        } else if (password.length() < 5 || password.length() > 20) {
            //todo: make this check more stringent
            throw new InvalidPasswordException();
        }

        HashMap<String, Account> accounts = getAccounts();
        if (accounts.containsKey(email)) throw new AccountAlreadyExistsException();

        Account authAccount = getTemp();
        if (authAccount == null) throw new IllegalArgumentException();

        JsonObject object = new JsonObject();
        object.addProperty(User.EMAIL, email);
        object.addProperty("password", password);
        object.addProperty("password_confirmation", password);
        object.addProperty(User.FIRST_NAME, firstName);
        object.addProperty(User.LAST_NAME, lastName);
        object.addProperty(User.LOCALE, accountManager.getUserData(authAccount, PeckAccountAuthenticator.INSTITUTION));

        Account temp = new Account(email, PeckAccountAuthenticator.ACCOUNT_TYPE);

        accountManager.addAccountExplicitly(temp, password, null);


        try {
            JsonObject ret = (ServerCommunicator.patch(PeckApp.buildEndpointURL(User.class) + accountManager.getUserData(authAccount, PeckAccountAuthenticator.USER_ID) + "/super_create",
                    JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), JsonUtils.auth(authAccount)));

            JsonObject user = ((JsonObject) ret.get("user"));
            JsonArray errors = ((JsonArray) ret.get("errors"));

            if (errors.size() > 0) Log.e(LoginManager.class.getSimpleName(), errors.toString());

            setActiveAccount(temp);

            accountManager.setUserData(temp, PeckAccountAuthenticator.EMAIL, user.get(User.EMAIL).getAsString());
            accountManager.setUserData(temp, PeckAccountAuthenticator.INSTITUTION, user.get(User.LOCALE).getAsString());
            accountManager.setUserData(temp, PeckAccountAuthenticator.API_KEY, user.get("api_key").getAsString());
            accountManager.setAuthToken(temp, PeckAccountAuthenticator.TOKEN_TYPE, user.get("authentication_token").getAsString());
            accountManager.setPassword(temp, password);

            LoginManager.logAccount(temp);

            accountManager.removeAccount(authAccount, null, null);

            while (!hasTemp()) {
                createTemp();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        }

        return false;
    }

    public static synchronized void logout(Account account) {
        logout(account.name);
    }

    public static synchronized void logout(String email) {
        HashMap<String, Account> accounts = getAccounts();
        Account account = accounts.get(email);
        if (account != null) {
            String token = accountManager.peekAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE);
            if (token != null) accountManager.invalidateAuthToken(PeckAccountAuthenticator.ACCOUNT_TYPE, token);
        }
        PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(ACTIVE_ACCOUNT, null).apply();
    }

    public static synchronized boolean hasTemp() {
        HashMap<String, Account> accounts = getAccounts();
        return (accounts.containsKey(PeckAccountAuthenticator.TEMP_NAME) && accountManager.getUserData(accounts.get(PeckAccountAuthenticator.TEMP_NAME),
                PeckAccountAuthenticator.API_KEY) != null && accountManager.getUserData(accounts.get(PeckAccountAuthenticator.TEMP_NAME), PeckAccountAuthenticator.USER_ID) != null);
    }

    public static synchronized boolean isValid(Account account) {
        if (account == null) return false;
        HashMap<String, Account> accounts = getAccounts();
        return (accounts.containsKey(account.name) && accounts.get(account.name).equals(account) && accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) != null
                && accountManager.getUserData(account, PeckAccountAuthenticator.INSTITUTION) != null);
    }

    private static synchronized HashMap<String, Account> getAccounts() {
        HashMap<String, Account> ret = new HashMap<String, Account>();
        for (Account acct : accountManager.getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE)) {
            ret.put(acct.name, acct);
        }
        return ret;
    }

    public static synchronized Account getActive() {
        HashMap<String, Account> accounts = getAccounts();
        String active = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getString(ACTIVE_ACCOUNT, PeckAccountAuthenticator.TEMP_NAME);
        Account account = accounts.get(active);
        if (account != null && !account.name.equals(PeckAccountAuthenticator.TEMP_NAME) && !isValid(account)) account = null;

        if (account == null && hasTemp()) account = getTemp();

        return account;
    }

    private static synchronized void setActiveAccount(String name) {
        PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(ACTIVE_ACCOUNT, name).apply();
    }

    private static synchronized void setActiveAccount(Account account) {
        setActiveAccount(account.name);
    }

    /**
     * blocking method to create a temporary account with the server and add to accounts stored on device.
     * @return the account; null if not created
     */
    public static synchronized boolean createTemp() {
        Account tmp;
        HashMap<String, Account> accounts = getAccounts();
        if (accounts.containsKey(PeckAccountAuthenticator.TEMP_NAME)) tmp = accounts.get(PeckAccountAuthenticator.TEMP_NAME);
        else {
            tmp = new Account(PeckAccountAuthenticator.TEMP_NAME, PeckAccountAuthenticator.ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(tmp, null, null)) {
                Log.e(LoginManager.class.getSimpleName(), "AccountManager failed to create a temporary account.");
                return false;
            }
        }

        try {
            final JsonObject ret = ServerCommunicator.post(PeckApp.buildEndpointURL(User.class), JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), null), null).get("user").getAsJsonObject();
            accountManager.setUserData(tmp, PeckAccountAuthenticator.API_KEY, ret.get("api_key").getAsString());
            accountManager.setUserData(tmp, PeckAccountAuthenticator.USER_ID, ret.get("id").getAsString());
            if (getLocale() != null) accountManager.setUserData(tmp, PeckAccountAuthenticator.INSTITUTION, getLocale());
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        }

        return false;
    }

    public static synchronized Account getTemp() {
        return getAccounts().get(PeckAccountAuthenticator.TEMP_NAME);
    }

    @Nullable
    public static synchronized String getLocale() {
        return PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getString(PeckApp.Constants.Preferences.LOCALE_ID, null);
    }

    public static synchronized void setLocale(String name, long id) {
        accountManager.setUserData(getAccounts().get(name), PeckAccountAuthenticator.INSTITUTION, Long.toString(id));
        PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(PeckApp.Constants.Preferences.LOCALE_ID, Long.toString(id)).apply();
    }


    public static void logAccount(@Nullable Account account) {
        if (account == null) {
            Log.d("Account Log", "Account is null");
            return;
        }
        final AccountManager accountManager = AccountManager.get(PeckApp.getContext());
        String name = account.name;
        String type = account.type;
        String id = accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID);
        String inst = accountManager.getUserData(account, PeckAccountAuthenticator.INSTITUTION);
        String email = accountManager.getUserData(account, PeckAccountAuthenticator.EMAIL);
        String temp = accountManager.getUserData(account, PeckAccountAuthenticator.IS_TEMP);
        String password = (accountManager.getPassword(account) == null) ? "null" : "set";
        String api_key = (accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) == null) ? "null" : "set";
        String auth_token = (accountManager.peekAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE) == null) ? "null" : "set";
        Log.d("Account Log", String.format(" \nAccount %s \n" +
                "type            %s\n" +
                "user_id         %s\n" +
                "institution_id  %s\n" +
                "email           %s\n" +
                "password        %s\n" +
                "api_key         %s\n" +
                "auth_token      %s\n" +
                "temp            %s", name, type, id, inst, email, password, api_key, auth_token, temp));
    }
}
