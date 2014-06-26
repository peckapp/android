package com.peck.android.network.NetworkSpec;

import java.lang.reflect.Type;

/**
 * Created by mammothbane on 6/26/2014.
 */
public interface NetworkSpec<T> {

    public String getApiExtension();

    public Type getType();


}
