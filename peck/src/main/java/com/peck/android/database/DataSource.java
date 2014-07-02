package com.peck.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Factory;
import com.peck.android.json.JsonConverter;
import com.peck.android.models.DBOperable;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends DBOperable> implements Factory<T> {
    private SQLiteDatabase database;
    private Class<T> tClass;
    private static final String TAG = "DataSource";
    private String[] columns;

    private opThread workingThread;

    final LinkedBlockingQueue<dbOp> queue = new LinkedBlockingQueue<dbOp>() {

        @Override
        public boolean add(dbOp dbOp) {
            boolean ret = super.add(dbOp);

            if (workingThread == null || workingThread.getState() == Thread.State.TERMINATED) workingThread = new opThread();

            synchronized (workingThread) {
                if (workingThread.getState() == Thread.State.NEW) workingThread.start();
            }

            return ret;
        }

    };

    public T generate() {
        try {
        return (T) tClass.newInstance();
        } catch (Exception e) {
            Log.e(tClass.getSimpleName(),
                    "all dboperables must have public, nullary constructors\n" + e.toString());
            return null;
        }
    }

    public DataSource(Class<T> tClass) {
        this.tClass = tClass;
    }

    public void open() {
        database = DatabaseManager.openDB();
    }

    public void close() {
        DatabaseManager.closeDB();
    }

    public void create(T t, Callback<T> callback) {
        queue.add(new create(t, callback));
    }

    public void update(T t) {
        queue.add(new update(t));
    }

    public void delete(T T) {
        queue.add(new delete(T));

    }

    public void getAll(Callback<ArrayList<T>> callback) {
        queue.add(new getAll(callback));
    }

    public void get(int id, Callback<T> callback) {
        queue.add(new get(id, callback));
    }

    private String[] getColumns() {
        if (columns == null) columns = generate().getColumns();
        return columns;
    }

    private class opThread extends Thread {
        @Override
        public void run() {
            open();
            Log.i(TAG + ": "  + tClass.getSimpleName(), "running");
            while (!queue.isEmpty()) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    Log.e(TAG + ": "  + tClass.getSimpleName(), e.toString());
                }
            }
            close();
            Log.i(TAG + ": " + tClass.getSimpleName(), "dying");
        }
    }

    private abstract class dbOp {
        abstract void run();
    }

    private class get extends dbOp {
        private int id;
        private Callback<T> callback;

        private get(int id, Callback<T> callback) {
            this.id = id;
            this.callback = callback;
        }

        public void run() {
            Cursor cursor = database.query(generate().getTableName(), getColumns(), PeckApp.Constants.Database.LOCAL_ID
                    + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            callback.callBack(JsonConverter.fromCursor(cursor, tClass));
        }
    }

    private class getAll extends dbOp {
        private Callback<ArrayList<T>> callback;

        private getAll(Callback<ArrayList<T>> callback) {
            this.callback = callback;
        }

        public void run() {
            ArrayList<T> ret = new ArrayList<T>();
            Cursor cursor = database.query(generate().getTableName(),
                    generate().getColumns(), null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T obj = JsonConverter.fromCursor(cursor, tClass);
                ret.add(obj);
                cursor.moveToNext();
            }
            // Make sure to close the cursor
            cursor.close();

            callback.callBack(ret);
        }
    }

    private class create extends dbOp {
        T t;
        Callback<T> callback;

        private create(T t, Callback<T> callback) {
            this.t = t;
            this.callback = callback;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            ContentValues contentValues = JsonConverter.toContentValues(t);

            long insertId;
            Cursor cursor;

            try {

                insertId = database.insert(generate().getTableName(), null, contentValues);

                if (insertId == -1)
                    throw new SQLiteException("Row could not be inserted into the database");

                cursor = database.query(generate().getTableName(), generate().getColumns(),
                        PeckApp.Constants.Database.LOCAL_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();

                T newT = JsonConverter.fromCursor(cursor, tClass);
                cursor.close();
                callback.callBack(newT);

            } catch (SQLiteConstraintException e) {
                Log.e(TAG, "item broke a constraint: " + e.toString());
                callback.callBack(null);
            }

        }
    }

    private class delete extends dbOp {

        private T t;

        private delete(T t) {
            this.t = t;
        }

        public void run() {
            long id = t.getLocalId();
            Log.d(TAG, t.getClass() + " deleted with id: " + id);
            database.delete(generate().getTableName(), PeckApp.Constants.Database.LOCAL_ID
                    + " = " + id, null);
        }
    }

    private class update extends dbOp {
        T t;

        private update(T t) {
            this.t = t;
        }

        public void run() {
            database.update(generate().getTableName(),
                    JsonConverter.toContentValues(t),
                    PeckApp.Constants.Database.LOCAL_ID + " = ?",
                    new String[]{String.valueOf(t.getLocalId())});
        }
    }


}
