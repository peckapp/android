package com.peck.android.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.managers.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 7/22/2014.
 */
public class ServerCommunicator {
    private static JsonObject sendJson(String url, int method, JsonObject data, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        if (auth == null) auth = new HashMap<String, String>();

        StringBuilder stringBuilder = new StringBuilder(((url.charAt(url.length() - 1) == '/') ? url.substring(0, url.length() - 1) : url) + "?");
        for (String key : auth.keySet()) {
            stringBuilder.append(key).append("=").append((auth.get(key) == null) ? "" : auth.get(key)).append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        Log.v("ServerCommunicator", stringBuilder.toString());
        JsonObjectRequest request = new JsonObjectRequest(method, stringBuilder.toString(), (data != null) ? new JSONObject(data.toString()) : null, future, future) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Content-Type", "application/json");
                return map;
            }
        };

        try {
            PeckApp.getRequestQueue().add(request);
            return ((JsonObject) new JsonParser().parse(future.get().toString()));
        } catch (ExecutionException e) {
            if (e.getCause() instanceof VolleyError) {
                VolleyError error = ((VolleyError) e.getCause());
                if (error.networkResponse.statusCode == 401 && auth.containsKey(PeckAccountAuthenticator.AUTH_TOKEN)) {
                    LoginManager.invalidateAuthToken(auth.get(PeckAccountAuthenticator.EMAIL));
                }
                throw error;
            } else {
                throw e;
            }
        }
    }

    public static JsonObject get(String url, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return sendJson(url, Request.Method.GET, null, auth);
    }

    public static JsonObject post(String url, JsonObject object, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return sendJson(url, Request.Method.POST, object, auth);
    }

    public static JsonObject patch(String url, JsonObject object, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return sendJson(url, Request.Method.PATCH, object, auth);
    }

    public static boolean delete(String url, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        sendJson(url, Request.Method.DELETE, null, auth);
        //todo: what happens when we run a delete? how do we return?
        return true;
    }
}
