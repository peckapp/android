package com.peck.android.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.BuildConfig;
import com.peck.android.PeckApp;
import com.peck.android.annotations.Header;
import com.peck.android.database.DBUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mammothbane on 6/20/2014.
 *
 * prepares model objects for insertion into the database
 * and restores them from database query cursors.
 *
 */


public class JsonUtils {


    public static final String ARRAY_MARKER = "#jsonarray#";
    public static JsonParser parser = new JsonParser();

    private JsonUtils() {}

    public static ContentValues jsonToContentValues(JsonObject object, Class tClass) {
        ContentValues ret = new ContentValues();

        for (Field field : DBUtils.getAllFields(tClass)) {
            String s = DBUtils.getSerializedFieldName(field);
            JsonElement element = object.get(s);
            if (element != null) {
                if (element.isJsonPrimitive()) {
                    if (element.getAsJsonPrimitive().isString()) {
                        ret.put(s, element.getAsString());
                    } else if (element.getAsJsonPrimitive().isNumber()) {
                        if (field.getType().equals(double.class) || field.getType().equals(float.class)
                                || field.getType().equals(Double.class) || field.getType().equals(Float.class)) {
                            ret.put(s, element.getAsDouble());
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) ret.put(s, element.getAsLong());
                        else ret.put(s, element.getAsInt());
                    } else if (element.getAsJsonPrimitive().isBoolean()) {
                        ret.put(s, element.getAsBoolean());
                    }
                } else if (element.isJsonArray()) {
                    ret.put(s, ARRAY_MARKER + element.getAsJsonArray().toString());
                }
            }
        }

        return ret;
    }

    public static JsonObject cursorToJson(Cursor cursor) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            int colType = cursor.getType(i);
            String colName = cursor.getColumnName(i);
            switch (colType) {
                case Cursor.FIELD_TYPE_STRING:
                    String s = cursor.getString(i);
                    if (s.contains(ARRAY_MARKER)) object.add(colName, parser.parse(s.replace(ARRAY_MARKER, "")));
                    else object.addProperty(colName, s);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    object.addProperty(colName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    object.addProperty(colName, cursor.getDouble(i));
                    break;
                default:
                    //todo: throw an exception that the column couldn't be added
                    break;
            }

        }
        return object;
    }

    public static JsonObject wrapJson(String objHeader, JsonObject object) {
        JsonObject ret = new JsonObject();
        ret.add(objHeader, object);
        return ret;
    }

    /**
     * blocking method to build authentication parameters
     *
     * @param account the authenticating account
     * @return the authentication map
     */
    public static Map<String, String> auth(Account account) throws IOException, OperationCanceledException, AuthenticatorException {
        if (account == null) return new HashMap<String, String>();
        AccountManager accountManager = AccountManager.get(PeckApp.getContext());
        Map<String, String> auth = new HashMap<String, String>();
        String apiKey = accountManager.getUserData(account, PeckAccountAuthenticator.API_KEY);
        if (BuildConfig.DEBUG && apiKey == null) { throw new AuthenticatorException("API key can't be null"); }

        String authToken = null;
        if (!account.name.equals(PeckAccountAuthenticator.TEMP_NAME)) {
            authToken = accountManager.blockingGetAuthToken(account, PeckAccountAuthenticator.TOKEN_TYPE, false);
        }

        if (authToken != null) auth.put("authentication[authentication_token]", authToken);
        auth.put("authentication[user_id]", accountManager.getUserData(account, PeckAccountAuthenticator.USER_ID));
        auth.put("authentication[institution_id]", accountManager.getUserData(account, PeckAccountAuthenticator.INSTITUTION));
        auth.put("authentication[api_key]", apiKey);

        return auth;
    }

    public static String getJsonHeader(Class tClass, boolean plural) {
        Header header = (Header)tClass.getAnnotation(Header.class);
        if (BuildConfig.DEBUG && (header == null || header.singular() == null || header.plural() == null)) throw new IllegalArgumentException(tClass.getSimpleName() + " does not have a header");
        if (plural) return header.plural();
        else return header.singular();
    }
}