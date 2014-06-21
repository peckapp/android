package com.peck.android.database.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends DBOperable> implements Factory<T> {
    private SQLiteDatabase database;
    private DataSourceHelper<T> dbHelper;
    private static final String TAG = "datasource";

    public DataSource(DataSourceHelper<T> dbHelper) {
        this.dbHelper = dbHelper;
        dbHelper.setDatasource(this);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public T generate() { return dbHelper.generate(); }

    public T create(T t) {
        ContentValues contentValues = t.toContentValues();

        Log.d(TAG, "cv: " + ((contentValues == null) ? "null" : "not null"));
        Log.d(TAG, "database: " + ((database == null) ? "null" : "not null"));

        long insertId;
        Cursor cursor;

        synchronized (database) {
            insertId = database.insert(dbHelper.getTableName(), null, contentValues);
            cursor = database.query(dbHelper.getTableName(), dbHelper.getColumns(),
                    dbHelper.getColLocId() + " = " + insertId, null, null, null, null);
        }

        T newT = (T) generate().fromCursor(cursor);
        cursor.close();
        return newT;
    }
    
    public void update(T t) {

        synchronized (database) {

            database.update(dbHelper.getTableName(),
                    t.toContentValues(),
                    dbHelper.getColLocId() + " = ?",
                    new String[]{String.valueOf(t.getLocalId())});
        }
    }

    public void delete(T T) {
        long id = T.getLocalId();
        Log.d(TAG, T.getClass() + " deleted with id: " + id);
        database.delete(dbHelper.getTableName(), dbHelper.getColLocId()
                + " = " + id, null);
    }

    public ArrayList<T> getAll() {
        ArrayList<T> ret = new ArrayList<T>();
        Cursor cursor = database.query(dbHelper.getTableName(),
                dbHelper.getColumns(), null, null, null, null, null);
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
        Cursor cursor = database.query(dbHelper.getTableName(),
                dbHelper.getColumns(), null, null, null, null, null);
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
        Cursor cursor = database.query(dbHelper.getTableName(), dbHelper.getColumns(), dbHelper.getColLocId() + " = " + id, null, null, null, null);
        return (T)generate().fromCursor(cursor);
    }


}
