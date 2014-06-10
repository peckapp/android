package com.peck.android.tests;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.peck.android.database.source.DataSource;
import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.models.Event;

import java.util.Date;

public class DataSourceTest extends AndroidTestCase {
    private final static String testDB = "test.db"; //never set to the value in EventOpenHelper.DATABASE_NAME
    private final static String testStr = "mytest";
    private final static int testcol = 42;
    private final static int testsv = 1;
    private final static Date testcr = new Date(10000);
    private final static Date testup = new Date(20000);
    private final static String key = "key";
    private final static String TAG = "DataSourceTest";

    private DataSource<Event, EventOpenHelper> dSource;
    private EventOpenHelper dbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbHelper = new EventOpenHelper(getContext(), testDB);
        dSource = new DataSource<Event, EventOpenHelper>(dbHelper);
        assertPre();
    }

    @Override
    protected void tearDown() throws Exception {
        try { getContext().deleteDatabase(testDB); }
        catch (Exception e) { throw new Exception("there wasn't a database to delete.", e); }
        dSource = null;
        dbHelper = null;
    }

    private void assertPre() throws AssertionError {
        assertNotNull(dSource);
        assertNotNull(dbHelper);
    }

    public void testOpenClose() throws Exception {
        boolean bool = false;
        try {
        dSource.open();
        dbHelper.create(testStr, testcol, testsv, testcr, testup);
        dSource.close();
        } catch (Exception e) { throw new Exception("event creation threw an exception", e);}
        try { dbHelper.create(testStr, testcol*2, testsv*2, testcr, testup); }
        catch (Exception e) { bool = true; }

        if (!bool) throw new Exception("event creation succeeded.");
    }

    public void testCreate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
        values.put(dbHelper.COLUMN_COLOR, testcol);
        values.put(dbHelper.COLUMN_TITLE, testStr);
        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
        values.put(dbHelper.COLUMN_HIDDEN, 0);
        dSource.open();
        Event e = dSource.create(values);
        Event f = new Event();
        dSource.close();
        f.setServerId(testsv);
        f.setColor(testcol);
        f.setTitle(testStr);
        f.setCreated(testcr);
        f.setUpdated(testup);

        f.setLocalId(e.getLocalId());

        if (e.equals(f)) throw new Exception("database did not return an equivalent event." +
                "\ndatabase event hash: " + e.hashCode() + "\nconstructed event hash: " + f.hashCode());
    }

    public void testUpdate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
        values.put(dbHelper.COLUMN_COLOR, testcol);
        values.put(dbHelper.COLUMN_TITLE, testStr);
        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
        values.put(dbHelper.COLUMN_HIDDEN, 0);
        dSource.open();
        Event e = dSource.create(values);
        dSource.close();
        final int i = e.getColor();
        values.remove(dbHelper.COLUMN_COLOR);
        values.put(dbHelper.COLUMN_COLOR, testcol + 1);
        dSource.open();
        dSource.update(values, e.getLocalId());
        e = dbHelper.createFromCursor(dbHelper.getWritableDatabase().query(dbHelper.TABLE_NAME, dbHelper.getColumns(),
                dbHelper.COLUMN_LOC_ID + " = " + e.getLocalId(), null, null, null, null));
        dSource.close();
        if (e.getColor() != i + 1) throw new Exception("event load failed; color is " + e.getColor() +
        ", should be " + i + 1);
    }

    public void testDelete() throws Exception {
        dSource.open();
        final int sz = dSource.getAll().size();
        dSource.close();
        assertEquals(sz, 0);
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
        values.put(dbHelper.COLUMN_COLOR, testcol);
        values.put(dbHelper.COLUMN_TITLE, testStr);
        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
        values.put(dbHelper.COLUMN_HIDDEN, 0);
        dSource.open();
        Event e = dSource.create(values);
        dSource.delete(e);
        int x = dSource.getAll().size();
        dSource.close();
        if (x != 0) throw new Exception("delete failed");
    }

    public void testGetAll() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COLUMN_TITLE, testStr);
        dSource.open();
        Event e = dSource.create(cv);
        Event f = dSource.getAll().get(0);
        dSource.close();
        if (e.hashCode() != f.hashCode()) throw new Exception("object hashcodes differ." +
                "\nconstructed event hash: " + e.hashCode() +
                "\ndb event hash: " + f.hashCode());

    }
}