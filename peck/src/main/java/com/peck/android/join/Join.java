package com.peck.android.join;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.managers.Manager;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/27/2014.
 */
public abstract class Join<T extends DBOperable, R extends DBOperable> extends DBOperable {
    private ArrayList<Integer> ts;
    private ArrayList<Integer> rs;


    public R getR(int localId) {
        return getRManager().getByLocalId(localId);
    }

    public T getT(int localId) {
        return getTManager().getByLocalId(localId);
    }

    public abstract Manager<T> getTManager();
    public abstract Manager<R> getRManager();

}
