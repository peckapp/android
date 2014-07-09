package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.fragments.posts.AnnouncementPost;
import com.peck.android.fragments.posts.EventPost;
import com.peck.android.fragments.posts.PhotoPost;
import com.peck.android.listeners.FragmentSwitcherListener;

import java.util.HashMap;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment {

    private final static HashMap<Integer, Fragment> buttonIds = new HashMap<Integer, Fragment>(3); //don't use a sparsearray, we need the keys

    static {
        buttonIds.put(R.id.bt_event, new EventPost());
        buttonIds.put(R.id.bt_announce, new AnnouncementPost());
        buttonIds.put(R.id.bt_photo, new PhotoPost());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_newpost, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        for (int i : buttonIds.keySet()) {
            getView().findViewById(i).setOnClickListener(new FragmentSwitcherListener(getActivity().getSupportFragmentManager(), buttonIds.get(i), "sbbtn " + i, R.id.post_content));
        }

        getView().findViewById(R.id.bt_event).performClick();
    }

    //todo: animate text field into visible area


}
