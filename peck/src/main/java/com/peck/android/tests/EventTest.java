package com.peck.android.tests;

import android.content.Context;
import android.test.AndroidTestCase;

import com.peck.android.models.Event;

import java.util.Date;

public class EventTest extends AndroidTestCase {

    private final static String testStr = "mytest";
    private final static int testid = 42;
    private final static Date testdt = new Date(10000);
    private final static String TAG = "eventmodeltest";

    private Event e;
    private Context context;

    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        e = new Event();
        assertPre();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        context = null;
        e = null;
        assertPost();
    }

    private void assertPre() {
        assertNotNull(context);
        assertNotNull(e);
    }

    private void assertPost() {
        assertNull(context);
        assertNull(e);
    }

    public void testLocalId() throws Exception {
        e.setLocalId(testid);
        if (e.getLocalId() != testid) throw new Exception("get/set localId not working");
    }


    public void testServerId() throws Exception {
        e.setServerId(testid);
        if (e.getServerId() != testid) throw new Exception("get/set serverId not working");
    }

    public void testColor() throws Exception {
        e.setColor(testid);
        if (e.getColor() != testid) throw new Exception("get/set color not working");
    }

    public void testCreated() throws Exception {
        e.setCreated(testdt);
        if (e.getCreated() != testdt) throw new Exception("get/set created date not working");
    }

    public void testUpdated() throws Exception {
        e.setLocalId(testid);
        if (e.getLocalId() != testid) throw new Exception("get/set localId not working");
    }

    public void testTitle() throws Exception {
        e.setTitle(testStr);
        if (!e.getTitle().equals(testStr)) throw new Exception("get/set localId not working");
    }

}