/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.Provider;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by mammothbane on 7/31/2014.
 *
 * date provider for joda, which is apparently slow
 * otherwise because it loads from a jar and references the manifest.
 *
 */
public class FastDateTimeZoneProvider implements Provider {
    public static final Set<String> AVAILABLE_IDS = new HashSet<String>();

    static {
        AVAILABLE_IDS.addAll(Arrays.asList(TimeZone.getAvailableIDs()));
    }

    public DateTimeZone getZone(String id) {
        if (id == null) {
            return DateTimeZone.UTC;
        }

        TimeZone tz = TimeZone.getTimeZone(id);
        if (tz == null) {
            return DateTimeZone.UTC;
        }

        int rawOffset = tz.getRawOffset();

        //sub-optimal. could be improved to only create a new Date every few minutes
        if (tz.inDaylightTime(new Date())) {
            rawOffset += tz.getDSTSavings();
        }

        return DateTimeZone.forOffsetMillis(rawOffset);
    }

    public Set<String> getAvailableIDs() {
        return AVAILABLE_IDS;
    }


}
