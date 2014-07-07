package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.fragments.BaseTab;
import com.peck.android.interfaces.Singleton;
import com.peck.android.listeners.FragmentSwitcherListener;

import java.util.HashMap;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends BaseTab {

    private final static HashMap<Integer, Fragment> buttonIds = new HashMap<Integer, Fragment>(3); //don't use a sparsearray, we need the keys

    static {
        buttonIds.put(R.id.bt_event, new EventPostTab());
        buttonIds.put(R.id.bt_announce, new AnnouncementPostTab());
        buttonIds.put(R.id.bt_photo, new PhotoPostTab());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_newpost, container, false);

        for (int i : buttonIds.keySet()) {
            v.findViewById(i).setOnClickListener(new FragmentSwitcherListener(getActivity().getSupportFragmentManager(), buttonIds.get(i), "btn " + i, R.id.post_content));
        }

        v.findViewById(R.id.bt_event).performClick();

        return v;
    }

    //todo: animate text field into visible area

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return null;
    }


}
