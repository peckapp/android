package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;


public class LoginActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AccountManager accountManager = (AccountManager)getSystemService(Context.ACCOUNT_SERVICE);

        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.et_email)).getText().toString();
                String password = ((EditText)findViewById(R.id.et_password)).getText().toString();

                if (!EmailValidator.getInstance().isValid(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                    return;
                } else if (password.length() < 4 || password.length() > 20) {
                    //todo: make this check more stringent
                    Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                    return;
                }

                Account[] accounts = AccountManager.get(LoginActivity.this).getAccounts();

                Account tmp = null;

                for (Account account : accounts) {
                    if (account.name.equals(email)) {
                        tmp = account;
                        break;
                    }
                }

                if (tmp == null) {
                    tmp = new Account(email, PeckAccountAuthenticator.ACCOUNT_TYPE);
                    accountManager.addAccountExplicitly(tmp, password, null);
                }

                JsonObject object = new JsonObject();
                object.addProperty(User.EMAIL, email);
                object.addProperty("password", password);

                try {
                    JsonObject ret = ServerCommunicator.post(PeckApp.Constants.Network.BASE_URL + "/access", JsonUtils.wrapJson(JsonUtils.getJsonHeader(User.class, false), object), null);
                    String authToken = ret.get("authentication_token").toString();
                    String apiKey = ret.get(PeckAccountAuthenticator.API_KEY).toString();
                    String userId = ret.get(PeckAccountAuthenticator.USER_ID).toString();

                    accountManager.setUserData(tmp, PeckAccountAuthenticator.API_KEY, apiKey);
                    accountManager.setUserData(tmp, PeckAccountAuthenticator.USER_ID, userId);
                    accountManager.setAuthToken(tmp, PeckAccountAuthenticator.TOKEN_TYPE, authToken);

                    Intent intent = new Intent();
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, tmp);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PeckAccountAuthenticator.ACCOUNT_TYPE);
                    intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                    intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, PeckAccountAuthenticator.TOKEN_TYPE);
                    setAccountAuthenticatorResult(intent.getExtras());
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountManager.addAccountExplicitly(new Account(((EditText) findViewById(R.id.et_email)).getText().toString(),
                        PeckAccountAuthenticator.ACCOUNT_TYPE), ((EditText) findViewById(R.id.et_password)).getText().toString(), null);
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


}


