package com.peck.android.database.dataspec.joinspec;

import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Join;

/**
 * Created by mammothbane on 6/26/2014.
 */
public abstract class JoinDataSpec<S, T extends DBOperable & Join<S>> extends DataSpec<T> {

    public static final String JOINER_ID = "joiner";
    public static final String JOINING_ID = "joining";

    {
        COLUMNS.put(JOINER_ID, "integer not null");
        COLUMNS.put(JOINING_ID, "integer not null");
    }


    @Override
    public abstract String getTableName();

    @Override
    public T generate() {
        return null;
    }



}
