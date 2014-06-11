package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.database.helper.LocaleOpenHelper;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleDataSource extends DataSource {


    private Context context;
    private EventOpenHelper dbHelper;

    public LocaleDataSource(Context context) {
        super(new LocaleOpenHelper(context));
    }


}
