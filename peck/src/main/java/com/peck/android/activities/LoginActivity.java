package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.peck.android.R;
import com.peck.android.network.PeckAccountAuthenticator;


public class LoginActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AccountManager accountManager = (AccountManager)getSystemService(Context.ACCOUNT_SERVICE);

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                accountManager.addAccountExplicitly(new Account(((EditText)findViewById(R.id.et_username)).getText().toString(),
                        PeckAccountAuthenticator.ACCOUNT_TYPE), ((EditText)findViewById(R.id.et_password)).getText().toString(), null);

            }
        });

        findViewById(R.id.bt_create_acct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountManager.addAccountExplicitly(new Account(((EditText) findViewById(R.id.et_username)).getText().toString(),
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


