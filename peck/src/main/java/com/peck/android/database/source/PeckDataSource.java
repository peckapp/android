package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.PeckOpenHelper;
import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckDataSource extends DataSource<Peck> {

    public PeckDataSource(Context context) {
        super(new PeckOpenHelper(context));
    }

    @Override
    public Peck generate() {
        return new Peck();
    }
}
