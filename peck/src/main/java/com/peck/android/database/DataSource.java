package com.peck.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.peck.android.interfaces.WithLocal;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends WithLocal, S extends DataSourceHelper<T>> {
    private SQLiteDatabase database;
    private S dbHelper;
    private static final String TAG = "datasource";

    public DataSource(S dbHelper) {
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
        long insertId = database.insert(dbHelper.getTableName(), null, contentValues);
        Cursor cursor = database.query(dbHelper.getTableName(), dbHelper.getColumns(),
                dbHelper.getColLocId() + " = " + insertId, null, null, null, null);
        T newT = dbHelper.createFromCursor(cursor);
        cursor.close();
        return newT;
    }
    
    public void update(ContentValues values, int id){

        database.update(dbHelper.getTableName(),
                values,
                dbHelper.getColLocId() + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void delete(T t) {
        long id = t.getLocalId();
        System.out.println(t.getClass() + " deleted with id: " + id);
        database.delete(dbHelper.getTableName(), dbHelper.getColLocId()
                + " = " + id, null);
    }

    public ArrayList<T> getAll() {
        ArrayList<T> ret = new ArrayList<T>();
        Cursor cursor = database.query(dbHelper.getTableName(),
                dbHelper.getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T obj = dbHelper.createFromCursor(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return ret;
    }

}
