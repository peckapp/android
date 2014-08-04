package com.peck.android.fragments;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.peck.android.listeners.ImagePickerListener;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Event;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import retrofit.RetrofitError;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment {

    private final static int ANNOUNCEMENT = 0;
    private final static int EVENT = 1;
    private int bt_selected = EVENT;
    private AsyncTask<JsonObject, Void, JsonArray> runningTask;
    private final static DecimalFormat doubleFormat = new DecimalFormat("#");
    private Bitmap imageBitmap;
    private ProgressBar bar;

    static {
        doubleFormat.setMaximumFractionDigits(1);
    }

    private class PostTask extends AsyncTask<JsonObject, Void, JsonArray> {
        private String path;
        private Bitmap image;
        private String fileName;

        private PostTask(String path) {
            this.path = path;
        }

        private PostTask(String path, Bitmap image, String fileName) {
            this.path = path;
            this.image = image;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JsonArray doInBackground(JsonObject... object) {
            try {
                JsonObject ret;
                if (image != null) {
                    Map<String, String> jsonMap = JsonUtils.auth(LoginManager.getActive());
                    jsonMap.putAll(JsonUtils.jsonToMap(object[0]));
                    ret = ServerCommunicator.jsonService.post(path, jsonMap, new ServerCommunicator.Jpeg(fileName, image, 2 * 1024 * 1024));
                } else {
                    ret = ServerCommunicator.jsonService.post(path, new ServerCommunicator.TypedJsonBody(object[0]), JsonUtils.auth(LoginManager.getActive()));
                }
                return ((JsonArray) ret.get("errors"));
            } catch (RetrofitError e) {
                Log.e(NewPostTab.class.getSimpleName(), (e.getMessage() != null) ? e.getMessage() : e.toString() );
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (LoginManager.InvalidAccountException e) {
                e.printStackTrace();
            } catch (NetworkErrorException e) {
                e.printStackTrace();
            } finally {
                bar.post( new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.GONE);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonArray errors) {
            if (errors != null && errors.size() > 0) {
                Toast.makeText(getActivity(), errors.toString(), Toast.LENGTH_LONG).show();
            } else { Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show(); }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_newpost, container, false);
        view.findViewById(R.id.iv_select).setOnClickListener(new ImagePickerListener(this));
        bar = ((ProgressBar) view.findViewById(R.id.pb_network));

        if (getChildFragmentManager().findFragmentByTag("start") == null) getChildFragmentManager().beginTransaction().add(R.id.post_content, new DateSelector(), "start").commit();
        if (getChildFragmentManager().findFragmentByTag("end") == null) getChildFragmentManager().beginTransaction().add(R.id.post_content, new DateSelector(), "end").commit();

        view.findViewById(R.id.bt_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText)view.findViewById(R.id.et_title)).getText().toString();
                String text = ((EditText)view.findViewById(R.id.et_announce)).getText().toString();
                String userId = AccountManager.get(getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID);

                if (runningTask == null || runningTask.getStatus() != AsyncTask.Status.PENDING || runningTask.getStatus() != AsyncTask.Status.RUNNING) {
                    switch (bt_selected) {
                        case ANNOUNCEMENT:
                            runningTask = (imageBitmap == null) ? new PostTask("announcements") : new PostTask("announcements", imageBitmap,
                                    "announcement_photo_" + userId + "_" + DateTime.now().toInstant().getMillis()/1000 + ".jpeg");
                            JsonObject announcement = new JsonObject();
                            announcement.addProperty(Event.ANNOUNCEMENT_TITLE, title);
                            announcement.addProperty(Event.ANNOUNCEMENT_TEXT, text);
                            announcement.addProperty(Event.ANNOUNCEMENT_USER_ID, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID)));
                            announcement.addProperty(Event.LOCALE, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                            announcement.addProperty(Event.PUBLIC, ((Switch)getView().findViewById(R.id.sw_public)).isChecked());
                            runningTask.execute(JsonUtils.wrapJson("announcement", announcement));
                            break;
                        case EVENT:
                            runningTask = (imageBitmap == null) ? new PostTask("simple_events") : new PostTask("simple_events", imageBitmap,
                                    "event_photo_" + userId + "_" + DateTime.now().toInstant().getMillis()/1000 + ".jpeg");
                            JsonObject event = new JsonObject();
                            //event.addProperty(Event.ANNOUNCEMENT_USER_ID, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID));
                            event.addProperty(Event.TITLE, title);
                            event.addProperty(Event.TEXT, text);
                            event.addProperty(Event.START_TIMESTAMP, (((DateSelector)getChildFragmentManager().findFragmentByTag("start")).getDate().toInstant().getMillis()) / 1000);
                            event.addProperty(Event.END_TIMESTAMP, (((DateSelector)getChildFragmentManager().findFragmentByTag("end")).getDate().toInstant().getMillis()) / 1000);
                            event.addProperty(Event.LOCALE, Integer.parseInt(AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                            event.addProperty(Event.PUBLIC, ((Switch)getView().findViewById(R.id.sw_public)).isChecked());
                            runningTask.execute(JsonUtils.wrapJson("simple_event", event));
                            break;
                    }
                }
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
                        try {
                            Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
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
