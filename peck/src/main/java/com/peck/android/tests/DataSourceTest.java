package com.peck.android.tests;

import android.content.ContentValues;
import android.content.Context;
import android.test.AndroidTestCase;

import com.peck.android.abstracts.DataSourceHelper;
import com.peck.android.database.DataSource;
import com.peck.android.database.EventOpenHelper;
import com.peck.android.models.Event;

import junit.framework.TestCase;

import org.apache.http.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.Date;

public class DataSourceTest extends AndroidTestCase {
    private final String testDB = "test.db"; //never set to the value in EventOpenHelper.DATABASE_NAME
    private final String testStr = "mytest";
    private final int testcol = 42;
    private final int testsv = 1;
    private final Date testcr = new Date(10000);
    private final Date testup = new Date(20000);
    private final String key = "key";

    //private DataSourceHelper<Event> dbHelper;
    private DataSource<Event> dSource;
    private Context context;
    private EventOpenHelper dbHelper;

    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        dbHelper = new EventOpenHelper(context, testDB);
        dSource = new DataSource<Event>(context, dbHelper);
    }

    public void tearDown() throws Exception {
        try { getContext().deleteDatabase(testDB); }
        catch (Exception e) { throw new Exception("there wasn't a database to delete.", e); }
        dSource = null;
        dbHelper = null;
        context = null;
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
        Event e = dSource.create(values);
        Event f = new Event();
        f.setServerId(testsv);
        f.setColor(testcol);
        f.setTitle(testStr);
        f.setCreated(testcr);
        f.setUpdated(testup);

        f.setLocalId(e.getLocalId());

        if (e.hashCode() != f.hashCode()) throw new Exception("database did not return an equivalent event");
    }

    public void testUpdate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_SERVER_ID, testsv);
        values.put(dbHelper.COLUMN_COLOR, testcol);
        values.put(dbHelper.COLUMN_TITLE, testStr);
        values.put(dbHelper.COLUMN_CREATED, testcr.getTime());
        values.put(dbHelper.COLUMN_UPDATED, testup.getTime());
        values.put(dbHelper.COLUMN_HIDDEN, 0);
        Event e = dSource.create(values);
        int i = e.getColor();
        values.remove(dbHelper.COLUMN_COLOR);
        values.put(dbHelper.COLUMN_COLOR, testcol+1);
        dSource.update(values, e.getLocalId());
        e = dbHelper.createFromCursor(dbHelper.getWritableDatabase().query(dbHelper.TABLE_NAME, dbHelper.getColumns(),
                dbHelper.COLUMN_LOC_ID + " = " + e.getLocalId(), null, null, null, null));
        if (e.getColor() != i) throw new Exception("event update failed");
    }

    public void testDelete() throws Exception {

    }

    public void testGetAll() throws Exception {
        ArrayList<Event> events = new ArrayList<Event>();
        Event e = new Event();
        e.setTitle(testStr);
        events.add(e);



    }
}