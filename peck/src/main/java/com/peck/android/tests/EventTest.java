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


}