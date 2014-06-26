package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserManager extends Manager<User> implements Singleton {

    private static UserManager userManager = new UserManager();

    private UserManager() {

    }

    public static UserManager getManager() {
        return userManager;
    }



}
