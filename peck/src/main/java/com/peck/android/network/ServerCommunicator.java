package com.peck.android.network;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 7/22/2014.
 */
public class ServerCommunicator {
    private static JsonObject send(String url, int method, JsonObject data, final Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String newUrl = String.format((url.charAt(url.length() - 1) == '/') ? url.substring(0, url.length() - 1) : url + "?authentication=[%1:%2,%3:%4,%5:%6]",
                "api_key", auth.get("api_key"),
                "user_id", auth.get("user_id"),
                "institution_id", auth.get("institution_id"));

        StringBuilder sb = new StringBuilder(url);
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.addParameter("authentication", );

        } catch (URISyntaxException e) { e.printStackTrace(); }
        if (sb.charAt(sb.length()-1) == '/') sb.deleteCharAt(sb.length() - 1);
        sb.append("?");
        for (String s : auth.keySet()) {
            sb.append(s).append("=").append(auth.get(s)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);

        JsonObjectRequest request = new JsonObjectRequest(method, sb.toString(), (data != null) ? new JSONObject(data.toString()) : null, future, future);

        PeckApp.getRequestQueue().add(request);
        return ((JsonObject)new JsonParser().parse(future.get().toString()));
    }

    public static JsonObject get(String url, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.GET, null, auth);
    }

    public static JsonObject post(String url, JsonObject object, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.POST, object, auth);
    }

    public static JsonObject patch(String url, JsonObject object, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.PATCH, object, auth);
    }

    public static boolean delete(String url, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        send(url, Request.Method.DELETE, null,auth);
        //todo: what happens when we run a delete? how do we return?
        return true;
    }
}
