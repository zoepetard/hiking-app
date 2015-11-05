/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 05 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;

public class DataManager {

    private static final String SERVER_URL = "http://footpath-1104.appspot.com";//"http://10.0.3.2:8080";

    private static LocalCache sLocalCache;
    private static DatabaseClient sDatabaseClient;

    /**
     * @return the DataManager
     */
    public static DataManager getInstance() {
        return DataManagerHolder.INSTANCE;
    }

    /**
     * Static setter for LocalCache and DatabaseClient: Use only for testing!
     */
    public static void setLocalCache(LocalCache localCache) {
        if(localCache == null) {
            throw new IllegalArgumentException();
        }
        sLocalCache = localCache;
    }

    public static void setDatabaseClient(DatabaseClient databaseClient) {
        if(databaseClient == null) {
            throw new IllegalArgumentException();
        }
        sDatabaseClient = databaseClient;
    }

    public static void reset() {
        sDatabaseClient = new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
        sLocalCache = new DefaultLocalCache();
    }

    /**
     * Creates the LocalCache and DatabaseClient
     */
    private DataManager() {
        if(sDatabaseClient == null) {
            sDatabaseClient = new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
        }
        if(sLocalCache == null) {
            sLocalCache = new DefaultLocalCache();
        }
    }

    /**
     * Holder for the data manager.
     * Using initialization-on-demand to create a DataManager
     * the first time getInstance() is called.
     */
    private static class DataManagerHolder {
        private static final DataManager INSTANCE = new DataManager();
    }
}