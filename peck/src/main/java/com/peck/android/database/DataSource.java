package com.peck.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.CursorCreatable;
import com.peck.android.models.Event;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends CursorCreatable> {
    private SQLiteDatabase database;
    private com.peck.android.abstracts.DataSourceHelper dbHelper;
    private String[] allColumns;
    private GenericFactory<T> factory;

    public DataSource(Context context, String[] allColumns, com.peck.android.abstracts.DataSourceHelper<T> dbHelper) {
        this.dbHelper = dbHelper;
        this.allColumns = allColumns;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public T create(ContentValues contentValues) {
        long insertId = database.insert(EventOpenHelper.TABLE_EVENTS, null, contentValues);

        Cursor cursor = database.query(EventOpenHelper.TABLE_EVENTS, allColumns,
                EventOpenHelper.COLUMN_LOC_ID + " = " + insertId, null, null, null, null);

        T newT = cursorTo(cursor);
        cursor.close();
        return newT;
    }
    
    public void update(T obj){
        D
        ContentValues values = new ContentValues();
        values.put(EventOpenHelper.COLUMN_SERVER_ID, obj.getLocalId());
        values.put(EventOpenHelper.COLUMN_COLOR, obj.getColor());
        values.put(EventOpenHelper.COLUMN_TITLE, obj.getTitle());

        database.update(EventOpenHelper.TABLE_EVENTS,
                values,
                EventOpenHelper.COLUMN_LOC_ID + " = ?",
                new String[]{String.valueOf(obj.getLocalId())});
    }

    public void delete(T t) {
        long id = t.getLocalId();
        System.out.println("Event deleted with id: " + id);
        database.delete(EventOpenHelper.TABLE_EVENTS, EventOpenHelper.COLUMN_LOC_ID
                + " = " + id, null);
    }

    public ArrayList<T> getAll() {
        ArrayList<T> ret = new ArrayList<T>();

        Cursor cursor = database.query(EventOpenHelper.TABLE_EVENTS,
                allColumns, null, null, null, null, null);

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
        T ret = (T)factory.getNew();
        ret.createFromCursor(cursor);
        return ret;
    }
}
