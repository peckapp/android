package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PostManager;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment implements BaseTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_newpost, container, false);

        (Button)getActivity().findViewById(R.id.bt_event).setOnClickListener(newPostListener(new ));

    }

    private class newPostListener implements View.OnClickListener {
        Fragment f;

        public newPostListener(Fragment f) {
            this.f = f;
        }

        @Override
        public void onClick(View view) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

            if (getActivity().getSupportFragmentManager().findFragmentByTag(f.getTag()))


            ft.commit();
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
