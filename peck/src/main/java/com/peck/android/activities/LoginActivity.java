package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.BuildConfig;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AccountManager accountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);

        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
                final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();

                login(email, password);

            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
                final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
                final String passwordConfirmation = ((EditText) findViewById(R.id.et_password_confirm)).getText().toString();
                final String[] name = ((EditText) findViewById(R.id.et_name)).getText().toString().split(" ");
                final Account account = PeckApp.peekValidAccount();

                if (!EmailValidator.getInstance().isValid(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                    return;
                } else if (password.length() < 5 || password.length() > 20 || !password.equals(passwordConfirmation)) {
                    //todo: make this check more stringent
                    Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                    return;
                } else if (name.length != 2) {
                    Toast.makeText(LoginActivity.this, "Invalid name", Toast.LENGTH_LONG).show();
                    return;
                } else if (account == null) {
                    Intent intent = new Intent(LoginActivity.this, LocaleActivity.class);
                    startActivity(intent);
                    finish();
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        JsonObject object = new JsonObject();
                        object.addProperty(User.EMAIL, email);
                        object.addProperty("password", password);
                        object.addProperty("password_confirmation", passwordConfirmation);
                        object.addProperty(User.FIRST_NAME, name[0]);
                        object.addProperty(User.LAST_NAME, name[1]);

                        Log.e(LoginActivity.class.getSimpleName(), "test");

                        try {
                            JsonObject ret = (ServerCommunicator.patch(PeckApp.buildEndpointURL(User.class) + "/" + accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID) + "/super_create",
                                    JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), JsonUtils.auth(account)));

                            JsonObject user = ((JsonObject) ret.get("user"));
                            JsonArray errors = ((JsonArray) ret.get("errors"));

                            if (errors.size() > 0) Log.e(LoginActivity.class.getSimpleName(), errors.toString());

                            PeckApp.setActiveAccount(account);

                            accountManager.setUserData(account, PeckAccountAuthenticator.EMAIL, user.get(User.EMAIL).getAsString());
                            accountManager.setAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE, user.get("authentication_token").getAsString());
                            accountManager.setUserData(account, PeckAccountAuthenticator.IS_TEMP, "false");
                            accountManager.setPassword(account, password);

                            PeckApp.logAccount(account);

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

                        return null;
                    }
                }.execute();
            }
        });


        findViewById(R.id.bt_account_suggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.rl_login).setVisibility(View.GONE);
                findViewById(R.id.rl_create).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.rl_create).setVisibility(View.GONE);
                findViewById(R.id.rl_login).setVisibility(View.VISIBLE);
            }
        });
    }

    private void login(final String email, final String password) {
        if (!EmailValidator.getInstance().isValid(email)) {
            Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
            return;
        } else if (password.length() < 4 || password.length() > 20) {
            //todo: make this check more stringent
            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
            return;
        }

        final AccountManager accountManager = AccountManager.get(LoginActivity.this);

        Account[] accounts = accountManager.getAccountsByType(PeckAccountAuthenticator.ACCOUNT_TYPE);

        Account tmp = null;

        if (accounts != null) {
            for (Account account : accounts) {
                String tEmail = accountManager.getUserData(account, PeckAccountAuthenticator.EMAIL);
                if (tEmail != null && tEmail.equals(email)) {
                    tmp = account;
                    break;
                }
            }
        }

        new AsyncTask<Account, Void, Void>() {
            @Override
            protected Void doInBackground(Account... accounts) {
                if (BuildConfig.DEBUG && accounts.length != 1) throw new IllegalArgumentException();
                Account account = accounts[0];
                String inst = getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).getString(PeckApp.Constants.Preferences.LOCALE_ID, null);

                if (account == null) {
                    account = PeckApp.createTempAccount();
                    if (account == null) {
                        return null;
                    } else {
                        accountManager.addAccountExplicitly(account, password, null);
                    }
                }

                accountManager.setUserData(account, PeckAccountAuthenticator.INSTITUTION, inst);
                accountManager.setPassword(account, password);
                accountManager.setUserData(account, PeckAccountAuthenticator.EMAIL, email);

                try {
                    String result = accountManager.blockingGetAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE, false);
                    if ( result != null){

                        Intent intent = new Intent();
                        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account);
                        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PeckAccountAuthenticator.ACCOUNT_TYPE);
                        intent.putExtra(AccountManager.KEY_AUTHTOKEN, result);
                        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, PeckAccountAuthenticator.TOKEN_TYPE);

                        LoginActivity.this.setAccountAuthenticatorResult(intent.getExtras());
                        LoginActivity.this.setResult(RESULT_OK, intent);
                        Log.d(LoginActivity.class.getSimpleName(), "preparing to finish activity");
                        LoginActivity.this.finish();

                    }

                } catch (OperationCanceledException e) { e.printStackTrace(); }
                catch (IOException e) { e.printStackTrace(); }
                catch (AuthenticatorException e) { e.printStackTrace(); }

                PeckApp.logAccount(account);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute(tmp);

    }

}


