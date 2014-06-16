package com.peck.android.interfaces;

/**
 * Created by mammothbane on 6/9/2014.
 */
public interface Factory<T> {

    public abstract T generate();

    //abstract protected GenericFactory<T> getFactory();

}
