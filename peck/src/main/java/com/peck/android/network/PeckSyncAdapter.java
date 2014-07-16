package com.peck.android.network;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.database.DBUtils;
import com.peck.android.json.JsonUtils;
import com.peck.android.models.DBOperable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class PeckSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String SYNC_TYPE = "sync type";
    private static long last_synced;

    final ContentResolver contentResolver;

    public PeckSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    public PeckSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient client, SyncResult syncResult) {
        Class clss;

        String s = bundle.getString(SYNC_TYPE);

        try {
            if (s != null) {
                try {
                    clss = Class.forName(s);
                    sync(clss, account, authority, client, syncResult);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("class could not be parsed.");
                }
            } else for (Class clzz : PeckApp.getModelArray()) {
                sync(clzz, account, authority, client, syncResult);
            }
        } catch (RemoteException e) {
            Log.e("SyncAdapter", "Database Error encountered", e);
            syncResult.databaseError = true;
        }
    }

    private <T extends DBOperable> void sync(final Class<T> tClass, final Account account, final String authority, final ContentProviderClient client, final SyncResult syncResult) throws RemoteException {
        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        PeckApp.getRequestQueue().add(new JsonObjectRequest(PeckApp.buildEndpointURL(tClass), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JsonObject object = (JsonObject)new JsonParser().parse(response.toString());
                        HashMap<Integer, JsonObject> incoming = new HashMap<Integer, JsonObject>();
                        Uri uri = PeckApp.buildLocalUri(tClass);

                        try {
                            JsonArray array = object.getAsJsonArray(PeckApp.getJsonHeader(tClass, true));
                            for(int i = 0; i < array.size(); i++) {
                                JsonObject temp = (JsonObject)array.get(i);
                                incoming.put(temp.get(DBOperable.SV_ID).getAsInt(), temp);
                            }

                            Cursor cursor = client.query(uri, DBUtils.getColumns(DBOperable.class), null, null, null);

                            while (cursor.moveToNext()) { //iterate through every item in the relevant table
                                syncResult.stats.numEntries++;
                                JsonObject match = incoming.get(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

                                if (match == null) { //if the incoming data doesn't contain the item, delete it
                                    if (cursor.getLong(cursor.getColumnIndex(DBOperable.CREATED_AT)) > last_synced) { //if ours was created since the last time we synced,
                                        try {
                                        post(tClass, JsonUtils.cursorToJson(cursor), null, null);  //post it to the server
                                        } catch (JSONException e) { Log.e(getClass().getSimpleName(), "couldn't serialize to json, e"); }
                                    } else {                                                                                    //if it's older and we haven't updated it, just delete it
                                        batch.add(ContentProviderOperation.newDelete(uri.buildUpon().appendPath(
                                                Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()).build());
                                    }

                                } else {
                                    //there's a match. remove from incoming to prevent being inserted into the database
                                    incoming.remove(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

                                    if (cursor.getLong(cursor.getColumnIndex(DBOperable.UPDATED_AT)) > match.get(DBOperable.UPDATED_AT).getAsLong()) { //our version is newer
                                        try {
                                            if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) delete(tClass, cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)), null, null); //delete the object
                                            else patch(tClass, object, null, null); //patch the object
                                        } catch (JSONException e) {
                                            Log.e(getClass().getSimpleName(), "couldn't convert to JSON", e);
                                        }
                                    } else { //the server version is newer
                                        if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) { //if ours was flagged for deletion, unflag it
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(DBOperable.DELETED, false);
                                            client.update(uri.buildUpon().appendPath(Integer.toString(
                                                    cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build(), contentValues, null, null);
                                        }

                                        batch.add(ContentProviderOperation.newUpdate(uri.buildUpon().
                                                appendPath(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()) //schedule an update
                                                .withValues(JsonUtils.jsonToContentValues(match, tClass))
                                                .build());

                                    }
                                }
                            }

                            cursor.close();

                            for (JsonObject json : incoming.values()) {
                                batch.add(ContentProviderOperation.newInsert(uri).withValues(JsonUtils.jsonToContentValues(json, tClass)).build());
                            }
                            try {
                                contentResolver.applyBatch(authority, batch);
                            } catch (OperationApplicationException e) {
                                Log.e(getClass().getSimpleName(), "couln't apply batch update", e);
                            }

                            contentResolver.notifyChange(uri, null, false);
                            client.delete(uri, null, null);

                        } catch (RemoteException e) {
                            Log.e("SyncAdapter", "Database Error encountered", e);
                            syncResult.databaseError = true;
                        }

                        last_synced = System.currentTimeMillis();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }));

    }


    private static void post(Class tClass, JsonObject object, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) throws JSONException {
        PeckApp.getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, PeckApp.buildEndpointURL(tClass), JsonUtils.wrapJson(tClass, object), (listener != null) ? listener : new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, (errorListener != null) ? errorListener : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }


    private static void patch(Class tClass, JsonObject object, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) throws JSONException {
        PeckApp.getRequestQueue().add(new JsonObjectRequest(Request.Method.PATCH, PeckApp.buildEndpointURL(tClass) + "/" + object.get(DBOperable.SV_ID), JsonUtils.wrapJson(tClass, object), (listener != null) ? listener : new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, (errorListener != null) ? errorListener : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private static void delete(Class tClass, int id, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) throws JSONException {
        PeckApp.getRequestQueue().add(new JsonObjectRequest(Request.Method.DELETE, PeckApp.buildEndpointURL(tClass) + "/" + id, null, (listener != null) ? listener : new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, (errorListener != null) ? errorListener : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }



}
