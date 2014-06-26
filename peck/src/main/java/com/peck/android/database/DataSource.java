package com.peck.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends DBOperable> implements Factory<T> {
    private SQLiteDatabase database;
    private DataSpec<T> dbSpec;
    private static final String TAG = "DataSource";


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

    public void create(T t, Callback<T> callback) {
        queue.add(new create(t, callback));
    }

    public void createMult(Collection<T> ts, Callback<Collection<T>> callback) {
        queue.add(new createMult(ts, callback));
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



    private class opThread extends Thread {
        @Override
        public void run() {
            open();
            Log.i(TAG + ": "  + generate().getClass().getSimpleName(), "running");
            while (!queue.isEmpty()) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    Log.e(TAG + ": "  + generate().getClass().getSimpleName(), e.toString());
                }
            }
            close();
            Log.i(TAG + ": "  + generate().getClass().getSimpleName(), "dying");
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
            Cursor cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(), DataSpec.COLUMN_LOC_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            callback.callBack((T) generate().fromCursor(cursor));
        }
    }

    private class getAll extends dbOp {
        private Callback<ArrayList<T>> callback;

        private getAll(Callback<ArrayList<T>> callback) {
            this.callback = callback;
        }

        public void run() {
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

        public void run() {
            ContentValues contentValues = t.toContentValues();

            long insertId;
            Cursor cursor;

            try {

                insertId = database.insert(dbSpec.getTableName(), null, contentValues);

                if (insertId == -1)
                    throw new SQLiteException("Row could not be inserted into the database");

                cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(),
                        DataSpec.COLUMN_LOC_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();

                T newT = (T) generate().fromCursor(cursor);
                cursor.close();
                callback.callBack(newT);

            } catch (SQLiteConstraintException e) {
                Log.e(TAG, "item broke a constraint: " + e.toString());
                callback.callBack(null);
            }

        }
    }

    private class createMult extends dbOp {
        Collection<T> ts;
        Callback<Collection<T>> callback;

        private createMult(Collection<T> ts, Callback<Collection<T>> callback) {
            this.ts = ts;
            this.callback = callback;
        }

        public void run() {
            long insertId;
            Cursor cursor;

            Collection<T> ret = new ArrayList<T>();

            for (T t : ts) {

                ContentValues contentValues = t.toContentValues();

                Log.d(TAG, "cv: " + ((contentValues == null) ? "null" : "not null"));
                Log.d(TAG, "database: " + ((database == null) ? "null" : "not null"));

                insertId = database.insert(dbSpec.getTableName(), null, contentValues);

                if (insertId == -1)
                    throw new SQLiteException("Row could not be inserted into the database");

                cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(),
                        DataSpec.COLUMN_LOC_ID + " = " + insertId, null, null, null, null);

                cursor.moveToFirst();

                ret.add((T)generate().fromCursor(cursor));
                cursor.close();
            }

            callback.callBack(ret);
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
            database.delete(dbSpec.getTableName(), DataSpec.COLUMN_LOC_ID
                    + " = " + id, null);
        }
    }

    private class update extends dbOp {
        T t;

        private update(T t) {
            this.t = t;
        }

        public void run() {
            database.update(dbSpec.getTableName(),
                    t.toContentValues(),
                    DataSpec.COLUMN_LOC_ID + " = ?",
                    new String[]{String.valueOf(t.getLocalId())});
        }
    }

    private class relationshipQuery extends dbOp {


        @Override
        void run() {

        }
    }



}
