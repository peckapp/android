package com.peck.android.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mammothbane on 7/1/2014.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface DBType {
    String value() default "text";
}
