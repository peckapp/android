package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.peck.android.PeckApp;
import com.peck.android.database.DBType;
import com.peck.android.models.DBOperable;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    private static final String DELIM = ", ";
    private static Gson gson = new GsonBuilder().serializeNulls().create();
    private static JsonParser parser = new JsonParser();
    private static final String ARRAY_MARKER = "#jsonarray#";

    private static final HashMap<Class, String[]> columnMap = new HashMap<Class, String[]>();
    private static final HashMap<Class, DBOperable> refList = new HashMap<Class, DBOperable>();

    static {
        try {
            for (Class t : PeckApp.getModelArray()) {
                refList.put(t, (DBOperable)t.newInstance());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("every model must have a nullary constructor.");
        }
    }


    private JsonUtils() {}

    public static <T extends DBOperable> String getDatabaseCreate(Class<T> tClass) {
        String dbCreate = "create table " + getTableName(tClass) + " (";

        for (Field objField : getAllFields(tClass)) {  //this block can cause issues if we have fields with the same names as other fields' serializations. don't do that.
            SerializedName annotation = objField.getAnnotation(SerializedName.class);
            dbCreate += ((annotation == null) ? objField.getName() + " " : annotation.value() + " ");
            DBType type = objField.getAnnotation(DBType.class);
            dbCreate += ((type == null) ? "text" : type.value());
            dbCreate += DELIM;
        }

        dbCreate += "unique (" + DBOperable.SV_ID + "));";

        return dbCreate;
    }

    public static <T extends DBOperable> String getTableName(Class<T> tClass) {
        return "tbl_" + tClass.getSimpleName().toLowerCase();
    }

    @NonNull
    public static ArrayList<Field> getAllFields(@NonNull Class clss){
        ArrayList<Field> ret = new ArrayList<Field>();
        for (Field field : clss.getDeclaredFields()) {
            field.setAccessible(true);
            if (!Modifier.isTransient(field.getModifiers())) ret.add(field);
        }
        Class superClss = clss.getSuperclass();
        if (superClss != null) ret.addAll(getAllFields(superClss));
        return ret;
    }

    public static <T extends DBOperable> String[] getColumns(Class<T> tClass) {
        if (columnMap.get(tClass) == null) {
            try {
                ArrayList<String> columns = new ArrayList<String>();
                T t = tClass.newInstance();
                for (Map.Entry<String, JsonElement> entry : ((JsonObject)new JsonParser().parse(new GsonBuilder().serializeNulls().create().toJson(t, tClass))).entrySet()) {
                    columns.add(entry.getKey());
                }
                columnMap.put(tClass, columns.toArray(new String[columns.size()]));
            } catch (Exception e) { throw new IllegalArgumentException("DBOperables must provide a nullary constructor."); }
        }

        return columnMap.get(tClass);
    }

    public static HashMap<Integer, ContentValues> toMap(JSONObject object) {


    }

    private static <T extends DBOperable> ArrayList<T> parseJson(JsonElement obj, Class<T> tClass) {
        ArrayList<T> ret = new ArrayList<T>();
        if (obj.isJsonObject()) {
            if (wrapsJsonElement((JsonObject) obj)) {
                ret.addAll(parseJson(((JsonObject)obj).entrySet().iterator().next().getValue(), tClass));
            } else ret.add(gson.fromJson(obj, tClass));
        } else if (obj.isJsonArray()) {
            for (JsonElement arrayElement : (JsonArray)obj) {
                ret.addAll(parseJson(arrayElement, tClass));
            }
        } //if it's none of those, it's a jsonnull or a primitive, and we don't handle either of those, maybe todo: throw an exception
        return ret;
    }




    @NonNull
    public static <T> ContentValues toContentValues(T t) throws JsonParseException {
        ContentValues ret = new ContentValues();

        JsonObject object = (JsonObject)parser.parse(gson.toJson(t, t.getClass()));

        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    ret.put(field.getKey(), element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsDouble() != ((double)element.getAsInt())) ret.put(field.getKey(), element.getAsDouble());
                    else ret.put(field.getKey(), element.getAsInt());
                }
            } else if (element.isJsonArray()) {
                ret.put(field.getKey(), ARRAY_MARKER + element.getAsJsonArray().toString());
            }
        }

        if (ret.containsKey(DBOperable.LOCAL_ID)) ret.remove(DBOperable.LOCAL_ID);

        return ret;
    }






    public static <T extends DBOperable> ContentValues fromJson(JsonObject jsonObject, Class<T> tClass) {
        ContentValues contentValues = new ContentValues();


    }


    @NonNull
    public static <T> T fromCursor(Cursor cursor, Class<T> tClass) {
        T ret;
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

        ret = gson.fromJson(object, tClass);

        return ret;
    }

}
