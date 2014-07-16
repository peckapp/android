package com.peck.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.peck.android.database.DBUtils;
import com.peck.android.database.DatabaseManager;
import com.peck.android.models.DBOperable;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class InternalContentProvider extends ContentProvider {

    private final static String AUTHORITY = "com.peck.android.provider.all";
    private final static int[] URIs_ALL = { 10, 20, 30, 40, 50, 60 };

    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        for (int i = 0; i < PeckApp.getModelArray().length; i++) {
            uriMatcher.addURI(AUTHORITY, DBUtils.getTableName(PeckApp.getModelArray()[i]), URIs_ALL[i]);                 //operate on the whole list
            uriMatcher.addURI(AUTHORITY, DBUtils.getTableName(PeckApp.getModelArray()[i]) + "/#", URIs_ALL[i] + 1);      //operate on a model by local id
        }
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    private String trimUri(Uri uri) {
        return (uri.getPath());
    }

    private String extendSelection(String selection, String append) {
        String s = new StringBuilder().append(((selection != null && selection.length() != 0) ? selection + " and " : "")).append(append).toString();
        Log.v(getClass().getSimpleName(), s);
        return s;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = uriMatcher.match(uri);

        Cursor cursor = null;

        for (int i = 0; i < URIs_ALL.length;  i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query(DBUtils.getTableName(PeckApp.getModelArray()[i]), projection, extendSelection(selection, DBOperable.DELETED + " IS NOT ?"), ArrayUtils.add(selectionArgs, "0"), null, null, sortOrder);
                break;
            } else if (uriType == URIs_ALL[i] + 1) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query(DBUtils.getTableName(PeckApp.getModelArray()[i]), projection, extendSelection(selection, DBOperable.LOCAL_ID + " = ? and " +
                        DBOperable.DELETED + " IS NOT ?"), ArrayUtils.addAll(selectionArgs, uri.getLastPathSegment(), "0" ), null, null, sortOrder);
                break;
            }
        }

        if (cursor == null) throw new IllegalArgumentException();

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.v(getClass().getSimpleName(), "[" + cursor.getCount() + "]query: " + trimUri(uri) + ", type: " + uriType);


        return cursor;
    }


    @Override
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {
        //Log.v(getClass().getSimpleName(), "Insert: " + trimUri(uri) + ": " + contentValues);
        int uriType = uriMatcher.match(uri);
        long insertId = -1;

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                insertId = database.insert(DBUtils.getTableName(PeckApp.getModelArray()[i]), null, contentValues);
                DatabaseManager.closeDB();
                break;
            }
        }

        if (insertId == -1) throw new IllegalArgumentException("Couldn't insert ContentValues into the database: " + contentValues);

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.withAppendedPath(uri, Long.toString(insertId));
    }


    /**
     *
     * Pass [base uri]/[path]/[id] for a deletion marker. Sets a deletion flag on the item to true. If unsure, use this format.
     * Pass [base uri]/[path]:
     *      Selection null - sweeps the database for items marked to be deleted. Should be called by the SyncAdapter exclusively.
     *      Selection non-null - marks all items in selection as for deletion
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

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                if (selection == null) {
                    Log.v(getClass().getSimpleName(), "Sweep for deletes: " + trimUri(uri));
                    SQLiteDatabase database = DatabaseManager.openDB();
                    deleted = database.delete(DBUtils.getTableName(PeckApp.getModelArray()[i]), DBOperable.DELETED + " = ?", new String[]{"1"});
                    DatabaseManager.closeDB();
                } else {
                    Log.v(getClass().getSimpleName(), "Mark for delete: " + trimUri(uri));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBOperable.DELETED, true);
                    update(uri, contentValues, selection, selectionArgs);
                }
                break;
            } else if (uriType == URIs_ALL[i] + 1) {
                Log.v(getClass().getSimpleName(), "Mark for delete: " + trimUri(uri));
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBOperable.DELETED, true);
                update(uri, contentValues, selection, selectionArgs);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return deleted;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.v(getClass().getSimpleName(), "Update: " + trimUri(uri) + ": " + contentValues);
        int uriType = uriMatcher.match(uri);
        int updated = 0;

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                updated = database.update(DBUtils.getTableName(PeckApp.getModelArray()[i]), contentValues, selection, selectionArgs);
                DatabaseManager.closeDB();
                break;
            } else if (uriType == URIs_ALL[i] + 1) {
                SQLiteDatabase database = DatabaseManager.openDB();
                updated = database.update(DBUtils.getTableName(PeckApp.getModelArray()[i]), contentValues, extendSelection(selection, DBOperable.LOCAL_ID + " = ?"),
                        ArrayUtils.add(selectionArgs, uri.getLastPathSegment()));
                DatabaseManager.closeDB();
                break;
            }

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }

    @Override
    public synchronized String getType(Uri uri) {
        return null;
    }

}
