package com.peck.android.fragments.tabs;

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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.fragments.posts.AnnouncementPost;
import com.peck.android.fragments.posts.EventPost;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.listeners.ImagePickerListener;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Event;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;

import org.joda.time.DateTime;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment {

    private final static HashMap<Integer, Fragment> buttonIds = new HashMap<Integer, Fragment>(2); //don't use a sparsearray, we need the keys
    private final static int ANNOUNCEMENT = 0;
    private final static int EVENT = 1;
    private int bt_selected = EVENT;
    private AsyncTask<JsonObject, Void, JsonArray> runningTask;
    private final static DecimalFormat doubleFormat = new DecimalFormat("#");

    static {
        doubleFormat.setMaximumFractionDigits(1);
    }

    private class AnnouncementPostTask extends AsyncTask<JsonObject, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(JsonObject... object) {
            try {
                JsonObject ret = ServerCommunicator.post(PeckApp.Constants.Network.API_ENDPOINT + "announcements", object[0], JsonUtils.auth(LoginManager.getActive()));
                return ((JsonArray) ret.get("errors"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (VolleyError volleyError) {
                volleyError.printStackTrace();
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
    private class EventPostTask extends AsyncTask<JsonObject, Void, JsonArray> {
        @Override
        protected JsonArray doInBackground(JsonObject... object) {
            try {
                JsonObject ret = ServerCommunicator.post(PeckApp.Constants.Network.API_ENDPOINT + "simple_events", object[0], JsonUtils.auth(LoginManager.getActive()));
                return ((JsonArray) ret.get("errors"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (VolleyError volleyError) {
                volleyError.printStackTrace();
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

    static {
        buttonIds.put(R.id.bt_event, new EventPost());
        buttonIds.put(R.id.bt_announce, new AnnouncementPost());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_newpost, container, false);
        view.findViewById(R.id.iv_select).setOnClickListener(new ImagePickerListener(this));

        view.findViewById(R.id.bt_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText)view.findViewById(R.id.et_title)).getText().toString();
                if (runningTask == null || runningTask.getStatus() != AsyncTask.Status.PENDING || runningTask.getStatus() != AsyncTask.Status.RUNNING) {
                    switch (bt_selected) {
                        case ANNOUNCEMENT:
                            String text = ((EditText) view.findViewById(R.id.et_announce)).getText().toString();
                            runningTask = new AnnouncementPostTask();
                            JsonObject announcement = new JsonObject();
                            announcement.addProperty(Event.ANNOUNCEMENT_TITLE, title);
                            announcement.addProperty(Event.ANNOUNCEMENT_TEXT, text);
                            announcement.addProperty(Event.ANNOUNCEMENT_USER_ID, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID));
                            announcement.addProperty(Event.LOCALE, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION));
                            runningTask.execute(JsonUtils.wrapJson("announcement", announcement));
                            break;
                        case EVENT:
                            runningTask = new EventPostTask();
                            JsonObject event = new JsonObject();
                            //event.addProperty(Event.ANNOUNCEMENT_USER_ID, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID));
                            event.addProperty(Event.TITLE, title);
                            event.addProperty(Event.START_DATE, ((DateTime.now().toInstant().getMillis())/1000));
                            event.addProperty(Event.END_DATE, ((DateTime.now().toInstant().getMillis())/1000));
                            event.addProperty(Event.LOCALE, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION));
                            runningTask.execute(JsonUtils.wrapJson("simple_event", event));
                            break;
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        for (int i : buttonIds.keySet()) {
            getView().findViewById(i).setOnClickListener(new FragmentSwitcherListener(getChildFragmentManager(), buttonIds.get(i), "sbbtn " + i, R.id.post_content) {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.bt_event) bt_selected = EVENT;
                    else bt_selected = ANNOUNCEMENT;
                    super.onClick(view);
                }
            });
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
