package com.peck.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;

import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends DBOperable> implements Factory<T> {
    private SQLiteDatabase database;
    private DataSpec<T> dbSpec;
    private static final String TAG = "datasource";

    public DataSource(DataSpec<T> dbSpec) {
        this.dbSpec = dbSpec;
    }

    public void open() {
        database = DatabaseManager.openDB();
    }

    public void close() {
        DatabaseManager.closeDB();
    }

    public T generate() { return dbSpec.generate(); }

    public T create(T t) {
        ContentValues contentValues = t.toContentValues();

        Log.d(TAG, "cv: " + ((contentValues == null) ? "null" : "not null"));
        Log.d(TAG, "database: " + ((database == null) ? "null" : "not null"));

        long insertId;
        Cursor cursor;

        insertId = database.insert(dbSpec.getTableName(), null, contentValues);
        cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(),
                dbSpec.getColLocId() + " = " + insertId, null, null, null, null);

        T newT = (T) generate().fromCursor(cursor);
        cursor.close();
        return newT;
    }

    public void update(T t) {
        database.update(dbSpec.getTableName(),
                t.toContentValues(),
                dbSpec.getColLocId() + " = ?",
                new String[]{String.valueOf(t.getLocalId())});
    }

    public void delete(T T) {
        long id = T.getLocalId();
        Log.d(TAG, T.getClass() + " deleted with id: " + id);
        database.delete(dbSpec.getTableName(), dbSpec.getColLocId()
                + " = " + id, null);
    }

    public ArrayList<T> getAll() {
        ArrayList<T> ret = new ArrayList<T>();
        Cursor cursor = database.query(dbSpec.getTableName(),
                dbSpec.getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T obj = (T) generate().fromCursor(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return ret;
    }

    public void getAll(ArrayList<T> ret) {
        Cursor cursor = database.query(dbSpec.getTableName(),
                dbSpec.getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T obj = (T) generate().fromCursor(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();

    }

    public T get(int id) {
        Cursor cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(), dbSpec.getColLocId() + " = " + id, null, null, null, null);
        return (T)generate().fromCursor(cursor);
    }


}
