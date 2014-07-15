package com.peck.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = uriMatcher.match(uri);

        Cursor cursor = null;

        for (int i = 0; i < URIs_ALL.length;  i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query(DBUtils.getTableName(PeckApp.getModelArray()[i]), projection, selection + " and " + DBOperable.DELETED + " = ?", ArrayUtils.add(selectionArgs, "false"), null, null, sortOrder);
                break;
            } else if (uriType == URIs_ALL[i] + 1) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query(DBUtils.getTableName(PeckApp.getModelArray()[i]), projection, selection + " and " + DBOperable.LOCAL_ID + " = ?" + " and " +
                        DBOperable.DELETED + " = ?", ArrayUtils.addAll(selectionArgs, uri.getLastPathSegment(), "false" ), null, null, sortOrder);
                break;
            }
        }

        if (cursor == null) throw new IllegalArgumentException();

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int deleted = 0;

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                if (selection == null) {
                    SQLiteDatabase database = DatabaseManager.openDB();
                    deleted = database.delete(DBUtils.getTableName(PeckApp.getModelArray()[i]), DBOperable.DELETED + " = ?", new String[]{"true"});
                    DatabaseManager.closeDB();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBOperable.DELETED, true);
                    update(uri, contentValues, selection, selectionArgs);
                }
                break;
            } else if (uriType == URIs_ALL[i] + 1) {
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
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
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
                updated = database.update(DBUtils.getTableName(PeckApp.getModelArray()[i]), contentValues, selection + " and " + DBOperable.LOCAL_ID + " = ?", ArrayUtils.add(selectionArgs, uri.getLastPathSegment()));
                DatabaseManager.closeDB();
                break;
            }

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}
