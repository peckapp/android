package com.peck.android.fragments.tabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peck.android.R;
import com.peck.android.fragments.posts.AnnouncementPost;
import com.peck.android.fragments.posts.EventPost;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.listeners.ImagePickerListener;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment {

    private final static HashMap<Integer, Fragment> buttonIds = new HashMap<Integer, Fragment>(2); //don't use a sparsearray, we need the keys

    static {
        buttonIds.put(R.id.bt_event, new EventPost());
        buttonIds.put(R.id.bt_announce, new AnnouncementPost());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_newpost, container, false);
        view.findViewById(R.id.iv_select).setOnClickListener(new ImagePickerListener(this));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        for (int i : buttonIds.keySet()) {
            getView().findViewById(i).setOnClickListener(new FragmentSwitcherListener(getChildFragmentManager(), buttonIds.get(i), "sbbtn " + i, R.id.post_content));
        }

        getView().findViewById(R.id.bt_event).performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case ImagePickerListener.REQUEST_CODE:
                        try {
                            Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                            ((ImageView)getView().findViewById(R.id.iv_select)).setImageBitmap(bmp);
                            Log.d("tag", "i have a bitmap");
                        } catch (FileNotFoundException e) { e.printStackTrace(); }
                        break;
                }
                break;

            default:
                break;

        }
    }

}
