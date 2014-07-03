package com.peck.android.models;

import android.support.annotation.NonNull;

import com.peck.android.interfaces.Joined;

import java.util.ArrayList;

/**
 * Created by mammothbane on 7/3/2014.
 *
 * a class to represent serverside join relationships that we're going to have to update
 * when we sync. since we're only responsible for joins in one direction (e.g. circles -> users, events -> users),
 * the hosting model should hold the joins.
 *
 */
public class JoinGroup<T extends DBOperable, S extends DBOperable & Joined> {

    @NonNull
    private S from;

    public JoinGroup(S s) {
        from = s;
    }

    private ArrayList<Join<T>> joins = new ArrayList<Join<T>>();

    public void addJoin(T t) {
        for (Join<T> join : joins) {
            if (join.getTo().equals(t)) return;
        }
        joins.add(new Join<T>(t));
    }

    public void removeJoin(T t) {
        for (Join<T> join : joins) {
            if (join.getTo() == t) joins.remove(join);
        }
    }


    public static class Join<T> extends DBOperable {
        @NonNull
        private T to;

        public Join(T to) {
            this.to = to;
        }

        @NonNull
        public T getTo() {
            return to;
        }
    }


}
