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

import org.apache.commons.lang3.StringUtils;

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
        //todo: remove "on conflict replace". we don't want any conflicts going into the db
        return "create table " + getTableName(tClass) + " (" + StringUtils.join(fieldToCreatorString(tClass), DELIM) + DELIM + "unique (" + DBOperable.SV_ID + ") on conflict replace);";
    }

    public static <T extends DBOperable> String getTableName(Class<T> tClass) {
        return "tbl_" + tClass.getSimpleName().toLowerCase();
    }

    public static String getSerializedFieldName(Field field) {
        SerializedName name = field.getAnnotation(SerializedName.class);
        return name == null ? field.getName() : name.value();
    }


    public static ArrayList<String> fieldToCreatorString(Class tClass) {
        ArrayList<String> ret = new ArrayList<String>();
        for (Field objField : getAllFields(tClass)) {  //this block can cause issues if we have fields with the same names as other fields' serializations. don't do that.
            DBType type = objField.getAnnotation(DBType.class);
            ret.add(getSerializedFieldName(objField) + " " + ((type == null) ? "text" : type.value()));
        }
        return ret;
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
            } catch (InstantiationException e) { throw new IllegalArgumentException("DBOperables must provide a nullary constructor.", e);
            } catch (IllegalAccessException e) { throw new IllegalArgumentException("couldn't instantiate a " + tClass.getSimpleName() + " object", e); }
        }

        return columnMap.get(tClass);
    }

}
