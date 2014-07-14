package com.peck.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.peck.android.database.DatabaseManager;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 7/14/2014.
 */
public class InternalContentProvider extends ContentProvider {

    private final static String AUTHORITY = "com.peck.android.provider.all";
    private final static DBOperable[] TYPES = { new Event(), new Circle(), new User(), new Comment(), new Locale(), new Peck()};
    private final static int[] URIs_ALL = { 10, 20, 30, 40, 50, 60 };

    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        for (int i = 0; i < TYPES.length; i++) {
            uriMatcher.addURI(AUTHORITY, TYPES[i].getTableName(), URIs_ALL[i]);                 //return the whole list
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
                cursor = database.query(TYPES[i].getTableName(), projection, selection, selectionArgs, null, null, sortOrder);
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
                insertId = database.insert(TYPES[i].getTableName(), null, contentValues);
                DatabaseManager.closeDB();
                break;
            }
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.withAppendedPath(uri, Long.toString(insertId));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int deleted = 0;

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                deleted = database.delete(TYPES[i].getTableName(), selection, selectionArgs);
                DatabaseManager.closeDB();
                break;
            }
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int updated = 0;

        for (int i = 0; i < URIs_ALL.length; i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                updated = database.update(TYPES[i].getTableName(), contentValues, selection, selectionArgs);
                DatabaseManager.closeDB();
                break;
            }
        }

        return updated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}
