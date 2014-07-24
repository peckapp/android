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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.android.volley.VolleyError;
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
                } else if (password.length() < 4 || password.length() > 20 || !password.equals(passwordConfirmation)) {
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

                        try {
                            ServerCommunicator.patch(PeckApp.buildEndpointURL(User.class) + "/" + accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID),
                                    JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), JsonUtils.auth(account));

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

                        accountManager.setUserData(account, PeckAccountAuthenticator.EMAIL, email);
                        accountManager.setPassword(account, password);
                        login(email, password);

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

        for (Account account : accounts) {
            if (accountManager.getUserData(account, PeckAccountAuthenticator.EMAIL).equals(email)) {
                tmp = account;
                break;
            }
        }

        if (tmp == null) {
            tmp = new Account(PeckAccountAuthenticator.getUserId(), PeckAccountAuthenticator.ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(tmp, password, null);
        }

        new AsyncTask<Account, Void, Void>() {
            @Override
            protected Void doInBackground(Account... accounts) {
                if (BuildConfig.DEBUG && accounts.length != 1) throw new IllegalArgumentException();
                Account account = accounts[0];
                try {
                    JsonObject object = new JsonObject();
                    object.addProperty(User.EMAIL, email);
                    object.addProperty("password", password);
                    JsonObject ret;

                    ret = ServerCommunicator.post(PeckApp.Constants.Network.BASE_URL + "/access", JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), JsonUtils.auth(account));
                    String authToken = ret.get("authentication_token").toString();
                    String apiKey = ret.get(PeckAccountAuthenticator.API_KEY).toString();
                    String userId = ret.get(PeckAccountAuthenticator.USER_ID).toString();

                    accountManager.setUserData(account, PeckAccountAuthenticator.API_KEY, apiKey);
                    accountManager.setUserData(account, PeckAccountAuthenticator.USER_ID, userId);
                    accountManager.setAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE, authToken);

                    PeckApp.setActiveAccount(account);

                    Intent intent = new Intent();
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PeckAccountAuthenticator.ACCOUNT_TYPE);
                    intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                    intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, PeckAccountAuthenticator.TOKEN_TYPE);
                    setAccountAuthenticatorResult(intent.getExtras());
                    setResult(RESULT_OK, intent);
                    finish();



                } catch (ServerError e) {
                    e.printStackTrace();
                } catch (VolleyError volleyError) {
                    accountManager.removeAccount(account, null, null);
                    volleyError.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(tmp);

    }

}


