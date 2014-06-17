package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PostManager;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment implements BaseTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_newpost, container, false);

        Bundle b = new Bundle(); //classloader here?

        getActivity().findViewById(R.id.bt_event).setOnClickListener(new newPostListener(new EventPost(), EventPost.class.getName()));
        getActivity().findViewById(R.id.bt_message).setOnClickListener(new newPostListener(new MessagePost(), MessagePost.class.getName()));

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add


        return v;
    }

    private class newPostListener implements View.OnClickListener {
        Post f;
        String tag;

        public newPostListener(Post f, String tag) {
            this.f = f;
            this.tag = tag;
        }

        @Override
        public void onClick(View view) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

            Fragment tempfrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.post_content);
            if (tempfrag != null){
                ft.detach(tempfrag);
            }

            if (getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null) {
                ft.add(R.id.post_content, f, tag);
            } else {
                ft.attach(f);
            }

            ft.commit();

            //todo: ui update the selected button
        }
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
