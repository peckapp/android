package com.peck.android.database.dataspec.joinspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class CircleUserJoinSpec extends JoinDataSpec<User, Circle> implements Singleton {
    private static CircleUserJoinSpec joinSpec = new CircleUserJoinSpec();

    private CircleUserJoinSpec() {}

    public static CircleUserJoinSpec getJoinSpec() {
        return joinSpec;
    }

    public static final String TABLE_NAME = "circlesUsers";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
