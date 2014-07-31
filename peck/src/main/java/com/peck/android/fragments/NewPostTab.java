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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.R;
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
import java.util.concurrent.ExecutionException;

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

    private class DateUpdaterListener implements TimePicker.OnTimeChangedListener {
        DateTime time;
        DatePicker picker;

        private DateUpdaterListener(DatePicker picker, int startingHour) {
            this.picker = picker;
            time = new DateTime(picker.getYear(), picker.getMonth() + 1, picker.getDayOfMonth(), startingHour, 0);
        }

        private void updatePicker() {
            picker.updateDate(time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth());
        }

        @Override
        public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
            time = time.withDate(picker.getYear(), picker.getMonth() + 1, picker.getDayOfMonth());
            if (time.getHourOfDay() == 0 && hour == 23) {
                time = time.minusDays(1);
                updatePicker();
            }
            else if (time.getHourOfDay() == 23 && hour == 0) {
                time = time.plusDays(1);
                updatePicker();
            }

            time = time.withHourOfDay(hour);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_newpost, container, false);
        view.findViewById(R.id.iv_select).setOnClickListener(new ImagePickerListener(this));

        DateTime future = DateTime.now().plusHours(1);
        final DatePicker dpStart = ((DatePicker) view.findViewById(R.id.dp_start));
        final DatePicker dpEnd = ((DatePicker) view.findViewById(R.id.dp_end));
        dpEnd.updateDate(future.getYear(), future.getMonthOfYear(), future.getDayOfMonth());
        final TimePicker tpStart = ((TimePicker)view.findViewById(R.id.tp_start));
        final TimePicker tpEnd = ((TimePicker)view.findViewById(R.id.tp_end));
        tpEnd.setCurrentHour(future.getHourOfDay());
        tpEnd.setCurrentMinute(future.getMinuteOfHour());

        tpStart.setOnTimeChangedListener(new DateUpdaterListener(dpStart, tpStart.getCurrentHour()));
        tpEnd.setOnTimeChangedListener(new DateUpdaterListener(dpEnd, tpEnd.getCurrentHour()));

        view.findViewById(R.id.bt_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText)view.findViewById(R.id.et_title)).getText().toString();
                String text = ((EditText)view.findViewById(R.id.et_announce)).getText().toString();

                if (runningTask == null || runningTask.getStatus() != AsyncTask.Status.PENDING || runningTask.getStatus() != AsyncTask.Status.RUNNING) {
                    switch (bt_selected) {
                        case ANNOUNCEMENT:

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
                            event.addProperty(Event.TEXT, text);
                            event.addProperty(Event.START_DATE, ((new DateTime(dpStart.getYear(), dpStart.getMonth(), dpStart.getDayOfMonth(), tpStart.getCurrentHour(), tpStart.getCurrentMinute()).toInstant().getMillis())/1000));
                            event.addProperty(Event.END_DATE, ((new DateTime(dpEnd.getYear(), dpEnd.getMonth(), dpEnd.getDayOfMonth(), tpEnd.getCurrentHour(), tpEnd.getCurrentMinute()).toInstant().getMillis()) / 1000));
                            event.addProperty(Event.LOCALE, AccountManager.get(NewPostTab.this.getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION));
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
