package com.peck.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.Named;

/**
 * Created by mammothbane on 8/30/2014.
 */
public class UnavailableFragment extends Fragment implements Named {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_unavailable, container, false);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("you must override this method");
    }
}
