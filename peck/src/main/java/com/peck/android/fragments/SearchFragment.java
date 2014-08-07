package com.peck.android.fragments;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.peck.android.R;

/**
 * Created by mammothbane on 8/7/2014.
 */
public class SearchFragment extends Fragment {
    public enum TYPE { USER, CIRCLE }
    public static final String SEARCH_TYPE = "search type";

    private TYPE type;

    private Thread searchThread = new Thread(new Runnable() {
        public Looper mine = Looper.myLooper();
        @Override
        public void run() {
            Looper.prepare();
            Looper.loop();
        }
    });



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        type = TYPE.values()[args.getInt(SEARCH_TYPE)];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_search, container, false);

        ((EditText) view.findViewById(R.id.et_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d(SearchFragment.class.getSimpleName(), "text changed.");


            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    public void reset() {

    }

}
