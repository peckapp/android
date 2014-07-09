package com.peck.android.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mammothbane on 7/9/2014.
 */
public abstract class FiltrationPolicy<T> implements Comparator<T> {

    public Collection<T> filter(Collection<T> tCollection) {
        ArrayList<T> ret = new ArrayList<T>();
        for (T i : tCollection) {
            if (test(i)) ret.add(i);
        }
        Collections.sort(ret, this);
        return ret;
    }

    /**
     * tests an item to see if it should be included in the returned list
     *
     * @param t the item to test
     * @return true if the item should be included, false if not
     */
    public abstract boolean test(T t);

}
