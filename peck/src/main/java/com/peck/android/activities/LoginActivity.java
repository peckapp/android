package com.peck.android.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peck.android.R;
import com.peck.android.managers.LoginManager;

import java.io.IOException;


public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String USER_EMAIL = "user_email";
    public static final String REDIRECT_TO_FEEDACTIVITY = "redirect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean redirect = getIntent().getBooleanExtra(REDIRECT_TO_FEEDACTIVITY, false);

        String userEmail = getIntent().getStringExtra(USER_EMAIL);

        if (userEmail != null) {
            ((EditText) findViewById(R.id.et_email)).setText(userEmail);
        }

        Button button = ((Button) findViewById(R.id.bt_back));
        if (redirect) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
                final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            LoginManager.login(email, password);
                        } catch (LoginManager.InvalidEmailException e) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (LoginManager.InvalidPasswordException e) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        //todo: finish the activity
                        if (!isFinishing() && !LoginManager.isValidTemp(LoginManager.getActive())) finish();
                        else Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                    }

                }.execute();

            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
                final String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
                final String passwordConfirmation = ((EditText) findViewById(R.id.et_password_confirm)).getText().toString();
                final String[] name = ((EditText) findViewById(R.id.et_name)).getText().toString().split(" ");

                if (!password.equals(passwordConfirmation)) Toast.makeText(LoginActivity.this, "Passwords must match", Toast.LENGTH_LONG).show();
                else {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                LoginManager.create(email, password, name[0], name[1], Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                            } catch (LoginManager.InvalidEmailException e) {
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(), "Invalid email", Toast.LENGTH_LONG).show();
                                    }
                                });
                                e.printStackTrace();
                            } catch (LoginManager.InvalidPasswordException e) {
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(), "Invalid password", Toast.LENGTH_LONG).show();
                                    }
                                });
                                e.printStackTrace();
                            } catch (LoginManager.AccountAlreadyExistsException e) {
                                LoginManager.cleanInvalid();
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(), "That account is already registered on this device", Toast.LENGTH_LONG).show();
                                    }
                                });
                                e.printStackTrace();
                            } catch (OperationCanceledException e) {
                                LoginManager.cleanInvalid();
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            LoginManager.cleanInvalid();
                            if (!isFinishing() && !LoginManager.isValidTemp(LoginManager.getActive())) finish();
                            else Toast.makeText(LoginActivity.this, "Account creation failed.", Toast.LENGTH_LONG).show();
                        }
                    }.execute();
                }
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


