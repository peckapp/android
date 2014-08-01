package com.peck.android.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.managers.LoginManager;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && auth.containsKey(PeckAccountAuthenticator.AUTH_TOKEN)) {
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

    public static JsonObject post(String url, JsonObject object, Bitmap image, String imageName, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        if (auth == null) auth = new HashMap<String, String>();

        StringBuilder stringBuilder = new StringBuilder(((url.charAt(url.length() - 1) == '/') ? url.substring(0, url.length() - 1) : url) + "?");
        for (String key : auth.keySet()) {
            stringBuilder.append(key).append("=").append((auth.get(key) == null) ? "" : auth.get(key)).append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        Log.v("ServerCommunicator", stringBuilder.toString());
        Request<JSONObject> request = new ImageJsonPost(stringBuilder.toString(), future, future, image, object, imageName);

        try {
            PeckApp.getRequestQueue().add(request);
            return ((JsonObject) new JsonParser().parse(future.get().toString()));
        } catch (ExecutionException e) {
            if (e.getCause() instanceof VolleyError) {
                VolleyError error = ((VolleyError) e.getCause());
                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && auth.containsKey(PeckAccountAuthenticator.AUTH_TOKEN)) {
                    LoginManager.invalidateAuthToken(auth.get(PeckAccountAuthenticator.EMAIL));
                }
                throw error;
            } else {
                throw e;
            }
        }
    }

    public static JsonObject patch(String url, JsonObject object, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return sendJson(url, Request.Method.PATCH, object, auth);
    }

    public static boolean delete(String url, Map<String, String> auth) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        sendJson(url, Request.Method.DELETE, null, auth);
        //todo: what happens when we run a delete? how do we return?
        return true;
    }

    private static class ImageJsonPost extends Request<JSONObject> {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        Response.Listener<JSONObject> listener;
        String boundary = Integer.toString(Math.abs(new Random().nextInt()));

        private ImageJsonPost(String url, Response.Listener<JSONObject> respListener, Response.ErrorListener listener, Bitmap image, JsonObject object, String imageFileName) {
            super(Method.POST, url, listener);
            this.listener = respListener;

            builder.setBoundary(boundary);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            try {
                //builder.addTextBody("announcement", object.toString(), ContentType.APPLICATION_JSON);
                builder.addBinaryBody("image", stream.toByteArray(), ContentType.create("image/jpeg"), imageFileName);
            } finally {
                try {
                    stream.flush();
                    stream.close();
                } catch (Throwable ignore) {}
            }
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                builder.build().writeTo(stream);
                return stream.toByteArray();
            } catch (IOException e) {
                VolleyLog.e("Failed to convert body to byte array.");
            } finally {
                try {
                    stream.close();
                } catch (Throwable ignore) {}
            }
            return null;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(final NetworkResponse response) {
            return new JsonObjectRequest(null, null, null, null) {
                public Response<JSONObject> parse(NetworkResponse response1) {
                    return parseNetworkResponse(response);
                }
            }.parse(response);
        }

        @Override
        protected void deliverResponse(JSONObject response) {
            listener.onResponse(response);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> ret = new HashMap<String, String>();
            ret.put("Content-Type", "multipart/form-data; boundary=" + boundary);
            return ret;
        }
    }

}
