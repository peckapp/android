package com.peck.android.listeners;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class FragmentSwitcherListener implements View.OnClickListener {
    private FragmentManager fm;
    private Fragment f;
    private String tag;
    private int containerId;

    private int animIn = -1;
    private int animOut = -1;
    private int popIn = -1;
    private int popOut = -1;

    public FragmentSwitcherListener(FragmentManager fm, Fragment f, String tag, int containerId) {
        this.fm = fm;
        this.f = f;
        this.tag = tag;
        this.containerId = containerId;
    }

    public void setAnimations(int in, int out) {
        this.animIn = in;
        this.animOut = out;
    }

    public void setAnimations(int animIn, int animOut, int popIn, int popOut) {
        setAnimations(animIn, animOut);
        this.popIn = popIn;
        this.popOut = popOut;
    }


    @Override
    public void onClick(View view) {

        Fragment contentFrag = fm.findFragmentById(containerId);

        FragmentTransaction ft = fm.beginTransaction();

        if (contentFrag != null) { //detach the fragment that's currently attached
            ft.detach(contentFrag);
        } else ft.addToBackStack(tag); //if we're going from null content (the home page) to something else, add this transaction to the backstack

        if (fm.findFragmentByTag(tag) == null) { //if the fragmentmanager doesn't have our fragment, add it
            ft.add(containerId, f, tag);
        } else {
            ft.attach(fm.findFragmentByTag(tag)); //otherwise attach it
        }

        if (animIn > 0 && animOut > 0) { //if we have custom animations, set them
            ft.setCustomAnimations(animIn, animOut);
        }

        ft.commit();
    }

}
