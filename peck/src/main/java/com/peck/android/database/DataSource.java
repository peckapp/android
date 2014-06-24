package com.peck.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DataSource<T extends DBOperable> implements Factory<T> {
    private SQLiteDatabase database;
    private DataSpec<T> dbSpec;
    private static final String TAG = "datasource";

    private Boolean threadWorking = false;

    final LinkedBlockingQueue<dbOp> queue = new LinkedBlockingQueue<dbOp>() {

        @Override
        public boolean add(dbOp dbOp) {
            boolean ret = super.add(dbOp);

            synchronized (threadWorking) {
                if (!threadWorking) {
                    try {
                        threadWorking = true;
                        take();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "loop was interrupted");
                        threadWorking = false;
                    }
                }
            }
            return ret;
        }

        @Override
        public dbOp take() throws InterruptedException {
            dbOp op = super.take();
            op.start();

            return op;
        }};

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

    public void getAll(Callback<HashMap<Integer, T>> callback) {
        queue.add(new getAll(callback));
    }

    public void get(int id, Callback<T> callback) {
        queue.add(new get(id, callback));
    }

    private abstract class dbOp extends Thread {

        public void run() {
            try {
                join(PeckApp.Constants.Database.QUEUE_TIMEOUT);
            } catch (InterruptedException e) {
                Log.e(TAG, "couldn't execute operation " + getClass() + "\n" + e.toString());
            } finally {
                synchronized (DataSource.this.queue) {
                    if (queue.isEmpty()) {
                        threadWorking = false;
                    } else {
                        try {
                            queue.take().start();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "couldn't execute " + this.getClass());
                        }
                    }

                }
            }
        }; //run the operation
    }

    private class get extends dbOp {
        private int id;
        private Callback<T> callback;

        private get(int id, Callback<T> callback) {
            this.id = id;
            this.callback = callback;
        }

        @Override
        public void run() {
            DataSource.this.open();
            Cursor cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(), dbSpec.getColLocId() + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            callback.callBack((T) generate().fromCursor(cursor));
            DataSource.this.close();
            super.run();
        }
    }

    private class getAll extends dbOp {
        private Callback<HashMap<Integer, T>> callback;

        private getAll(Callback<HashMap<Integer, T>> callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            HashMap<Integer, T> ret = new HashMap<Integer, T>();
            DataSource.this.open();
            Cursor cursor = database.query(dbSpec.getTableName(),
                    dbSpec.getColumns(), null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T obj = (T) generate().fromCursor(cursor);
                ret.put(obj.getLocalId(), obj);
                cursor.moveToNext();
            }
            // Make sure to close the cursor
            cursor.close();
            DataSource.this.close();

            callback.callBack(ret);
            super.run();
        }
    }

    private class create extends dbOp {
        T t;
        Callback<T> callback;

        private create(T t, Callback<T> callback) {
            this.t = t;
            this.callback = callback;
        }

        @Override
        public void run() {
            DataSource.this.open();
            ContentValues contentValues = t.toContentValues();

            Log.d(TAG, "cv: " + ((contentValues == null) ? "null" : "not null"));
            Log.d(TAG, "database: " + ((database == null) ? "null" : "not null"));

            long insertId;
            Cursor cursor;

            insertId = database.insert(dbSpec.getTableName(), null, contentValues);

            if (insertId == -1) throw new SQLiteException("Row could not be inserted into the database");

            cursor = database.query(dbSpec.getTableName(), dbSpec.getColumns(),
                    dbSpec.getColLocId() + " = " + insertId, null, null, null, null);

            cursor.moveToFirst();

            T newT = (T) generate().fromCursor(cursor);
            cursor.close();
            DataSource.this.close();

            callback.callBack(newT);
            super.run();
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
            DataSource.this.open();
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
                        dbSpec.getColLocId() + " = " + insertId, null, null, null, null);

                cursor.moveToFirst();

                ret.add((T)generate().fromCursor(cursor));
                cursor.close();
            }
            DataSource.this.close();

            callback.callBack(ret);
            super.run();
        }


    }


    private class delete extends dbOp {

        private T t;

        private delete(T t) {
            this.t = t;
        }

        @Override
        public void run() {
            long id = t.getLocalId();
            Log.d(TAG, t.getClass() + " deleted with id: " + id);
            DataSource.this.open();
            database.delete(dbSpec.getTableName(), dbSpec.getColLocId()
                    + " = " + id, null);
            DataSource.this.close();
            super.run();
        }
    }

    private class update extends dbOp {
        T t;

        private update(T t) {
            this.t = t;
        }

        @Override
        public void run() {
            DataSource.this.open();
            database.update(dbSpec.getTableName(),
                    t.toContentValues(),
                    dbSpec.getColLocId() + " = ?",
                    new String[]{String.valueOf(t.getLocalId())});
            DataSource.this.close();
            super.run();
        }
    }



}
