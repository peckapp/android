package com.peck.android.managers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;

/**
 * Created by mammothbane on 7/29/2014.
 *
 * class that handles all login/logout operations. most methods are synchronized to this class and are blocking.
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
    public static class InvalidAccountException extends Exception {}

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
        } else if (password.length() < 5 || password.length() > 20) {
            throw new InvalidPasswordException();
        }

        Account temp;
        HashMap<String, Account> accounts = getAccounts();
        if (email.equals(PeckAccountAuthenticator.TEMP_NAME)) throw new InvalidEmailException("Email can't be the same as the temporary account name");
        if (accounts.keySet().contains(email)) {
            temp = accounts.get(email);
            accountManager.setPassword(temp, password);
        } else {
            temp = new Account(email, PeckAccountAuthenticator.ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(temp, password, null)) throw new OperationCanceledException();
        }

        invalidateAuthToken(temp);

        Account authAccount = getActive();
        if (authAccount == null) {
            int counter = 0;
            while (getActive() == null && counter++ < 15) {
                createTemp();
                authAccount = getActive();
            }
            if (authAccount == null) throw new OperationCanceledException("authentication account couldn't be created");
        }



        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("user[password]", password);
            map.put("user[email]", temp.name);
            map.put("user[udid]", Settings.Secure.getString(PeckApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            map.put("user[device_type]", "android");
            map.putAll(JsonUtils.auth(authAccount, false));

            Log.d(LoginManager.class.getSimpleName(), map.toString());
            Log.d(LoginManager.class.getSimpleName(), GcmRegistrar.register());
            JsonObject jsonRet = ServerCommunicator.jsonService.login(map, GcmRegistrar.register());
            JsonObject user = ((JsonObject) jsonRet.get("user"));

            String token = user.get(PeckAccountAuthenticator.AUTH_TOKEN).getAsString();

            if (token == null) {
                accountManager.clearPassword(temp);
                accountManager.removeAccount(temp, null, null);
            } else {
                accountManager.setUserData(temp, PeckAccountAuthenticator.EMAIL, user.get(User.EMAIL).getAsString());
                accountManager.setUserData(temp, PeckAccountAuthenticator.INSTITUTION, user.get(User.LOCALE).getAsString());
                accountManager.setUserData(temp, PeckAccountAuthenticator.API_KEY, user.get(PeckAccountAuthenticator.API_KEY).getAsString());
                accountManager.setUserData(temp, PeckAccountAuthenticator.USER_ID, user.get(User.SV_ID).getAsString());
                setAuthToken(temp, token);
                accountManager.setPassword(temp, password);
                setActiveAccount(temp);

            }
        } catch (NetworkErrorException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * blocking method to create a new account with the specified parameters.
     *
     * @param email user's email
     * @param password user's password
     * @param firstName user's first name
     * @param lastName user's last name
     * @return true if successful, false if not
     * @throws InvalidEmailException
     * @throws InvalidPasswordException
     * @throws AccountAlreadyExistsException
     * @throws OperationCanceledException
     */
    public static synchronized boolean create(String email, String password, String firstName, String lastName)
            throws InvalidEmailException, InvalidPasswordException, AccountAlreadyExistsException, OperationCanceledException {

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException();
        } else if (password.length() < 5 || password.length() > 20) {
            //todo: make this check more stringent
            throw new InvalidPasswordException();
        }

        HashMap<String, Account> accounts = getAccounts();
        if (accounts.containsKey(email)) throw new AccountAlreadyExistsException();

        Account authAccount = getActive();
        if (authAccount == null) {
            int counter = 0;
            while (getActive() == null && counter++ < 15) {
                createTemp();
                authAccount = getActive();
            }
            if (authAccount == null) throw new OperationCanceledException("authentication account couldn't be created");
        }

        JsonObject object = new JsonObject();
        object.addProperty(User.EMAIL, email);
        object.addProperty("password", password);
        object.addProperty("password_confirmation", password);
        object.addProperty(User.FIRST_NAME, firstName);
        object.addProperty(User.LAST_NAME, lastName);
        object.addProperty(User.LOCALE, accountManager.getUserData(authAccount, PeckAccountAuthenticator.INSTITUTION));
        object.addProperty(User.UDID, Settings.Secure.getString(PeckApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        object.addProperty("device_type", "android");

        Account temp = new Account(email, PeckAccountAuthenticator.ACCOUNT_TYPE);

        if (!accountManager.addAccountExplicitly(temp, password, null)) throw new OperationCanceledException();


        try {
            JsonObject ret = ServerCommunicator.jsonService.superCreate(accountManager.getUserData(authAccount, PeckAccountAuthenticator.USER_ID),
                    new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object)), JsonUtils.auth(authAccount));

            JsonObject user = ((JsonObject) ret.get("user"));
            JsonArray errors = ((JsonArray) ret.get("errors"));

            if (errors.size() > 0) Log.e(LoginManager.class.getSimpleName(), errors.toString());

            accountManager.setUserData(temp, PeckAccountAuthenticator.EMAIL, user.get(User.EMAIL).getAsString());
            accountManager.setUserData(temp, PeckAccountAuthenticator.INSTITUTION, user.get(User.LOCALE).getAsString());
            accountManager.setUserData(temp, PeckAccountAuthenticator.API_KEY, accountManager.getUserData(authAccount, PeckAccountAuthenticator.API_KEY));
            accountManager.setUserData(temp, PeckAccountAuthenticator.USER_ID, user.get(User.SV_ID).getAsString());
            setAuthToken(temp, user.get(PeckAccountAuthenticator.AUTH_TOKEN).getAsString());
            accountManager.setPassword(temp, password);

            setActiveAccount(temp);
            clearTemp();

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
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        } catch (NetworkErrorException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param account the account to check
     * @return the cached value for the account's auth token
     */
    public static synchronized String peekAuthToken(Account account) {
        return accountManager.getUserData(account, PeckAccountAuthenticator.AUTH_TOKEN);
    }

    /**
     * blocking method to get an auth token for the given account. if the cached token is valid, we return it immediately.
     * do not call on the main thread.
     *
     * @param account the account
     * @return the account's token, null on failure
     */
    public static synchronized String getAuthToken(Account account) throws NetworkErrorException {
        if (peekAuthToken(account) != null) return peekAuthToken(account);
        try {
            Account authAccount = LoginManager.getActive();
            if (authAccount == null) throw new RuntimeException("LoginManager had null account");

            Map<String, String> map = new HashMap<String, String>();
            map.put("user[password]", accountManager.getPassword(account));
            map.put("user[email]", account.name);
            try {
                map.putAll(JsonUtils.auth(authAccount, false));
            } catch (InvalidAccountException e) {
                e.printStackTrace();
            }

            JsonObject jsonRet = ServerCommunicator.jsonService.login(map, GcmRegistrar.register());

            String token = ((JsonObject) jsonRet.get("user")).get("authentication_token").getAsString();
            setAuthToken(account, token);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * invalidate the auth token for a given account
     * @param account the account to invalidate
     * @return true if successful
     */
    public static synchronized boolean invalidateAuthToken(@Nullable Account account) {
        if (account != null) {
            accountManager.setUserData(account, PeckAccountAuthenticator.AUTH_TOKEN, null);
            return true;
        }
        return false;
    }

    /**
     * invalidate auth token for the account with the given name
     * @param accountName the account's name
     * @return true if successful
     */
    public static synchronized boolean invalidateAuthToken(@Nullable String accountName) {
        if (accountName != null) {
            Account invalidated = getAccounts().get(accountName);
            if (invalidated != null) return invalidateAuthToken(invalidated);
        }
        return false;
    }


    public static synchronized void setAuthToken(Account account, String token) {
        accountManager.setUserData(account, PeckAccountAuthenticator.AUTH_TOKEN, token);
    }


    /**
     * clears all data from the temporary account if it isn't the active account
     * sets the account's locale to the currently active locale
     * @deprecated we shouldn't be relying on temp accounts anymore, in theory
     */
    @Deprecated
    public static synchronized void clearTemp() {
        Account temp = getTemp();
        if (temp != null && !temp.equals(getActive())) {
            accountManager.setPassword(temp, null);
            accountManager.setUserData(temp, PeckAccountAuthenticator.EMAIL, null);
            accountManager.setUserData(temp, PeckAccountAuthenticator.USER_ID, null);
            accountManager.setUserData(temp, PeckAccountAuthenticator.API_KEY, null);
            accountManager.setUserData(temp, PeckAccountAuthenticator.INSTITUTION, getLocale());
            if (accountManager.getUserData(temp, PeckAccountAuthenticator.AUTH_TOKEN) != null) throw new RuntimeException("Temporary account had an auth token.", new InvalidAccountException());
        }
    }

    /**
     * cleans out all invalid accounts from the {@link android.accounts.AccountManager}
     */
    public static synchronized void cleanInvalid() {
        int removed = 0;
        HashMap<String, Account> accounts = getAccounts();
        for (Account account : accounts.values()) {
            if (!isValid(account) && !account.name.equals(PeckAccountAuthenticator.TEMP_NAME) || (account.name.equals(PeckAccountAuthenticator.TEMP_NAME) && !isValidTemp(account))) {
                accountManager.removeAccount(account, null, null);
                removed++;
            }
        }
        Log.v(LoginManager.class.getSimpleName(), removed + " invalid accounts removed.");
    }

    public static synchronized void logout(Account account) throws InvalidAccountException {
        if (!isValid(account)) throw new InvalidAccountException();
        logout(account.name);
    }

    public static synchronized void logout(String email) throws InvalidAccountException {
        Account account = getAccounts().get(email);
        if (!isValid(account)) throw new InvalidAccountException();
        invalidateAuthToken(account);
        setActiveAccount((Account)null);
    }

    /**
     * @return true if there is a temp account registered on the device
     * @deprecated preferable not to rely on temp accounts. no solution at the moment, but we should try not to rely on these methods.
     */
    @Deprecated
    public static synchronized boolean hasTemp() {
        HashMap<String, Account> accounts = getAccounts();
        return (accounts.containsKey(PeckAccountAuthenticator.TEMP_NAME) && accountManager.getUserData(accounts.get(PeckAccountAuthenticator.TEMP_NAME),
                PeckAccountAuthenticator.API_KEY) != null && accountManager.getUserData(accounts.get(PeckAccountAuthenticator.TEMP_NAME), PeckAccountAuthenticator.USER_ID) != null);
    }


    /**
     *
     * check a given account for validity as a temporary account.
     *
     * @param account the account to check
     * @return true if the account's name is the same as the temp account name defined in {@link com.peck.android.network.PeckAccountAuthenticator},
     * it has a user id, and it has an api key. false otherwise.
     * @deprecated see {@link #hasTemp()}}
     */
    @Deprecated
    public static synchronized boolean isValidTemp(Account account) {
        return (account != null && account.name.equals(PeckAccountAuthenticator.TEMP_NAME) &&
                accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) != null && accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID) != null);
    }


    /**
     *
     * checks the validity of a given account.
     *
     * @param account the account to check
     * @return true if the account is registered in the account manager && it has an api key && it has an institution && it has a user id. temp accounts are *not* valid if they haven't chosen an institution.
     */
    public static synchronized boolean isValid(@Nullable Account account) {
        if (account == null) return false;
        HashMap<String, Account> accounts = getAccounts();
        return (accounts.containsKey(account.name) && accounts.get(account.name).equals(account) && accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) != null
                && accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID) != null && accountManager.getUserData(account, PeckAccountAuthenticator.INSTITUTION) != null);
    }

    private static synchronized HashMap<String, Account> getAccounts() {
        HashMap<String, Account> ret = new HashMap<String, Account>();
        for (Account acct : accountManager.getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE)) {
            ret.put(acct.name, acct);
        }
        return ret;
    }

    /**
     * method to get the currently active account. performs no network access; can be called from the main thread.
     * @return the valid account specified when setActive was last called, or the temp account if none is available. null if there is no temp account.
     */
    @Nullable
    public static synchronized Account getActive() {
        HashMap<String, Account> accounts = getAccounts();
        String active = PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getString(ACTIVE_ACCOUNT, PeckAccountAuthenticator.TEMP_NAME);
        Account account = accounts.get(active);
        if (!isValid(account)) account = null;
        if (account == null && hasTemp()) account = getTemp();

        return account;
    }

    private static synchronized void setActiveAccount(@Nullable String name) throws InvalidAccountException {
        setActiveAccount(getAccounts().get(name));
    }

    /**
     * set the active account in sharedprefs. cancels any old syncs and initiates a new one for the newly active account.
     * @param account the account to activate
     * @throws InvalidAccountException when the account can't be validated (returns false from {@link #isValid(android.accounts.Account)})
     */
    private static synchronized void setActiveAccount(@Nullable Account account) throws InvalidAccountException {
        Account active = getActive();
        if (account == null) {
            for (PeriodicSync sync : ContentResolver.getPeriodicSyncs(active, PeckApp.AUTHORITY)) ContentResolver.removePeriodicSync(active, sync.authority, sync.extras);
            PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(ACTIVE_ACCOUNT, null).apply();
            Log.v(LoginManager.class.getSimpleName(), "Active account removed.");
            if (getTemp() == null) new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    //todo: fix this. we loop until we create an account, even though there might not be network access. we also don't need a temp account anymore
                    while (!createTemp()) {}
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    ContentResolver.addPeriodicSync(getTemp(), PeckApp.AUTHORITY, new Bundle(), PeckApp.Constants.Network.POLL_FREQUENCY);
                }
            }.execute();
            else ContentResolver.addPeriodicSync(getTemp(), PeckApp.AUTHORITY, new Bundle(), PeckApp.Constants.Network.POLL_FREQUENCY);
        } else if (isValid(account)) {
            if (active != null) ContentResolver.removePeriodicSync(active, PeckApp.AUTHORITY, new Bundle());
            ContentResolver.addPeriodicSync(account, PeckApp.AUTHORITY, new Bundle(), PeckApp.Constants.Network.POLL_FREQUENCY);
            PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(ACTIVE_ACCOUNT, account.name).apply();
            Log.v(LoginManager.class.getSimpleName(), "Active account changed.");
            logAccount(account);
        }
    }

    /**
     * send server current UDID. server hands back last active account on the device, if there was any, a new account if not.
     *
     * @return true if successful, false if not
     * @throws RetrofitError on network errors
     */
    public static synchronized boolean createUserWithUdid() throws RetrofitError {
        Account tmp;

        JsonObject retUser = ServerCommunicator.jsonService.userForUdid(Settings.Secure.getString(PeckApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID), "android").getAsJsonObject("user");

        if (retUser.get("new_user").getAsBoolean() || retUser.get(User.EMAIL).isJsonNull()) {
            HashMap<String, Account> accounts = getAccounts();
            if (accounts.containsKey(PeckAccountAuthenticator.TEMP_NAME)) tmp = accounts.get(PeckAccountAuthenticator.TEMP_NAME);
            else {
                tmp = new Account(PeckAccountAuthenticator.TEMP_NAME, PeckAccountAuthenticator.ACCOUNT_TYPE);
                if (!accountManager.addAccountExplicitly(tmp, null, null)) {
                    Log.e(LoginManager.class.getSimpleName(), "AccountManager failed to create a temporary account.");
                    return false;
                }
                Log.v(LoginManager.class.getSimpleName(), "Temp account created.");
            }
            if (getLocale() != null) accountManager.setUserData(tmp, PeckAccountAuthenticator.INSTITUTION, getLocale());
        } else {
            if (LoginManager.getAccounts().containsKey(retUser.get(User.EMAIL).getAsString())) {
                tmp = LoginManager.getAccounts().get(retUser.get(User.EMAIL).getAsString());
            } else {
                tmp = new Account(retUser.get(User.EMAIL).getAsString(), PeckAccountAuthenticator.ACCOUNT_TYPE);
                if (!accountManager.addAccountExplicitly(tmp, null, null)) {
                    Log.e(LoginManager.class.getSimpleName(), "AccountManager failed to create an account for " + retUser.get(User.FIRST_NAME));
                    return false;
                }
            }
            accountManager.setUserData(tmp, PeckAccountAuthenticator.INSTITUTION, retUser.get(User.LOCALE).getAsString());

        }


        accountManager.setUserData(tmp, PeckAccountAuthenticator.API_KEY, retUser.get("api_key").getAsString());
        accountManager.setUserData(tmp, PeckAccountAuthenticator.USER_ID, retUser.get("id").getAsString());
        logAccount(tmp);

        try {
            LoginManager.setActiveAccount(tmp);
        } catch (InvalidAccountException e) {
            return false;
        }

        return true;
    }


    /**
     * blocking method to create a temporary account with the server and add to accounts stored on device.
     * should *only* be used if the user requests a new account.
     * @return true if created, false if not
     */
    public static synchronized boolean createTemp() throws RetrofitError {
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

        final JsonObject ret = ((JsonObject) ServerCommunicator.jsonService.createUser().get("user"));
        accountManager.setUserData(tmp, PeckAccountAuthenticator.API_KEY, ret.get("api_key").getAsString());
        accountManager.setUserData(tmp, PeckAccountAuthenticator.USER_ID, ret.get("id").getAsString());
        if (getLocale() != null) accountManager.setUserData(tmp, PeckAccountAuthenticator.INSTITUTION, getLocale());
        Log.v(LoginManager.class.getSimpleName(), "Temp account created.");
        logAccount(tmp);
        return true;

    }

    /**
     * @return get the current temp account. may return null.
     * @deprecated we're no longer using the temp pattern in the same way. should figure out how to drop it.
     */
    @Nullable
    @Deprecated
    public static synchronized Account getTemp() {
        return getAccounts().get(PeckAccountAuthenticator.TEMP_NAME);
    }

    @Nullable
    /**
     * retrieve the current locale
     */
    public static synchronized String getLocale() {
        return PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getString(PeckApp.Constants.Preferences.LOCALE_ID, null);
    }

    /**
     * set the current locale for the logged-in account and through shared preferences.
     * todo: needs a rework. we shouldn't be setting both at the same time
     * @param name the name of the account to set the locale for
     * @param id the locale id to set.
     */
    public static synchronized void setLocale(String name, long id) {
        accountManager.setUserData(getAccounts().get(name), PeckAccountAuthenticator.INSTITUTION, Long.toString(id));
        PeckApp.getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit().putString(PeckApp.Constants.Preferences.LOCALE_ID, Long.toString(id)).apply();
    }


    /**
     * log the current status of the account in question
     *
     * @param account the account to log
     */
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
        String email = account.name;
        String password = (accountManager.getPassword(account) == null) ? "null" : "set";
        String api_key = (accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY) == null) ? "null" : "set";
        String auth_token = (peekAuthToken(account) == null) ? "null" : "set";
        Log.d("Account Log", String.format(" \nAccount %s \n" +
                "type            %s\n" +
                "user_id         %s\n" +
                "institution_id  %s\n" +
                "email           %s\n" +
                "password        %s\n" +
                "api_key         %s\n" +
                "auth_token      %s", name, type, id, inst, email, password, api_key, auth_token));
    }
}
