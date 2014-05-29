package com.peck.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.peck.android.abstracts.DataSourceHelper;
import com.peck.android.interfaces.withLocal;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends withLocal> {
    private SQLiteDatabase database;
    private com.peck.android.abstracts.DataSourceHelper dbHelper;

    public DataSource(Context context, DataSourceHelper<T> dbHelper) {
        this.dbHelper = dbHelper;
        dbHelper.setDatabase(this);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public T create(ContentValues contentValues) {
        long insertId = database.insert(dbHelper.TABLE_NAME, null, contentValues);

        Cursor cursor = database.query(dbHelper.TABLE_NAME, dbHelper.getColumns(),
                dbHelper.COLUMN_LOC_ID + " = " + insertId, null, null, null, null);

        T newT = cursorTo(cursor);
        cursor.close();
        return newT;
    }
    
    public void update(ContentValues values, int id){

        database.update(dbHelper.TABLE_NAME,
                values,
                dbHelper.COLUMN_LOC_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void delete(T t) {
        long id = t.getLocalId();
        System.out.println(t.getClass() + " deleted with id: " + id);
        database.delete(dbHelper.TABLE_NAME, dbHelper.COLUMN_LOC_ID
                + " = " + id, null);
    }

    public ArrayList<T> getAll() {
        ArrayList<T> ret = new ArrayList<T>();

        Cursor cursor = database.query(dbHelper.TABLE_NAME,
                dbHelper.getColumns(), null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T obj = cursorTo(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return ret;
    }

    private T cursorTo(Cursor cursor) {
        return (T)dbHelper.createFromCursor(cursor);
    }
}
