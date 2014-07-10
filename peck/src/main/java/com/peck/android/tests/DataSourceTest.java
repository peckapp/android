package com.peck.android.tests;

import android.test.AndroidTestCase;
import android.util.Log;

import com.peck.android.database.DataSource;
import com.peck.android.database.DatabaseManager;
import com.peck.android.models.Event;

import java.util.Date;

public class DataSourceTest extends AndroidTestCase {
    private final static String testStr = "mytest";
    private final static int testcol = 42;
    private final static int testsv = 1;
    private final static Date testcr = new Date(10000);
    private final static Date testup = new Date(20000);
    private final static String key = "key";
    private final static String TAG = "DataSourceTest";

    private DataSource<Event> dSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try { getContext().deleteDatabase(DatabaseManager.getDbName()); } catch (Exception e) {
            Log.e(TAG, "there wasn't a database to delete");
            e.printStackTrace();
        }
        //dbHelper = new EventOpenHelper(getContext());
        //fixme: dSource = new DataSource<Event>(dbHelper);
        assertPre();
    }

    @Override
    protected void tearDown() throws Exception {
        dSource = null;
    }

    private void assertPre() throws AssertionError {
        assertNotNull(dSource);
    }

    //fixme
//    public void testOpenClose() throws Exception {
//        boolean bool = false;
//        try {
//        dSource.open();
//        dbHelper.save(testStr, testStr, testcol, testsv, testcr, testup);
//        dSource.close();
//        } catch (Exception e) { throw new Exception("event creation threw an exception", e);}
//        try { dbHelper.save(testStr, testStr, testcol*2, testsv*2, testcr, testup); }
//        catch (Exception e) { bool = true; }
//
//        if (!bool) throw new Exception("event creation succeeded.");
//    }


//    fixme
//    public void testCreate() throws Exception {
//        ContentValues values = new ContentValues();
//        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
//        values.put(dbHelper.COLUMN_COLOR, testcol);
//        values.put(dbHelper.COLUMN_TITLE, testStr);
//        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
//        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
//        values.put(dbHelper.COLUMN_HIDDEN, 0);
//        dSource.open();
//        Event e = dSource.save(values);
//        Event f = new Event();
//        dSource.close();
//        f.setServerId(testsv);
//        f.setColor(testcol);
//        f.setTitle(testStr);
//        f.setCreated(testcr);
//        f.setUpdated(testup);
//
//        f.setLocalId(e.getLocalId());
//
//        if (e.equals(f)) throw new Exception("database did not return an equivalent event." +
//                "\ndatabase event hash: " + e.hashCode() + "\nconstructed event hash: " + f.hashCode());
//    }


    //fixme
//    public void testUpdate() throws Exception {
//        ContentValues values = new ContentValues();
//        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
//        values.put(dbHelper.COLUMN_COLOR, testcol);
//        values.put(dbHelper.COLUMN_TITLE, testStr);
//        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
//        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
//        values.put(dbHelper.COLUMN_HIDDEN, 0);
//        dSource.open();
//        Event e = dSource.save(values);
//        dSource.close();
//        final int i = e.getColor();
//        values.remove(dbHelper.COLUMN_COLOR);
//        values.put(dbHelper.COLUMN_COLOR, testcol + 1);
//        dSource.open();
//        dSource.update(values, e.getLocalId());
//        e = dbHelper.createFromCursor(dbHelper.getWritableDatabase().query(dbHelper.TABLE_NAME, dbHelper.getColumns(),
//                dbHelper.COLUMN_LOC_ID + " = " + e.getLocalId(), null, null, null, null));
//        dSource.close();
//        if (e.getColor() != i + 1) throw new Exception("event loadFromDatabase failed; color is " + e.getColor() +
//        ", should be " + i + 1);
//    }



    //fixme
//    public void testDelete() throws Exception {
//        dSource.open();
//        final int sz = dSource.getAll().size();
//        dSource.close();
//        assertEquals(sz, 0);
//        ContentValues values = new ContentValues();
//        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
//        values.put(dbHelper.COLUMN_COLOR, testcol);
//        values.put(dbHelper.COLUMN_TITLE, testStr);
//        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
//        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
//        values.put(dbHelper.COLUMN_HIDDEN, 0);
//        dSource.open();
//        Event e = dSource.save(values);
//        dSource.delete(e);
//        int x = dSource.getAll().size();
//        dSource.close();
//        if (x != 0) throw new Exception("delete failed");
//    }


    //fixme
//    public void testGetAll() throws Exception {
//        ContentValues cv = new ContentValues();
//        cv.put(dbHelper.COLUMN_TITLE, testStr);
//        dSource.open();
//        Event e = dSource.save(cv);
//        Event f = dSource.getAll().get(0);
//        dSource.close();
//        if (e.hashCode() != f.hashCode()) throw new Exception("object hashcodes differ." +
//                "\nconstructed event hash: " + e.hashCode() +
//                "\ndb event hash: " + f.hashCode());
//
//    }
}