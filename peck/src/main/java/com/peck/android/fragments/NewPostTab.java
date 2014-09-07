/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.interfaces.FailureCallback;
import com.peck.android.interfaces.Named;
import com.peck.android.listeners.ImagePickerListener;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Event;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment implements Named {
    private final static int ANNOUNCEMENT = 0;
    private final static int EVENT = 1;
    private int bt_selected = EVENT;
    private final static DecimalFormat doubleFormat = new DecimalFormat("#");
    private Bitmap imageBitmap;
    private ImageView imageView;
    private ProgressBar bar;

    static {
        doubleFormat.setMaximumFractionDigits(1);
    }

    @Override
    public String getName() {
        return "Post";
    }

    private class PostCallback implements Callback<JsonObject> {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            bar.setVisibility(View.GONE);
            JsonObject object = jsonObject.getAsJsonObject("announcement");
            if (object == null) {
                object = jsonObject.getAsJsonObject("simple_event");
                object.addProperty(Event.TYPE, Event.SIMPLE_EVENT);
            } else {
                object.addProperty(Event.TYPE, Event.ANNOUNCEMENT);
            }

            JsonArray errors = jsonObject.getAsJsonArray("errors");

            if (errors != null && errors.size() > 0) {
                Toast.makeText(getActivity(), errors.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();
                DBUtils.syncJson(DBUtils.buildLocalUri(Event.class), object, Event.class);
                imageView.setImageBitmap(null);
                if (imageBitmap != null) {
                    imageBitmap.recycle();
                    imageBitmap = null;
                }
                getActivity().findViewById(R.id.bt_add).performClick();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            bar.setVisibility(View.GONE);
            error.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_newpost, container, false);
        view.findViewById(R.id.iv_select).setOnClickListener(new ImagePickerListener(this));
        imageView = ((ImageView) view.findViewById(R.id.iv_select));
        if (imageBitmap != null) imageView.setImageBitmap(imageBitmap);
        bar = ((ProgressBar) view.findViewById(R.id.pb_network));

        if (getChildFragmentManager().findFragmentByTag("start") == null) getChildFragmentManager().beginTransaction().add(R.id.post_content, new DateSelector(), "start").commit();
        if (getChildFragmentManager().findFragmentByTag("end") == null) getChildFragmentManager().beginTransaction().add(R.id.post_content, new DateSelector(), "end").commit();

        view.findViewById(R.id.bt_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = ((EditText)view.findViewById(R.id.et_title)).getText().toString();
                final String text = ((EditText)view.findViewById(R.id.et_announce)).getText().toString();
                final String userId = AccountManager.get(getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID);

                bar.setVisibility(View.VISIBLE);
                JsonUtils.auth(LoginManager.getActive(), new FailureCallback<Map<String, String>>() {
                    @Override
                    public void success(Map<String, String> obj) {
                        switch (bt_selected) {
                            case ANNOUNCEMENT:
                                JsonObject announcement = new JsonObject();
                                announcement.addProperty(Event.TITLE, title);
                                announcement.addProperty(Event.ANNOUNCEMENT_TEXT, text);
                                announcement.addProperty(Event.ANNOUNCEMENT_USER_ID, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID)));
                                announcement.addProperty(Event.LOCALE, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                                announcement.addProperty(Event.PUBLIC, ((Switch) getView().findViewById(R.id.sw_public)).isChecked());
                                if (imageBitmap == null) {
                                    ServerCommunicator.jsonService.post("announcements", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("announcement", announcement)), obj, new PostCallback());
                                } else {
                                    obj.putAll(JsonUtils.jsonToMap(JsonUtils.wrapJson("announcement", announcement)));
                                    ServerCommunicator.jsonService.post("announcements", obj, new ServerCommunicator.Jpeg("announcement_photo_" + userId + "_" + DateTime.now().toInstant().getMillis() / 1000 + ".jpeg", imageBitmap, 2 * 1024 * 1024), new PostCallback());
                                }
                                break;
                            case EVENT:
                                final JsonObject event = new JsonObject();
                                event.addProperty(Event.TITLE, title);
                                event.addProperty(Event.TEXT, text);
                                event.addProperty(Event.START_TIMESTAMP, (((DateSelector) getChildFragmentManager().findFragmentByTag("start")).getDate().toInstant().getMillis()) / 1000);
                                event.addProperty(Event.END_TIMESTAMP, (((DateSelector) getChildFragmentManager().findFragmentByTag("end")).getDate().toInstant().getMillis()) / 1000);
                                event.addProperty(Event.LOCALE, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                                event.addProperty(Event.PUBLIC, ((Switch) getView().findViewById(R.id.sw_public)).isChecked());
                                if (imageBitmap == null) {
                                    ServerCommunicator.jsonService.post("simple_events", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("simple_event", event)), obj, new PostCallback());
                                } else {
                                    obj.putAll(JsonUtils.jsonToMap(JsonUtils.wrapJson("simple_event", event)));
                                    ServerCommunicator.jsonService.post("simple_events", obj, new ServerCommunicator.Jpeg("event_photo_" + userId + "_" + DateTime.now().toInstant().getMillis() / 1000 + ".jpeg", imageBitmap, 2 * 1024 * 1024), new PostCallback());
                                }
                        }
                    }
                    public void failure(Throwable cause) {
                        bar.setVisibility(View.GONE);
                        cause.printStackTrace();
                    }

                });
        }
    });

    view.findViewById(R.id.bt_event).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View t) {
            bt_selected = EVENT;
            view.findViewById(R.id.post_content).setVisibility(View.VISIBLE);
        }
    });

    view.findViewById(R.id.bt_announce).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View t) {
            bt_selected = ANNOUNCEMENT;
            view.findViewById(R.id.post_content).setVisibility(View.GONE);
        }
    });



    view.findViewById(R.id.sw_public).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View t) {
            Switch sw = ((Switch) t);
            if (sw.isChecked()) {
                view.findViewById(R.id.bt_group_select).setEnabled(true);
            } else {
                view.findViewById(R.id.bt_group_select).setEnabled(false);
            }
        }
    });

    view.findViewById(R.id.bt_event).performClick();
    return view;
}


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case ImagePickerListener.REQUEST_CODE:
                        final boolean isCamera;
                        if (data == null) {
                            isCamera = true;
                        } else {
                            final String action = data.getAction();
                            if (action == null) {
                                isCamera = false;
                            } else {
                                isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                            }
                        }

                        Uri imageUri;
                        if (isCamera) {
                            imageUri = data.getParcelableExtra(ImagePickerListener.URI);
                        } else {
                            imageUri = data == null ? null : data.getData();
                        }


                        try {
                            Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                            ((ImageView)getView().findViewById(R.id.iv_select)).setImageBitmap(bmp);
                            this.imageBitmap = bmp;
                            Log.d(NewPostTab.class.getSimpleName(), "i have a bitmap");
                        } catch (FileNotFoundException e) { e.printStackTrace(); }
                        break;
                }
                break;

            default:
                break;

        }
    }

}
