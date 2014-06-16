package com.peck.android.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by mammothbane on 6/16/2014.
 */
public abstract class PeckActivity extends FragmentActivity {
    //meta: might want to change from fragmentactivity, not sure

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set application theme here


        super.onCreate(savedInstanceState);
    }
}
