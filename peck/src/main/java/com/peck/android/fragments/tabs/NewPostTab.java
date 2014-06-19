package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.activities.FeedActivity;
import com.peck.android.fragments.SimpleFragment;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PostManager;

import java.util.HashMap;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends BaseTab {

    private final static HashMap<Integer, Integer> buttonIds = new HashMap<Integer, Integer>(3); //don't use a sparsearray, we need the keys

    static {
        buttonIds.put(R.id.bt_event, R.layout.pst_event);
        buttonIds.put(R.id.bt_message, R.layout.pst_message);
        buttonIds.put(R.id.bt_photo, R.layout.pst_photo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_newpost, container, false);

        Bundle b;
        SimpleFragment frag;

        for (int i : buttonIds.keySet()) {
            b = new Bundle();
            b.putInt(SimpleFragment.RESOURCE, buttonIds.get(i));
            frag = new SimpleFragment();
            frag.setArguments(b);
            v.findViewById(i).setOnClickListener(new FeedActivity.FragmentSwitcherListener(frag, "btn " + i, getActivity(), R.id.post_content));
        }

        v.findViewById(R.id.bt_event).performClick();

        return v;
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return PostManager.class;
    }

    @Override
    public int getTabTag() {
        return R.string.tb_newpost;
    }

}
