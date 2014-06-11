package com.peck.android.database.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.interfaces.WithLocal;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<model extends WithLocal, helper extends DataSourceHelper<model>> {
    private SQLiteDatabase database;
    private helper dbHelper;
    private static final String TAG = "datasource";

    public DataSource(helper dbHelper) {
        this.dbHelper = dbHelper;
        dbHelper.setDatasource(this);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public model create(ContentValues contentValues) {
        long insertId = database.insert(dbHelper.getTableName(), null, contentValues);
        Cursor cursor = database.query(dbHelper.getTableName(), dbHelper.getColumns(),
                dbHelper.getColLocId() + " = " + insertId, null, null, null, null);
        model newModel = dbHelper.createFromCursor(cursor);
        cursor.close();
        return newModel;
    }
    
    public void update(ContentValues values, int id){

        database.update(dbHelper.getTableName(),
                values,
                dbHelper.getColLocId() + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void delete(model model) {
        long id = model.getLocalId();
        Log.d(TAG, model.getClass() + " deleted with id: " + id);
        database.delete(dbHelper.getTableName(), dbHelper.getColLocId()
                + " = " + id, null);
    }

    public ArrayList<model> getAll() {
        ArrayList<model> ret = new ArrayList<model>();
        Cursor cursor = database.query(dbHelper.getTableName(),
                dbHelper.getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            model obj = dbHelper.createFromCursor(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return ret;
    }

    public void getAll(ArrayList<model> ret) {
        Cursor cursor = database.query(dbHelper.getTableName(),
                dbHelper.getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            model obj = dbHelper.createFromCursor(cursor);
            ret.add(obj);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();

    }


}
