package com.peck.android.database;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.models.DBOperable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mammothbane on 7/15/2014.
 */
public class DBUtils {
    private static final String DELIM = ", ";
    private static final HashMap<Class, String[]> columnMap = new HashMap<Class, String[]>();
    public static Gson gson = new GsonBuilder().serializeNulls().create();

    public static <T extends DBOperable> String getDatabaseCreate(Class<T> tClass) {
        String dbCreate = "create table " + getTableName(tClass) + " (";

        for (Field objField : getAllFields(tClass)) {  //this block can cause issues if we have fields with the same names as other fields' serializations. don't do that.
            SerializedName annotation = objField.getAnnotation(SerializedName.class);
            dbCreate += ((annotation == null) ? objField.getName() + " " : annotation.value() + " ");
            DBType type = objField.getAnnotation(DBType.class);
            dbCreate += ((type == null) ? "text" : type.value());
            dbCreate += DELIM;
        }

        dbCreate += "unique (" + DBOperable.SV_ID + ") on conflict replace);";

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
            } catch (Exception e) { throw new IllegalArgumentException("DBOperables must provide a nullary constructor.", e); }
        }

        return columnMap.get(tClass);
    }

}
