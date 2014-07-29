package com.peck.android.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
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

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.annotations.NoMod;
import com.peck.android.database.DBUtils;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class PeckSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String SYNC_TYPE = "sync type";
    public static final String URL = "url";
    public static final String EVENT_TYPE = "event_type";
    final ContentResolver contentResolver;

    public PeckSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient client, SyncResult syncResult) {
        Log.v(PeckSyncAdapter.class.getSimpleName(), "syncing");

        String syncType = bundle.getString(SYNC_TYPE);
        String urlToSync = bundle.getString(URL);
        int eventType = bundle.getInt(EVENT_TYPE, -1);

        final String authToken = AccountManager.get(getContext()).peekAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE);

        if (syncType != null) {
            try {
                runSync(Class.forName(syncType), account, authority, client, syncResult, urlToSync, eventType, authToken);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class not resolvable from name " + syncType);
            }
        } else for (Class clzz : PeckApp.getModelArray()) {
            runSync(clzz, account, authority, client, syncResult, urlToSync, eventType, authToken);
        }
    }

    private void runSync(Class tClass, Account account, String authority, ContentProviderClient client, SyncResult syncResult, String url, int eventType, String authToken) {
        Log.v(getClass().getSimpleName(), "initting sync for " + tClass.getSimpleName());


        try {
            if (!tClass.equals(Event.class)) {
                sync(tClass, account, authority, client, syncResult, url);
            } else {
                if (eventType == -1) {
                    customSyncEvents(account, authority, client, syncResult, Event.SIMPLE_EVENT, true, url);
                    customSyncEvents(account, authority, client, syncResult, Event.ATHLETIC_EVENT, false, url);
                    customSyncEvents(account, authority, client, syncResult, Event.DINING_OPPORTUNITY, false, url);
                    customSyncEvents(account, authority, client, syncResult, Event.ANNOUNCEMENT, true, url);
                } else {
                    customSyncEvents(account, authority, client, syncResult, eventType, (eventType == Event.SIMPLE_EVENT || eventType == Event.ANNOUNCEMENT), url);
                }
            }
        } catch (VolleyError volleyError) {
            if (volleyError.networkResponse != null) {
                Log.d(getClass().getSimpleName(), respondToStatusCode(volleyError.networkResponse.statusCode));
            }
        } catch (ExecutionException e) {
            if (e.getCause() instanceof VolleyError) {
                VolleyError error = ((VolleyError) e.getCause());
                if (error.networkResponse != null) Log.d(getClass().getSimpleName(), respondToStatusCode(error.networkResponse.statusCode));
            }
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            syncResult.stats.numParseExceptions++;
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
        }
    }

    public static String respondToStatusCode(int code) {
        String ret = "Error " + code + ": ";

        switch (code) {
            case 400:
                ret += "Bad request. Most likely malformed syntax";
                break;
            case 401:
                ret += "Unauthorized.";
                break;
            case 403:
                ret += "Forbidden.";
                break;
            case 404:
                ret += "Not found";
                break;
            case 500:
                ret += "Internal server error.";
                break;
            case 502:
                ret += "Bad gateway.";
                break;
            default:
                ret += "Unknown.";
                break;
        }
        return ret;
    }


    private <T extends DBOperable> void sync(final Class<T> tClass, final Account account, final String authority, final ContentProviderClient client, final SyncResult syncResult, final String url)
            throws RemoteException, InterruptedException, ExecutionException, OperationApplicationException, JSONException, VolleyError, IOException, OperationCanceledException, AuthenticatorException {
        final boolean mod = tClass.getAnnotation(NoMod.class) == null;
        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        final long sResultEntries = syncResult.stats.numEntries;
        final long sResultSkipped = syncResult.stats.numSkippedEntries;
        final long sResultUpdated = syncResult.stats.numUpdates;
        final long sResultDeleted = syncResult.stats.numDeletes;
        final long sResultInserts = syncResult.stats.numInserts;
        int svGet = 0;
        int svUpdated = 0;
        int svCreated = 0;
        int svDeleted = 0;

        JsonObject object = ServerCommunicator.get(PeckApp.buildEndpointURL(tClass), JsonUtils.auth(account));

        /*JsonArray post = new JsonArray();
        JsonArray patch = new JsonArray();*/


        HashMap<Integer, JsonObject> incoming = new HashMap<Integer, JsonObject>(); //don't use a sparsearray; hashmap performance will be better when we have a lot of objects, and the data doesn't get reused
        Uri uri = DBUtils.buildLocalUri(tClass);

        JsonArray array = object.getAsJsonArray(JsonUtils.getJsonHeader(tClass, true));
        for (int i = 0; i < array.size(); i++) {
            JsonObject temp = (JsonObject) array.get(i);
            incoming.put(temp.get(DBOperable.SV_ID).getAsInt(), temp);
        }
        svGet = array.size();

        Cursor cursor = client.query(uri, DBUtils.getColumns(DBOperable.class), null, null, null);

        while (cursor.moveToNext()) { //iterate through every item in the relevant table
            syncResult.stats.numEntries++;
            JsonObject match = incoming.get(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

            if (match == null && url == null) { //if the incoming data doesn't contain the item and this isn't an incremental sync
                    if (cursor.isNull(cursor.getColumnIndex(DBOperable.SV_ID)) && mod) {
                        //if ours was created since the last time we synced and we're allowed to modify the server's data
                        svCreated++;

                        //post.add(JsonUtils.cursorToJson(cursor));
                        ServerCommunicator.post(PeckApp.buildEndpointURL(tClass), JsonUtils.wrapJson(JsonUtils.getJsonHeader(tClass, false), JsonUtils.cursorToJson(cursor)), JsonUtils.auth(account));  //post it to the server

                    } else {
                        //if it's older and we haven't updated it, just delete it
                        syncResult.stats.numDeletes++;
                        batch.add(ContentProviderOperation.newDelete(uri.buildUpon().appendPath(
                                Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()).build());
                    }


            } else {
                //there's a match. remove from incoming to prevent being inserted into the database
                incoming.remove(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

                if (cursor.getLong(cursor.getColumnIndex(DBOperable.UPDATED_AT)) > match.get(DBOperable.UPDATED_AT).getAsLong() && mod) { //our version is newer and we're allowed to modify the server's version
                    if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) { //and it's been flagged for deletion
                        svDeleted++;
                        ServerCommunicator.delete(PeckApp.buildEndpointURL(tClass) + "/" + cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)), JsonUtils.auth(account)); //delete it from the server
                    } else { //if it hasn't been
                        svUpdated++; //patch it
                        ServerCommunicator.patch(PeckApp.buildEndpointURL(tClass) + cursor.getLong(cursor.getColumnIndex(DBOperable.SV_ID)),
                                JsonUtils.wrapJson(JsonUtils.getJsonHeader(tClass, false), JsonUtils.cursorToJson(cursor)), JsonUtils.auth(account));
                    }
                } else if (cursor.getLong(cursor.getColumnIndex(DBOperable.UPDATED_AT)) < match.get(DBOperable.UPDATED_AT).getAsLong()) { //the server version is newer or we're not allowed to modify the server version
                    if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) { //if ours was flagged for deletion, unflag it
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBOperable.DELETED, false);
                        client.update(uri.buildUpon().appendPath(Integer.toString(
                                cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build(), contentValues, null, null); //run an update immediately to unflag for deletion
                    }

                    //update our item
                    syncResult.stats.numUpdates++;
                    batch.add(ContentProviderOperation.newUpdate(uri.buildUpon().
                            appendPath(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()) //schedule an update
                            .withValues(JsonUtils.jsonToContentValues(match, tClass))
                            .build());


                } else {
                    syncResult.stats.numSkippedEntries++; //if it's the same age as the server's, do nothing.
                }
            }
        }

        cursor.close();

        for (JsonObject json : incoming.values()) { //add all the items that weren't matched to our database
            batch.add(ContentProviderOperation.newInsert(uri).withValues(JsonUtils.jsonToContentValues(json, tClass)).build());
            syncResult.stats.numInserts++;
        }



        Log.d(getClass().getSimpleName(), "[" + StringUtils.leftPad(Long.toString(syncResult.stats.numEntries - sResultEntries), 5) + "|" + StringUtils.rightPad(Long.toString(svGet), 5) +"]" +
                " no Δ: " + StringUtils.rightPad(Long.toString((syncResult.stats.numSkippedEntries - sResultSkipped)), 5) +
                " add: " + StringUtils.leftPad(Long.toString((syncResult.stats.numInserts - sResultInserts)), 5)  +
                "|" + StringUtils.rightPad(Long.toString(svCreated), 4) +
                " upd: " + StringUtils.leftPad(Long.toString(syncResult.stats.numUpdates - sResultUpdated), 4) + "|" +
                StringUtils.rightPad(Long.toString(svUpdated), 4) +
                " del: " + StringUtils.leftPad(Long.toString(syncResult.stats.numDeletes - sResultDeleted), 4) + "|" +
                StringUtils.rightPad(Long.toString(svDeleted), 4)+ "   " + tClass.getSimpleName().toLowerCase());

        contentResolver.applyBatch(authority, batch);
        contentResolver.notifyChange(uri, null, false);

        client.delete(uri, null, null);

    }

    private void customSyncEvents(final Account account, final String authority, final ContentProviderClient client, final SyncResult syncResult,
                                  final int type, final boolean modServer, final String url)
            throws RemoteException, InterruptedException, ExecutionException, OperationApplicationException, JSONException,
            VolleyError, IOException, OperationCanceledException, AuthenticatorException {

        final String single = (type == Event.ANNOUNCEMENT) ? "announcement" : (type == Event.SIMPLE_EVENT) ? "simple_event" :
                (type == Event.ATHLETIC_EVENT) ? "athletic_event" : (type == Event.DINING_OPPORTUNITY) ? "dining_opportunity" : null;
        final String plural = (type == Event.ANNOUNCEMENT) ? "announcements" : (type == Event.SIMPLE_EVENT) ? "simple_events" :
                (type == Event.ATHLETIC_EVENT) ? "athletic_events" : (type == Event.DINING_OPPORTUNITY) ? "dining_opportunities" : null;

        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        final long sResultEntries = syncResult.stats.numEntries;
        final long sResultSkipped = syncResult.stats.numSkippedEntries;
        final long sResultUpdated = syncResult.stats.numUpdates;
        final long sResultDeleted = syncResult.stats.numDeletes;
        final long sResultInserts = syncResult.stats.numInserts;
        int svGet = 0;
        int svUpdated = 0;
        int svCreated = 0;
        int svDeleted = 0;

        JsonObject object = ServerCommunicator.get(PeckApp.Constants.Network.API_ENDPOINT + plural, JsonUtils.auth(account));

        HashMap<Integer, JsonObject> incoming = new HashMap<Integer, JsonObject>(); //don't use a sparsearray; hashmap performance will be better when we have a lot of objects, and the data doesn't get reused
        Uri uri = DBUtils.buildLocalUri(Event.class);

        JsonArray array = object.getAsJsonArray(plural);
        for (int i = 0; i < array.size(); i++) {
            JsonObject temp = (JsonObject) array.get(i);
            incoming.put(temp.get(DBOperable.SV_ID).getAsInt(), temp);
        }
        svGet = array.size();

        Cursor cursor = client.query(uri, ArrayUtils.add(DBUtils.getColumns(DBOperable.class), Event.TYPE), Event.TYPE + " = ?", new String[] { Integer.toString(type) }, null);

        while (cursor.moveToNext()) { //iterate through every item in the relevant table
            syncResult.stats.numEntries++;
            JsonObject match = incoming.get(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

            if (match == null) { //if the incoming data doesn't contain the item
                if (cursor.isNull(cursor.getColumnIndex(DBOperable.SV_ID)) && modServer) {
                    //if ours was created since the last time we synced
                    svCreated++;
                    ServerCommunicator.post(PeckApp.Constants.Network.API_ENDPOINT + plural, JsonUtils.wrapJson(single, JsonUtils.cursorToJson(cursor)), JsonUtils.auth(account));  //post it to the server
                } else {
                    //if it's older and we haven't updated it, just delete it
                    syncResult.stats.numDeletes++;
                    batch.add(ContentProviderOperation.newDelete(uri.buildUpon().appendPath(
                            Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()).build());
                }

            } else {
                //there's a match. remove from incoming to prevent being inserted into the database
                incoming.remove(cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)));

                if (cursor.getLong(cursor.getColumnIndex(DBOperable.UPDATED_AT)) > match.get(DBOperable.UPDATED_AT).getAsLong() && modServer) { //our version is newer and we're allowed to modify server data
                    if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) { //and it's been flagged for deletion
                        svDeleted++;
                        ServerCommunicator.delete(PeckApp.Constants.Network.API_ENDPOINT + single + "/" +
                                cursor.getInt(cursor.getColumnIndex(DBOperable.SV_ID)), JsonUtils.auth(account)); //delete it from the server
                    } else { //if it hasn't been
                        svUpdated++; //patch it
                        ServerCommunicator.patch(PeckApp.Constants.Network.API_ENDPOINT + single + "/" + cursor.getLong(cursor.getColumnIndex(DBOperable.SV_ID)),
                                JsonUtils.wrapJson(single, JsonUtils.cursorToJson(cursor)), JsonUtils.auth(account));
                    }
                } else if (cursor.getLong(cursor.getColumnIndex(DBOperable.UPDATED_AT)) < match.get(DBOperable.UPDATED_AT).getAsLong()){ //the server version is newer
                    if (cursor.getInt(cursor.getColumnIndex(DBOperable.DELETED)) > 0) { //if ours was flagged for deletion, unflag it
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBOperable.DELETED, false);
                        client.update(uri.buildUpon().appendPath(Integer.toString(
                                cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build(), contentValues, null, null); //run an update immediately to unflag for deletion
                    }

                    //update our item
                    syncResult.stats.numUpdates++;
                    batch.add(ContentProviderOperation.newUpdate(uri.buildUpon().
                            appendPath(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID)))).build()) //schedule an update
                            .withValues(JsonUtils.jsonToContentValues(match, Event.class))
                            .build());


                } else {
                    syncResult.stats.numSkippedEntries++; //if it's the same age as the server's, do nothing.
                }
            }
        }

        cursor.close();

        for (JsonObject json : incoming.values()) { //add all the items that weren't matched to our database
            ContentValues contentValues = JsonUtils.jsonToContentValues(json, Event.class);
            contentValues.put(Event.TYPE, type);
            batch.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
            syncResult.stats.numInserts++;
        }



        Log.d(getClass().getSimpleName(), "[" + StringUtils.leftPad(Long.toString(syncResult.stats.numEntries - sResultEntries), 5) + "|" + StringUtils.rightPad(Long.toString(svGet), 5) +"]" +
                " no Δ: " + StringUtils.rightPad(Long.toString((syncResult.stats.numSkippedEntries - sResultSkipped)), 5) +
                " add: " + StringUtils.leftPad(Long.toString((syncResult.stats.numInserts - sResultInserts)), 5)  +
                "|" + StringUtils.rightPad(Long.toString(svCreated), 4) +
                " upd: " + StringUtils.leftPad(Long.toString(syncResult.stats.numUpdates - sResultUpdated), 4) + "|" +
                StringUtils.rightPad(Long.toString(svUpdated), 4) +
                " del: " + StringUtils.leftPad(Long.toString(syncResult.stats.numDeletes - sResultDeleted), 4) + "|" +
                StringUtils.rightPad(Long.toString(svDeleted), 4)+ "   " + StringUtils.split(plural, "_")[0]);

        contentResolver.applyBatch(authority, batch);
        contentResolver.notifyChange(uri, null, false);

        client.delete(uri, null, null);
    }



    private static void handleException(Exception e, SyncResult syncResult, String authToken) {
        Log.e(PeckSyncAdapter.class.getSimpleName(), "Exception encountered on sync: " + e.getClass().getSimpleName());
        if (e instanceof VolleyError || e.getCause() != null && e.getCause() instanceof VolleyError) {
            if (e instanceof AuthFailureError || e.getCause() instanceof AuthFailureError)
                AccountManager.get(PeckApp.getContext()).invalidateAuthToken(PeckAccountAuthenticator.ACCOUNT_TYPE, authToken);
            syncResult.stats.numAuthExceptions++;
            if (e instanceof VolleyError && ((VolleyError) e).networkResponse != null) Log.v(PeckSyncAdapter.class.getSimpleName(), Integer.toString(((VolleyError) e).networkResponse.statusCode));
            else if (e.getCause() instanceof VolleyError && ((VolleyError) e.getCause()).networkResponse != null) Log.v(PeckSyncAdapter.class.getSimpleName(), Integer.toString(((VolleyError) e.getCause()).networkResponse.statusCode));
        } else if (e instanceof IOException) {
            syncResult.stats.numIoExceptions++;
        } else if (e instanceof AuthenticatorException) {
            syncResult.stats.numAuthExceptions++;
        } else if (e instanceof JSONException) {
            syncResult.stats.numParseExceptions++;
        } else if (!(   e instanceof RemoteException    || e instanceof InterruptedException ||
                e instanceof ExecutionException || e instanceof OperationApplicationException) ) {
            e.printStackTrace();
        } else {
            e.printStackTrace();
        }

    }


}
