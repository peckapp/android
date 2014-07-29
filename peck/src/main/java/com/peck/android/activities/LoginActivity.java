package com.peck.android.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.peck.android.R;
import com.peck.android.managers.LoginManager;

import java.io.IOException;


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

                new AsyncTask<Void, Void, LoginManager.InvalidCredentialsException>() {
                    @Override
                    protected LoginManager.InvalidCredentialsException doInBackground(Void... voids) {
                        try {
                            LoginManager.login(email, password);
                        } catch (LoginManager.InvalidEmailException e) {
                            return e;
                        } catch (LoginManager.InvalidPasswordException e) {
                            return e;
                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(LoginManager.InvalidCredentialsException e) {
                        if (e == null) {

                        } else if (e instanceof LoginManager.InvalidEmailException) {
                            Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                        } else if (e instanceof LoginManager.InvalidPasswordException) {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                        }
                    }

                }.execute();

            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
                final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
                final String passwordConfirmation = ((EditText) findViewById(R.id.et_password_confirm)).getText().toString();
                final String[] name = ((EditText) findViewById(R.id.et_name)).getText().toString().split(" ");



                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            LoginManager.create(email, password, name[0], name[1]);
                        } catch (LoginManager.InvalidEmailException e) {
                            e.printStackTrace();
                        } catch (LoginManager.InvalidPasswordException e) {
                            e.printStackTrace();
                        } catch (LoginManager.AccountAlreadyExistsException e) {
                            e.printStackTrace();
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

}


