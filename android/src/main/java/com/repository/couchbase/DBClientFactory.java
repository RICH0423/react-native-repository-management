package com.repository.couchbase;

import android.content.Context;
import android.util.Log;

import com.repository.Utils.PropertyValue;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by rich on 2016/4/15.
 */
public class DBClientFactory {

    private Context context;
    private Manager manager = null;
    private Database database = null;

    public DBClientFactory(Context context){
        this.context = context.getApplicationContext();
        initDatabase();
    }

    private void initDatabase(){
        try {
            if (this.manager == null) {
                this.manager = new Manager(new AndroidContext(this.context), Manager.DEFAULT_OPTIONS);
            }

            if ((this.database == null) & (this.manager != null)) {
                this.database = manager.getDatabase(PropertyValue.DB_NAME);
            }
        } catch(Exception e){
            Log.e(PropertyValue.LOG_DB_TAG, "Init database instance error!", e);
        }
    }

    /**
     * Get Database instance.
     *
     * @return Database
     * @throws CouchbaseLiteException
     */
    public Database getDatabaseInstance() throws CouchbaseLiteException, IOException {
        if ((this.database == null) & (this.manager != null)) {
            this.database = manager.getDatabase(PropertyValue.DB_NAME);
        }
        return database;
    }

    /**
     * Reset database.
     *
     * @return Database
     * @throws CouchbaseLiteException
     * @throws IOException
     */
    public Database resetDatabase() throws CouchbaseLiteException, IOException{
        database.delete();
        database = manager.getDatabase(PropertyValue.DB_NAME);
        return database;
    }

    /**
     * Get Couchbase manager instance.
     *
     * @return Manager
     * @throws IOException
     */
    public Manager getManagerInstance() throws IOException {
        if (manager == null) {
            manager = new Manager(new AndroidContext(this.context), Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }

}
