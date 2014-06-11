package com.peck.android.factories;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class GenericFactory<T> {

    public abstract T generate();

    //abstract protected GenericFactory<T> getFactory();

}
