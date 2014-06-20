package com.peck.android.database.helper;

import android.content.Context;

import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserOpenHelper extends DataSourceHelper<User> {

    private static final String TAG = "PeckOpenHelper";

    public static final String TABLE_NAME = "users";

    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BIO = "bio";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_CREATED = "created_at";
    public static final String COLUMN_FACEBOOK_ID = "fb_id";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID,
            COLUMN_BIO, COLUMN_NAME, COLUMN_UPDATED, COLUMN_CREATED, COLUMN_FACEBOOK_ID };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_FACEBOOK_ID + " integer, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_CREATED + " integer, "
            + COLUMN_BIO + " text, "
            + COLUMN_UPDATED + " integer"
            + ");";


    public UserOpenHelper(Context context) {
        super(context, null);
    }

    public UserOpenHelper() {
        super();
    }

    @Override
    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    @Override
    public String[] getColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }
}
