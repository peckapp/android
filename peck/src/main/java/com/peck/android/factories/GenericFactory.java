package com.peck.android.factories;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class GenericFactory<T> {

    public abstract T generate();

    //all factories must be singletons, can't implement this below because i can't force static methods in subclasses

    //public abstract GenericFactory<T> getFactory(); //all factories are singletons

}
