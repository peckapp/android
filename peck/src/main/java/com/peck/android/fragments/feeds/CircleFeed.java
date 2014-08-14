/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments.feeds;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.makeramen.RoundedImageView;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
import com.peck.android.models.User;
import com.peck.android.models.joins.CircleMember;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mammothbane on 8/14/2014.
 */
public class CircleFeed extends Feed {
    private AutoCompleteTextView tView;

    //the circlefeed's current add position
    private int currentCircleAddPos = -1;

    //the current circle
    private long currentCircleId = -1;

    {
        //set up the circle member search view
        tView = new AutoCompleteTextView(PeckApp.getContext());

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);
        tView.setLayoutParams(params);
        tView.setTextColor(PeckApp.getContext().getResources().getColor(android.R.color.primary_text_light_nodisable));
        tView.setBackgroundColor(PeckApp.getContext().getResources().getColor(R.color.white));
        tView.setDropDownBackgroundDrawable(new ColorDrawable(R.color.white));
        tView.setDropDownVerticalOffset(8);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(PeckApp.getContext(), R.layout.frag_search, null, new String[] { User.FIRST_NAME }, new int[] {R.id.tv_title}, 0);
        tView.setAdapter(adapter);
        //return the first 10 results
        FilterQueryProvider queryProvider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                Cursor src = adapter.getCursor();
                if (src == null || src.getCount() == 0 || charSequence == null || charSequence.length() == 0) return src;
                MatrixCursor cursor = new MatrixCursor(new String[]{User.SV_ID, User.FIRST_NAME});
                String regex = "";
                for (char cha : charSequence.toString().toCharArray()) {
                    regex += cha;
                    regex += "[a-zA-Z]*";
                }
                Pattern pattern = Pattern.compile(regex);
                while (src.moveToNext() && cursor.getCount() < 10) {
                    if (pattern.matcher(src.getString(src.getColumnIndex(User.FIRST_NAME))).matches()) {
                        cursor.addRow(new Object[]{src.getLong(src.getColumnIndex(User.SV_ID)), src.getString(src.getColumnIndex(User.FIRST_NAME))});
                    }
                }
                return src;
            }
        };

        adapter.setFilterQueryProvider(queryProvider);

        tView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                tView.setText("");

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        JsonObject object = new JsonObject();
                        object.addProperty(CircleMember.LOCALE, Integer.parseInt(AccountManager.get(getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                        object.addProperty(CircleMember.CIRCLE_ID, currentCircleId);
                        object.addProperty(CircleMember.USER_ID, l);
                        object.addProperty(CircleMember.INVITED_BY, AccountManager.get(getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID));
                        if (currentCircleAddPos < 0)
                            throw new IllegalArgumentException("must set currentcircle before calling this method");
                        try {
                            ServerCommunicator.jsonService.post("circle_members", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("circle_member", object)), JsonUtils.auth(LoginManager.getActive()), new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    JsonArray errors = jsonObject.getAsJsonArray("errors");
                                    if (errors.size() > 0) {
                                        Toast.makeText(getActivity(), errors.toString(), Toast.LENGTH_LONG).show();
                                    } else {
                                        DBUtils.syncJson(DBUtils.buildLocalUri(CircleMember.class), jsonObject.getAsJsonObject("circle_member"), CircleMember.class);
                                        Log.d(CircleFeed.class.getSimpleName(), "success adding user " + jsonObject.getAsJsonObject("circle_member").get(CircleMember.USER_ID).getAsLong());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e(CircleFeed.class.getSimpleName(), "failure adding circle member");
                                }
                            });
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
                }.execute();
            }
        });

        loaderBundle = new Bundle();

        listItemRes = R.layout.lvitem_circle;
        binds_from = new String[] { Circle.NAME, Circle.NAME, Circle.NAME};
        binds_to = new int[] { R.id.tv_title, R.id.hlv_users, R.id.bt_add_cm};
        loaderBundle.putString(LOADER_SORT_ORDER, Circle.UPDATED_AT + " desc");
        loaderBundle.putParcelable(LOADER_URI, DBUtils.buildLocalUri(Circle.class));
        runnable = new RecycleRunnable() {
            @Override
            public void run() {
                //set/clear the search view as necessary
                View mView = getView();
                if (mView != null && recycledView != null && recycledView.isFocusable() && (getListView()).getPositionForView(recycledView) != currentCircleAddPos) {
                    ((LinearLayout) recycledView).addView(tView);
                } else if (recycledView != null && tView != null)
                    ((LinearLayout) recycledView).removeView(tView);
            }
        };

        viewBinder =  new SimpleCursorAdapter.ViewBinder() {
            final SparseArray<ArrayList<Map<String, Object>>> circleMembers = new SparseArray<ArrayList<Map<String, Object>>>();

            @Override
            public boolean setViewValue(final View view, final Cursor cursor,
                    int i) {
                switch (view.getId()) {
                    case R.id.hlv_users:
                        final int circle_id = cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID));
                        new AsyncTask<Void, Void, Void>() {
                            ArrayList<Map<String, Object>> ret = circleMembers.get(circle_id);
                            SimpleAdapter simpleAdapter;

                            @Override
                            protected void onPreExecute() {
                                setUpCell();
                            }

                            private void setUpCell() {
                                synchronized (circleMembers) {
                                    if (ret == null)
                                        ret = new ArrayList<Map<String, Object>>();
                                }

                                simpleAdapter = new SimpleAdapter(getActivity(), ret, R.layout.hlvitem_user,
                                        new String[]{User.FIRST_NAME, User.IMAGE_NAME}, new int[]{R.id.tv_title, R.id.iv_event});

                                ((HListView) view).setAdapter(simpleAdapter);
                                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                    @Override
                                    public boolean setViewValue(View view, Object o,
                                            String s) {
                                        switch (view.getId()) {
                                            case R.id.iv_event:
                                                if (o != null && o.toString().length() > 0) {
                                                    Picasso.with(getActivity())
                                                            .load(PeckApp.Constants.Network.BASE_URL + o)
                                                            .fit()
                                                            .centerCrop()
                                                            .into((RoundedImageView) view);
                                                }
                                                return true;
                                        }
                                        return false;
                                    }
                                });
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                Cursor second = getActivity().getContentResolver().query(DBUtils.buildLocalUri(Circle.class).buildUpon().appendPath(Integer.toString(circle_id)).appendPath("users").build(),
                                        new String[]{User.FIRST_NAME, User.IMAGE_NAME, User.LOCAL_ID, User.SV_ID}, null, null, null);

                                ret = new ArrayList<Map<String, Object>>();

                                while (second.moveToNext()) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(User.FIRST_NAME, second.getString(second.getColumnIndex(User.FIRST_NAME)));
                                    map.put(User.IMAGE_NAME, second.getString(second.getColumnIndex(User.IMAGE_NAME)));
                                    ret.add(map);
                                }
                                second.close();
                                synchronized (circleMembers) {
                                    circleMembers.put(circle_id, ret);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void avoid) {
                                setUpCell();
                            }
                        }.execute();
                        return true;
                    case R.id.bt_add_cm:
                        view.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        LinearLayout parent = ((LinearLayout) tView.getParent());
                                                        if (parent != null) {
                                                            parent.removeView(tView);
                                                            if (parent.equals(view.getParent().getParent()))
                                                                return;
                                                        }

                                                        tView.clearListSelection();
                                                        ((SimpleCursorAdapter) tView.getAdapter()).changeCursor(null);
                                                        tView.setText("");

                                                        ((LinearLayout) view.getParent().getParent()).addView(tView);
                                                        //tView.requestFocus();
                                                        View feedView = getView();
                                                        if (feedView != null) {
                                                            final ListView listView = ((ListView) getListView());
                                                            final int lvPos = listView.getPositionForView(tView);
                                                            currentCircleAddPos = lvPos;
                                                            listView.post(new Runnable() { //we post the movement as a runnable so the cell gets resized before it happens
                                                                @Override
                                                                public void run() {
                                                                    final int pos = lvPos - listView.getFirstVisiblePosition();
                                                                    final int offset = listView.getChildAt(pos).getMeasuredHeight() - tView.getMeasuredHeight();
                                                                    listView.smoothScrollToPositionFromTop(lvPos, -offset);
                                                                }
                                                            });
                                                            currentCircleId = listView.getAdapter().getItemId(currentCircleAddPos);
                                                            new AsyncTask<Void, Void, Void>() {
                                                                @Override
                                                                protected Void doInBackground(
                                                                        Void... voids) {
                                                                    ((SimpleCursorAdapter) tView.getAdapter()).changeCursor(getActivity().getContentResolver().query(DBUtils.buildLocalUri(Circle.class)
                                                                                    .buildUpon().appendPath(Long.toString(currentCircleId)).appendPath("nonmembers").build(),
                                                                            new String[]{ User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.IMAGE_NAME, User.THUMBNAIL}, null, null, User.FIRST_NAME ));
                                                                    return null;
                                                                }
                                                            }.execute();
                                                        } else {
                                                            throw new IllegalStateException("onclick was called when fragment was unavailable.");
                                                        }



                                                        Log.d(CircleFeed.class.getSimpleName(), "clicked");
                                                    }
                                                }

                        );
                        return true;
                    default:
                        return false;
                }
            }
        };

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LayoutInflater inflater = ((LayoutInflater) PeckApp.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        //inflate the header for the circles feed
        View header = inflater.inflate(R.layout.circles_header, null, false);

        //with circle-adding functionality
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View dialogView = inflater.inflate(R.layout.alert_circlecreate, null, false);
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setPositiveButton("Create", null).setCancelable(true).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View inner) {
                                final String circleName = ((EditText) dialogView.findViewById(R.id.et_name)).getText().toString();
                                Account account = LoginManager.getActive();
                                JsonObject jsonBody = new JsonObject();
                                jsonBody.addProperty(Circle.NAME, circleName);
                                jsonBody.addProperty(Circle.LOCALE, AccountManager.get(getActivity()).getUserData(account, PeckAccountAuthenticator.INSTITUTION));
                                jsonBody.addProperty(Circle.USER_ID, AccountManager.get(getActivity()).getUserData(account, PeckAccountAuthenticator.USER_ID));
                                dialogView.findViewById(R.id.pb_network).setVisibility(View.VISIBLE);

                                try {
                                    Map<String, String> auth = JsonUtils.auth(account);
                                    ServerCommunicator.jsonService.post("circles", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("circle", jsonBody)), auth, new Callback<JsonObject>() {
                                        @Override
                                        public void success(JsonObject object, Response response) {
                                            dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                            if (object.get("errors").getAsJsonArray().size() > 0) {
                                                Toast.makeText(getActivity(), "failure: " + object.get("errors").toString(), Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();
                                                DBUtils.syncJson(DBUtils.buildLocalUri(Circle.class), object.getAsJsonObject("circle"), Circle.class);
                                                dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(getActivity(), "Network error posting circle: " + ((error.getResponse() != null) ? (error.getResponse().getStatus() + " - " + error.getResponse().getReason()) : error.getMessage()), Toast.LENGTH_LONG).show();
                                            dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                        }
                                    });
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

                            }
                        });
                    }
                });
                dialog.show();

            }
        });
        headers.add(header);
    }
}
