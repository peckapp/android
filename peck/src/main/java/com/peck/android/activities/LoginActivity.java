package com.peck.android.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.managers.LoginManager;


public class LoginActivity extends PeckActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.login(
                        ((EditText)findViewById(R.id.et_username)).getText().toString(),
                        ((EditText)findViewById(R.id.et_password)).getText().toString(),
                        new Callback<Boolean>() {
                            @Override
                            public void callBack(Boolean obj) {
                                if (obj) finish();
                                else {} //todo: show the user an error toast
                            }
                        });
            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.createAccount(
                        ((EditText) findViewById(R.id.et_username)).getText().toString(),
                        ((EditText) findViewById(R.id.et_password)).getText().toString(),
                        new Callback<Boolean>() {
                            @Override
                            public void callBack(Boolean obj) {
                                if (obj) {
                                } //meta: if success, what do we do?
                                else {
                                } //todo: show the user an error toast
                            }
                        }
                );
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

    protected void onResume() {
        super.onResume();
    }


}


