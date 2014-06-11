package com.peck.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;

/**
 * Created by mammothbane on 5/21/2014.
 */
public class LoginFragment extends android.support.v4.app.Fragment {


    private final static String TAG = "login_fragment_";
    private static int counter = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter++;
    }

    public static String buildTag() {
        return TAG + counter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_bt_login, container, false);
    }

    public void onStop() {
        super.onStop();

    }

}
