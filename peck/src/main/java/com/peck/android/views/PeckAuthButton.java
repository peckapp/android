package com.peck.android.views;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.peck.android.R;
import com.peck.android.activities.LoginActivity;
import com.peck.android.managers.LoginManager;

/**
 * Created by mammothbane on 6/24/2014.
 */
public class PeckAuthButton extends Button {

    private Fragment fragment;

    public PeckAuthButton(Context context) {
        super(context);
    }

    public PeckAuthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PeckAuthButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void update() {
        String btnText;
        View.OnClickListener onClickListener;
        if (LoginManager.isLoggedIn()) {
            btnText = fragment.getActivity().getString(R.string.bt_peck_logout);
            onClickListener = (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.logout();
                    update();
                }
            });
            fragment.getActivity().findViewById(R.id.bt_fb_link).setVisibility(View.VISIBLE);
            //TODO: probably going to want to release facebook session here too
        } else {
            btnText = fragment.getActivity().getString(R.string.bt_peck_login);
            fragment.getActivity().findViewById(R.id.bt_fb_link).setVisibility(View.GONE);
            onClickListener = (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                    fragment.startActivity(intent);
                }
            });
        }
        setText(btnText);
        setOnClickListener(onClickListener);

    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }


}
