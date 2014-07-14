package com.peck.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
public class PeckContentProvider extends ContentProvider {

    private final static String AUTHORITY = "com.peck.android.provider.all";
    private final static DBOperable[] TYPES = { new Event(), new Circle(), new User(), new Comment(), new Locale(), new Peck()};
    private final static int[] URIs_ALL = { 10, 20, 30, 40, 50, 60 };

    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        for (int i = 0; i < TYPES.length; i++) {
            uriMatcher.addURI(AUTHORITY, TYPES[i].getTableName(), URIs_ALL[i]);
            uriMatcher.addURI(AUTHORITY, TYPES[i].getTableName() + "/#", URIs_ALL[i] + 1);
        }
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        int uriType = uriMatcher.match(uri);

        Cursor cursor;

        for (int i = 0; i < URIs_ALL.length;  i++) {
            if (uriType == URIs_ALL[i]) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query(TYPES[i].getTableName(), TYPES[i].getColumns(), )


            } else if (uriType == i + 1) {
                SQLiteDatabase database = DatabaseManager.openDB();
                cursor = database.query()


            }
        }


        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();



    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {




    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}
