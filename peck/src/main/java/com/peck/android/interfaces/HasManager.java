package com.peck.android.interfaces;

import com.peck.android.managers.Manager;

/**
 * Created by mammothbane on 6/13/2014.
 */
public interface HasManager {

    public abstract <S extends Manager & Singleton> Class<S> getManagerClass();

}
