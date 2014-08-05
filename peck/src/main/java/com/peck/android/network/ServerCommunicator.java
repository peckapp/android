package com.peck.android.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by mammothbane on 7/22/2014.
 */
public class ServerCommunicator {
    private static final boolean debug = false;
    private static RestAdapter apiAdapter = new RestAdapter.Builder().setEndpoint(!debug ? PeckApp.Constants.Network.BASE_URL : "http://192.168.0.24").
            setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("User-Agent", "Peck Android, v. 1.0");
                }
            }).setConverter(new Converter() {
        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            try {
                String json = IOUtils.toString(body.in());
                Log.v(ServerCommunicator.class.getSimpleName(), new JSONObject(json).toString(2));
                if (!body.mimeType().contains("application/json"))
                    throw new ConversionException("Data received from the server was not json.");
                return (new JsonParser().parse(json));
            } catch (IOException e) {
                throw new ConversionException(e);
            } catch (JSONException ignore) {
                throw new ConversionException(ignore);
            }
        }

        @Override
        public TypedOutput toBody(Object object) {
            return new TypedByteArray("application/json", ((JsonObject) object).getAsString().getBytes());
        }
    }).setLogLevel(RestAdapter.LogLevel.BASIC).build();



    public static SimpleJsonHandler jsonService = apiAdapter.create(SimpleJsonHandler.class);

    public static class Jpeg implements TypedOutput {
        TypedByteArray out;
        String fileName;

        public Jpeg(String fileName, Bitmap bitmap, int maxSizeInBytes) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            double ratio = 1.d;
            Bitmap temp = null;

            do {
                if (temp != null) temp.recycle();
                outputStream.reset();
                if (ratio != 1.d) temp = Bitmap.createScaledBitmap(bitmap, (int)(width*ratio), (int)(height*ratio), true);
                else temp = bitmap;
                temp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                ratio *= 1/Math.sqrt(2);
            } while (outputStream.size() > maxSizeInBytes);

            out = new TypedByteArray("image/jpeg", outputStream.toByteArray());
            this.fileName = fileName;

            try {
                outputStream.flush();
                outputStream.close();
            } catch (Throwable ignore) {}
        }

        @Override
        public String fileName() {
            return fileName;
        }

        public byte[] getBytes() {
            return out.getBytes();
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(this.out.getBytes());
        }

        @Override
        public long length() {
            return this.out.length();
        }

        public InputStream in() throws IOException {
            return out.in();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof TypedOutput && out.equals(o));
        }

        @Override
        public int hashCode() {
            return out.hashCode();
        }

        @Override
        public String mimeType() {
            return out.mimeType();
        }
    }

    public static class TypedJsonBody implements TypedOutput {
        TypedByteArray out;

        public TypedJsonBody(JsonObject json) {
            out = new TypedByteArray("application/json", (json != null) ? json.toString().getBytes() : new byte[0]);
        }

        @Override
        public String fileName() {
            return null;
        }

        public byte[] getBytes() {
            return out.getBytes();
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(this.out.getBytes());
        }

        @Override
        public long length() {
            return this.out.length();
        }

        public InputStream in() throws IOException {
            return out.in();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof TypedOutput && out.equals(o));
        }

        @Override
        public int hashCode() {
            return out.hashCode();
        }

        @Override
        public String mimeType() {
            return out.mimeType();
        }

    }

    public interface SimpleJsonHandler {
        @GET("/api/{type}/{id}")
        JsonObject show(@Path("type") String type, @Path("id") String id, @QueryMap Map<String, String> urlParams);

        @GET("/api/{type}")
        JsonObject get(@Path("type") String type, @QueryMap Map<String, String> urlParams);

        @POST("/api/{type}")
        JsonObject post(@Path("type") String type, @Body TypedJsonBody body, @QueryMap Map<String, String> authentication);

        @POST("/api/{type}")
        void post(@Path("type") String type, @Body TypedJsonBody body, @QueryMap Map<String, String> authentication, Callback<JsonObject> callback);

        @Multipart
        @POST("/api/{type}")
        JsonObject post(@Path("type") String type, @QueryMap Map<String, String> authentication, @Part("image") Jpeg image);

        @Multipart
        @POST("/api/{type}")
        void post(@Path("type") String type, @QueryMap Map<String, String> authentication, @Part("image") Jpeg image, Callback<JsonObject> callBack);

        @POST("/api/users")
        JsonObject createUser();

        @POST("/api/access")
        JsonObject login(@QueryMap Map<String, String> fields);

        @PATCH("/api/{type}/{id}")
        JsonObject patch(@Path("type") String type, @Path("id") String id, @Body TypedJsonBody body, @QueryMap Map<String, String> authentication);

        @PATCH("/api/users/{id}/super_create")
        JsonObject superCreate(@Path("id") String userId, @Body TypedJsonBody user, @QueryMap Map<String, String> authentication);

        @PATCH("/api/{type}/{id}")
        JsonObject patchImage(@Path("type") String type, @Path("id") String id, @Part("image") Jpeg image, @QueryMap Map<String, String> authentication);

        @DELETE("/api/{type}/{id}")
        JsonObject delete(@Path("type") String type, @Path("id") String id, @QueryMap Map<String, String> authentication);

    }

}
