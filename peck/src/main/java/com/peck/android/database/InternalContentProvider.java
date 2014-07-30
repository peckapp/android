package com.peck.android.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.peck.android.PeckApp;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class InternalContentProvider extends ContentProvider {

    private final static String AUTHORITY = "com.peck.android.provider.all";
    private final static SparseArray<Class> URIs_ALL = new SparseArray<Class>();

    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final static int PARTITION = 100;


    static {
        for (int i = 0; i < PeckApp.getModelArray().length; i++) {
            URIs_ALL.put(i, PeckApp.getModelArray()[i]);
            URIs_ALL.put(PARTITION + i, PeckApp.getModelArray()[i]);
            uriMatcher.addURI(AUTHORITY, DBUtils.getTableName(PeckApp.getModelArray()[i]), i);                          //operate on the whole list
            uriMatcher.addURI(AUTHORITY, DBUtils.getTableName(PeckApp.getModelArray()[i]) + "/#", PARTITION + i);      //operate on a model by local id
        }
        uriMatcher.addURI(AUTHORITY, DBUtils.getTableName(Circle.class) + "/#/users", 1001);
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    private static String trimUri(Uri uri) {
        return (uri.getPath());
    }

    private static String extendSelection(String selection, String append) {
        return ((selection != null && selection.length() != 0) ? selection + " and " : "") + append;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = uriMatcher.match(uri);
        Cursor cursor = null;

        SQLiteDatabase database = DatabaseManager.openDB();

        if (uriType == 1001) {
            Cursor temp = database.query(DBUtils.getTableName(Circle.class), new String[] { DBOperable.LOCAL_ID, Circle.MEMBERS },
                    DBOperable.LOCAL_ID + " = ?", new String[] { uri.getPathSegments().get(1) }, null, null, null);
            if (temp.moveToFirst()) {
                ArrayList<Integer> ints = new Gson().fromJson(JsonUtils.cursorToJson(temp).getAsJsonArray(Circle.MEMBERS), new TypeToken<ArrayList<Integer>>() {
                }.getType());

                if (ints.size() > 0) {
                    String s = "(" + StringUtils.join(ints, ",") + ")";

                    cursor = database.query(DBUtils.getTableName(User.class), projection, extendSelection(selection, DBOperable.DELETED + " IS NOT ? AND " + DBOperable.SV_ID + (" IN " + s)),
                            ArrayUtils.addAll(selectionArgs, "0"), null, null, sortOrder);
                }
            }
        } else if (uriType < PARTITION) {
            cursor = database.query(DBUtils.getTableName(URIs_ALL.get(uriType)), projection, extendSelection(selection, DBOperable.DELETED + " IS NOT ?"), ArrayUtils.add(selectionArgs, "0"), null, null, sortOrder);
        } else if (uriType >= PARTITION) {
            cursor = database.query(DBUtils.getTableName(URIs_ALL.get(uriType)), projection, extendSelection(selection, DBOperable.LOCAL_ID + " = ? and " +
                    DBOperable.DELETED + " IS NOT ?"), ArrayUtils.addAll(selectionArgs, uri.getLastPathSegment(), "0"), null, null, sortOrder);
        }

        if (cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = uriMatcher.match(uri);
        long insertId = -1;

        if (uriType >= 1000) throw new IllegalArgumentException("This uri should only be used for queries.");

        if (uriType < PARTITION) {
            insertId = DatabaseManager.openDB().insert(DBUtils.getTableName(URIs_ALL.get(uriType)), null, contentValues);
        } else throw new IllegalArgumentException("Uri may not specify a localId to insert.");

        if (insertId == -1) throw new IllegalArgumentException("Couldn't insert ContentValues into the database: " + contentValues);
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.withAppendedPath(uri, Long.toString(insertId));
    }


    /**
     *
     * Pass [base uri]/[path]/[id] for a deletion marker. Sets a deletion flag on the item to true. If unsure, use this format.
     * Pass [base uri]/[path]:
     *      Selection null - sweeps the database for items marked to be deleted. Should be called by the SyncAdapter exclusively.
     *      Selection non-null - marks all items in selection for deletion
     *
     *
     * @param uri the uri to delete or mark for deletion
     * @param selection the selection of items to delete
     * @param selectionArgs the arugments to selection
     * @return the number of items deleted or marked for deletion
     */
    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int deleted = 0;

        if (uriType >= 1000) throw new IllegalArgumentException("This uri should only be used for queries.");

        if (uriType < PARTITION) {
            if (selection == null) {
                Log.v(getClass().getSimpleName(), "Sweep for deletes: " + trimUri(uri));
                SQLiteDatabase database = DatabaseManager.openDB();
                deleted = database.delete(DBUtils.getTableName(URIs_ALL.get(uriType)), DBOperable.DELETED + " = ?", new String[]{"1"});
            } else {
                Log.v(getClass().getSimpleName(), "Mark for delete: " + trimUri(uri));
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBOperable.DELETED, true);
                update(uri, contentValues, selection, selectionArgs);
            }

        } else if (uriType >= PARTITION) {
            Log.v(getClass().getSimpleName(), "Mark for delete: " + trimUri(uri));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOperable.DELETED, true);
            update(uri, contentValues, selection, selectionArgs);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return deleted;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.v(getClass().getSimpleName(), "Update: " + trimUri(uri) + ": " + contentValues);
        int uriType = uriMatcher.match(uri);
        int updated = 0;

        if (uriType >= 1000) throw new IllegalArgumentException("This uri should only be used for queries.");

        if (uriType < PARTITION) {
            SQLiteDatabase database = DatabaseManager.openDB();
            updated = database.update(DBUtils.getTableName(URIs_ALL.get(uriType)), contentValues, selection, selectionArgs);
        } else if (uriType >= PARTITION) {
            SQLiteDatabase database = DatabaseManager.openDB();
            updated = database.update(DBUtils.getTableName(URIs_ALL.get(uriType)), contentValues, extendSelection(selection, DBOperable.LOCAL_ID + " = ?"),
                    ArrayUtils.add(selectionArgs, uri.getLastPathSegment()));
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }

    @Override
    public synchronized String getType(Uri uri) {
        return null;
    }
}
