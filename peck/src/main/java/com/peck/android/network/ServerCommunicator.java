package com.peck.android.network;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
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
    private static RestAdapter apiAdapter = new RestAdapter.Builder().setEndpoint(PeckApp.Constants.Network.BASE_URL).
            setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("User-Agent", "Peck Android, v. 1.0");
                }
            }).setConverter(new Converter() {
        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            if (!body.mimeType().equals("application/json"))
                throw new ConversionException("Data received from the server was not json.");
            return (new JsonParser().parse(body.toString()));
        }

        @Override
        public TypedOutput toBody(Object object) {
            return new TypedByteArray("application/json", ((JsonObject) object).getAsString().getBytes());
        }
    }).build();

    public static SimpleJsonHandler jsonService = apiAdapter.create(SimpleJsonHandler.class);

    public static class Jpeg extends TypedByteArray {
        TypedByteArray out;
        String fileName;

        public Jpeg(String fileName, Bitmap bitmap) {
            super("image/jpeg", null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            out = new TypedByteArray("image/jpeg", outputStream.toByteArray());
            this.fileName = fileName;
        }

        @Override
        public String fileName() {
            return fileName;
        }

        @Override
        public byte[] getBytes() {
            return out.getBytes();
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(this.out.getBytes());
        }


    }

    public interface SimpleJsonHandler {
        @FormUrlEncoded
        @GET("/api/{type}/{id}")
        JsonObject show(@Path("type") String type, @Path("id") String id, @FieldMap Map<String, String> authentication, @QueryMap Map<String, String> urlParams);

        @FormUrlEncoded
        @GET("/api/{type}")
        JsonObject get(@Path("type") String type, @FieldMap Map<String, String> authentication, @QueryMap Map<String, String> urlParams);

        @FormUrlEncoded
        @POST("/api/{type}")
        JsonObject post(@Path("type") String type, @Body JsonObject body, @FieldMap Map<String, String> authentication);

        @FormUrlEncoded
        @Multipart
        @POST("/api/{type}")
        JsonObject post(@Path("type") String type, @Body JsonObject body, @FieldMap Map<String, String> authentication, @Part("image") Jpeg image);

        @FormUrlEncoded
        @POST("/api/access")
        JsonObject login(@FieldMap Map<String, String> fields);

        @FormUrlEncoded
        @PATCH("/api/{type}/{id}")
        JsonObject patch(@Path("type") String type, @Path("id") String id, @Body JsonObject body, @FieldMap Map<String, String> authentication);

        @FormUrlEncoded
        @PATCH("/api/users/{id}/super_create")
        JsonObject superCreate(@Path("id") String userId, @Body JsonObject user, @FieldMap Map<String, String> authentication);

        @FormUrlEncoded
        @PATCH("/api/{type}/{id}")
        JsonObject patchImage(@Path("type") String type, @Path("id") String id, @Part("image") Jpeg image);

        @FormUrlEncoded
        @DELETE("/api/{type}/{id}")
        JsonObject delete(@Path("type") String type, @Path("id") String id, @FieldMap Map<String, String> authentication);

    }

}
