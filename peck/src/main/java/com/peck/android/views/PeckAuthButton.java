package com.peck.android.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.activities.LocaleActivity;
import com.peck.android.activities.LoginActivity;
import com.peck.android.database.DatabaseManager;
import com.peck.android.network.PeckAccountAuthenticator;

/**
 * Created by mammothbane on 6/24/2014.
 */
public class PeckAuthButton extends Button {

    private Fragment fragment;

    public PeckAuthButton(Context context) {
        super(context);
    }

    public void update() {
        String btnText;
        View.OnClickListener onClickListener;
        final Account account = PeckApp.peekValidAccount();
        if (account == null || AccountManager.get(fragment.getActivity()).getUserData(account, PeckAccountAuthenticator.IS_TEMP).equals("true")) {
            btnText = fragment.getActivity().getString(R.string.bt_peck_login);
            fragment.getActivity().findViewById(R.id.bt_fb_link).setVisibility(View.GONE);
            onClickListener = (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                    fragment.startActivity(intent);
                }
            });

        } else {
            btnText = fragment.getActivity().getString(R.string.bt_peck_logout);
            onClickListener = (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext()).setMessage("Logging out will clear cached data from your device. Continue?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ContentResolver.setSyncAutomatically(account, PeckApp.AUTHORITY, false);
                                    if (ContentResolver.isSyncActive(account, PeckApp.AUTHORITY) || ContentResolver.isSyncPending(account, PeckApp.AUTHORITY))
                                        ContentResolver.cancelSync(account, PeckApp.AUTHORITY);
                                    DatabaseManager.closeDB();
                                    getContext().deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME);
                                    String token = AccountManager.get(getContext()).peekAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE);
                                    if (token != null)
                                        AccountManager.get(getContext()).invalidateAuthToken(PeckAccountAuthenticator.ACCOUNT_TYPE, token);

                                    new AsyncTask<Void, Void, Account>() {
                                        String inst_id = AccountManager.get(getContext()).getUserData(account, PeckAccountAuthenticator.INSTITUTION);

                                        @Override
                                        protected Account doInBackground(Void... voids) {
                                            return PeckApp.createTempAccount();
                                        }

                                        @Override
                                        protected void onPostExecute(Account account) {
                                            if (account == null) {
                                                Intent intent = new Intent(getContext(), LocaleActivity.class);
                                                getContext().startActivity(intent);
                                                fragment.getActivity().finish();
                                            } else {
                                                PeckApp.setActiveAccount(account);
                                                AccountManager.get(getContext()).setUserData(account, PeckAccountAuthenticator.INSTITUTION, inst_id);
                                                ContentResolver.setSyncAutomatically(account, PeckApp.AUTHORITY, true);
                                                update();
                                            }
                                        }
                                    }.execute();

                                }
                            }).create();
                }
            });
            fragment.getActivity().findViewById(R.id.bt_fb_link).setVisibility(View.VISIBLE);
            //TODO: probably going to want to release facebook session here too

        }
        setText(btnText);
        setOnClickListener(onClickListener);

    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }


}
