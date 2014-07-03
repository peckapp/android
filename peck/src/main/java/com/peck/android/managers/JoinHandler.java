package com.peck.android.managers;

import com.peck.android.interfaces.Joined;
import com.peck.android.models.DBOperable;
import com.peck.android.models.JoinGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 7/3/2014.
 */
public class JoinHandler {
    private static HashMap<Class<? extends DBOperable>, ArrayList<JoinGroup<? extends DBOperable, ? extends DBOperable>>> joins =
            new HashMap<Class<? extends DBOperable>, ArrayList<JoinGroup<? extends DBOperable, ? extends DBOperable>>>();

    public static <T extends DBOperable & Joined> void put(ArrayList<JoinGroup<? extends DBOperable, ? extends DBOperable>> join, Class<T> hostClass) { joins.put(hostClass, join); }

    @SuppressWarnings("unchecked")
    public static <T extends DBOperable & Joined> ArrayList<JoinGroup<? extends DBOperable, T>> get(Class<T> hostClass) {
        return ((ArrayList<JoinGroup<? extends DBOperable, T>>) joins.get(hostClass));
    }

}
