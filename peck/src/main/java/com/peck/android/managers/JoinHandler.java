package com.peck.android.managers;

import com.peck.android.models.DBOperable;
import com.peck.android.models.JoinGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 7/3/2014.
 */
public class JoinHandler {
    private static HashMap<Class<? extends DBOperable>, ArrayList<JoinGroup<? extends DBOperable>>> joins =
            new HashMap<Class<? extends DBOperable>, ArrayList<JoinGroup<? extends DBOperable>>>();

    public static <T> void put(JoinGroup join, Class<T> tClass) {}

    public static <T> void get(Class<T> tClass)



}
