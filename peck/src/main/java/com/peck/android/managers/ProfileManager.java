package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Profile;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class ProfileManager extends Manager<Profile> implements Singleton {
    private static ProfileManager profileManager = new ProfileManager();

    private ProfileManager() { }

    public ProfileManager getManager() {
        return profileManager;
    }

}
