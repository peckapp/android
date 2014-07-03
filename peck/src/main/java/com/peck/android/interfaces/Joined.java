package com.peck.android.interfaces;

import com.peck.android.models.DBOperable;
import com.peck.android.models.JoinGroup;

import java.util.ArrayList;

/**
 * Created by mammothbane on 7/3/2014.
 */
public interface Joined {

    public <T extends DBOperable & Joined> ArrayList<JoinGroup<? extends DBOperable, T>> getJoinGroups();

}
