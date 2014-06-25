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

    public FragmentSwitcherListener(FragmentManager fm, Fragment f, String tag, int containerId, Selector selector) {
        this.fm = fm;
        this.f = f;
        this.tag = tag;
        this.containerId = containerId;
        this.selector = selector;
    }

    @Override
    public void onClick(View view) {

        Fragment tempfrag = fm.findFragmentById(containerId);

        FragmentTransaction ft = fm.beginTransaction();
        if (tempfrag == null || !tempfrag.equals(f)) {
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            if (tempfrag != null) {
                ft.detach(tempfrag);
            }
            if (fm.findFragmentByTag(tag) == null) {
                ft.add(containerId, f, tag);
            } else {
                ft.attach(f);
            }
        } else {
            ft.attach(f);
        }
        ft.addToBackStack("test");
        ft.commit();
        selector.setSelected(f);

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
