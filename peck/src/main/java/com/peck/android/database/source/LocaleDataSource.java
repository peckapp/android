package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.database.helper.LocaleOpenHelper;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleDataSource extends DataSource<Locale> {


    private Context context;
    private EventOpenHelper dbHelper;

    public LocaleDataSource(Context context) {
        super(new LocaleOpenHelper(context));
    }

    @Override
    public Locale generate() {
        return new Locale();
    }
}
