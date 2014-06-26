package com.peck.android.managers;


import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Relationship;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class RelationshipManager extends Manager<Relationship> implements Singleton {
    private static RelationshipManager manager = new RelationshipManager();

    private static RelationshipManager getManager() {
        return manager;
    }

    private RelationshipManager() {

    }

    @Override
    public Manager initialize(DataSource dSource, Callback callback) {
        return super.initialize(dSource, callback);
    }

    public static


    @Override
    public ArrayList downloadFromServer() {
        return super.downloadFromServer();
    }

    @Override
    public ArrayList loadFromDatabase(DataSource dataSource, Callback callback) {
        return super.loadFromDatabase(dataSource, callback);
    }

    @Override
    public ArrayList getData() {
        return super.getData();
    }

    @Override
    public DBOperable getByLocalId(int id) {
        return super.getByLocalId(id);
    }

    @Override
    public DBOperable getByServerId(int id) {
        return super.getByServerId(id);
    }

    @Override
    public void add(DBOperable item, Callback callback) {
        super.add(item, callback);
    }

    @Override
    public void update(DBOperable item) {
        super.update(item);
    }
}
