package com.peck.android.network;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.json.JsonUtils;
import com.peck.android.models.DBOperable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class PeckSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String SYNC_TYPE = "sync type";
    private static Date last_synced;

    ContentResolver contentResolver;

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

    private static <T extends DBOperable> void sync(final Class<T> tClass, final Account account, final String authority, final ContentProviderClient client, final SyncResult syncResult) throws RemoteException {
        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        PeckApp.getRequestQueue().add(new JsonObjectRequest(PeckApp.buildEndpointURL(tClass), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JsonObject object = (JsonObject)new JsonParser().parse(response.toString());
                        Uri uri = PeckApp.buildLocalUri(tClass);
                        HashMap<Integer, JsonObject> incoming = new HashMap<Integer, JsonObject>();


                        try {
                            JsonArray array = object.getAsJsonArray(PeckApp.getJsonHeader(tClass, true));
                            for(int i = 0; i < array.size(); i++) {
                                JsonObject temp = (JsonObject)array.get(i);
                                incoming.put(temp.get(DBOperable.SV_ID).getAsInt(), temp);
                            }

                            Cursor cursor = client.query(uri, JsonUtils.getColumns(tClass), null, null, null);

                            while (cursor.moveToNext()) { //iterate through every item in the database
                                syncResult.stats.numEntries++;
                                JsonObject match = incoming.get(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

                                if (match == null) { //if the incoming data doesn't contain the item
                                    if (new Date(cursor.getInt(cursor.getColumnIndex(DBOperable.UPDATED_AT))).after(last_synced)) { //if ours is newer, push it up
                                        PeckApp.getRequestQueue().add()
                                    }

                                    batch.add(ContentProviderOperation.newDelete(PeckApp.buildLocalUri(tClass).buildUpon()
                                            .appendPath(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()).build());

                                } else {


                                }

                            }




                        } catch (RemoteException e) {
                            Log.e("SyncAdapter", "Database Error encountered", e);
                            syncResult.databaseError = true;
                        }

                        last_synced = new Date(System.currentTimeMillis());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }));

    }


    private static JsonObjectRequest post() {

    }


    private static JsonObjectRequest patch() {

    }




}
