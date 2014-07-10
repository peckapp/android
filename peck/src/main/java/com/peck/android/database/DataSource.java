package com.peck.android.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.peck.android.BuildConfig;
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

    @NonNull
    private Class<T> tClass;
    private static final String TAG = "DataSource";

    @Nullable
    private String[] columns;

    @Nullable
    private String tableName;

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
            return tClass.newInstance();
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

    public void save(T t, Callback<Integer> callback) {
        queue.add(new save(t, callback));
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

    /**
     * call to execute after a database operation. not guaranteed to execute before other ops.
     * @param callback gets called once the queue reaches this item
     */

    public void post(Callback callback) {
        queue.add(new post(callback));
    }


    private String[] getColumns() {
        if (columns == null) columns = generate().getColumns();
        return columns;
    }

    private String getTableName() {
        if (tableName == null) tableName = generate().getTableName();
        return tableName;
    }

    private class opThread extends Thread {
        @Override
        public void run() {
            open();
            Log.v(TAG + ": "  + tClass.getSimpleName(), "running");
            while (!queue.isEmpty()) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    Log.e(TAG + ": "  + tClass.getSimpleName(), e.toString());
                }
            }
            close();
            Log.v(TAG + ": " + tClass.getSimpleName(), "dying");
        }
    }

    private abstract class dbOp {
        abstract void run();
    }

    private class post extends dbOp {
        private Callback callback;

        private post(Callback callback) {
            this.callback = callback;
        }

        //todo: set priority higher here
        @SuppressWarnings("unchecked")
        @Override
        void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    callback.callBack(null);
                }
            }).start();
        }
    }


    private class get extends dbOp {
        private int id;
        private Callback<T> callback;

        private get(int id, Callback<T> callback) {
            this.id = id;
            this.callback = callback;
        }

        public void run() {
            Cursor cursor = database.query(getTableName(), getColumns(), PeckApp.Constants.Database.LOCAL_ID
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
            Cursor cursor = database.query(getTableName(),
                    getColumns(), null, null, null, null, null);
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

    private class save extends dbOp {
        final T t;
        Callback<Integer> callback;

        private save(T t, Callback<Integer> callback) {
            this.t = t;
            this.callback = callback;
        }

        private void runCreate() {

            synchronized (t) {
                int insertId;
                try {
                    insertId = (int) database.insert(getTableName(), null, JsonConverter.toContentValues(t));

                    if (insertId == -1)
                        throw new SQLiteException("Row could not be inserted into the database");

                    callback.callBack(insertId);

                } catch (SQLiteConstraintException e) {
                    Log.e(TAG, "item broke a constraint: " + e.toString());
                    callback.callBack(null);
                }
            }
        }

        private void update(int localId) {
            if (BuildConfig.DEBUG && (t.getLocalId() == null && t.getServerId() == null)) throw new IllegalArgumentException("serverId and localId can't both be null on update");

            synchronized (t) {
                database.update(getTableName(), JsonConverter.toContentValues(t),
                        PeckApp.Constants.Database.LOCAL_ID + " = ?", new String[]{Integer.toString(localId)});
                callback.callBack(localId);
            }
        }

        @SuppressWarnings("unchecked")
        public void run() {
            if (t.getServerId() != null) {
                Cursor cursor = database.query(getTableName(), getColumns(), PeckApp.Constants.Network.SV_ID_NAME + " = " + t.getServerId(), null, null, null, null);
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    update(cursor.getInt(cursor.getColumnIndex(PeckApp.Constants.Database.LOCAL_ID)));
                }
                else if (cursor.getCount() < 1) runCreate();
                else throw new SQLiteConstraintException("More than one " + t.getClass().getSimpleName() + " with the same serverId already exists in the database.");

            } else runCreate();
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
            database.delete(getTableName(), PeckApp.Constants.Database.LOCAL_ID
                    + " = " + id, null);
        }
    }


}
