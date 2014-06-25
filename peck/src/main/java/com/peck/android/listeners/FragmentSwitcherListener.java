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
    private Selector selector;

    private int animIn = -1;
    private int animOut = -1;

    public FragmentSwitcherListener(FragmentManager fm, Fragment f, String tag, int containerId, Selector selector) {
        this.fm = fm;
        this.f = f;
        this.tag = tag;
        this.containerId = containerId;
        this.selector = selector;
    }

    public void setAnimations(int in, int out) {
        this.animIn = in;
        this.animOut = out;
    }


    @Override
    public void onClick(View view) {

        Fragment contentFrag = fm.findFragmentById(containerId);

        FragmentTransaction ft = fm.beginTransaction();

        if (contentFrag != null) { //detach the fragment that's currently attached
            ft.detach(contentFrag);
        }

        if (fm.findFragmentByTag(tag) == null) { //if the fragmentmanager doesn't have our fragment, add it
            ft.add(containerId, f, tag);
        } else {
            ft.attach(fm.findFragmentByTag(tag)); //otherwise attach it
        }

        if (animIn > 0 && animOut > 0) { //if we have custom animations, set them
            ft.setCustomAnimations(animIn, animOut);
        }

        ft.commit();
        fm.executePendingTransactions();

        selector.setSelected(fm.findFragmentByTag(tag));
    }

    public static class Selector {
        private Fragment selected;

        public Fragment getSelected() {
            return selected;
        }

        private void setSelected(Fragment selected) {
            this.selected = selected;
        }

        public void clear() {
            this.selected = null;
        }

    }
}
