package com.peck.android.interfaces;

import com.peck.android.database.dataspec.joinspec.JoinDataSpec;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/26/2014.
 */
public interface Join<T> {

    public ArrayList<T> getJoins();

    public JoinDataSpec getDataSpec();

}
