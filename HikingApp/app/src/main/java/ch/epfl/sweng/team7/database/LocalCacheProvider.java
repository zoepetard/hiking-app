/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;

/**
 * A class that provides access to the local cache of hike objects.
 */
public class LocalCacheProvider {
    private static final String SERVER_URL = "http://footpath-1104.appspot.com";//"http://10.0.3.2:8080";
    private static DatabaseClient mDatabaseClient;
    private static LocalCache mLocalCache;

    public static LocalCache getLocalCache() {

        // Create default database client
        if(mDatabaseClient == null) {
            mDatabaseClient = new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
        }

        // Create default local cache
        if(mLocalCache == null) {
            mLocalCache = new DefaultLocalCache(mDatabaseClient);
        }

        return mLocalCache;
    }

    /**
     * For testing only:
     * Configure to use a different database client. This clears the cache.
     */
    public static void configureDatabaseClient(DatabaseClient databaseClient) {
        mDatabaseClient = databaseClient;
        mLocalCache = null;
    }

    /**
     * For testing only:
     * Reset the provider. This clears the cache.
     */
    public static void configureReset() {
        mDatabaseClient = null;
        mLocalCache = null;
    }

    private LocalCacheProvider(){}
}
