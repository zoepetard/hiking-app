/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 05 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawUserData;

public final class DataManager {

    private final static String LOG_FLAG = "DB_DataManager";
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
     * Static setter: Use only for testing!
     */
    public static void setLocalCache(LocalCache localCache) {
        if (localCache == null) {
            throw new IllegalArgumentException();
        }
        sLocalCache = localCache;
    }

    /**
     * Static setter: Use only for testing!
     */
    public static void setDatabaseClient(DatabaseClient databaseClient) {
        if (databaseClient == null) {
            throw new IllegalArgumentException();
        }
        sDatabaseClient = databaseClient;
    }

    /**
     * Static reset: Use only for testing!
     */
    public static void reset() {
        sDatabaseClient = new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
        sLocalCache = new DefaultLocalCache();
    }

    /**
     * Get a HikeData object by its identifier.
     *
     * @return a valid HikeData object
     * @throws DataManagerException on error
     */
    public HikeData getHike(long hikeId) throws DataManagerException {

        // Check if hike is cached
        HikeData hikeData = sLocalCache.getHike(hikeId);
        if (hikeData != null) {
            return hikeData;
        }

        // Retrieve hike from the server
        try {
            RawHikeData rawHikeData = sDatabaseClient.fetchSingleHike(hikeId);
            hikeData = processAndCache(rawHikeData);
        } catch (DatabaseClientException e) {
            throw new DataManagerException(e);
        }
        return hikeData;
    }

    /**
     * Get multiple HikeData objects by its identifiers
     *
     * @return a list of valid HikeData objects
     * @throws DataManagerException on error
     */
    public List<HikeData> getMultipleHikes(List<Long> hikeIdList) throws DataManagerException {

        // Compile a list of hikes to ask from the server
        List<HikeData> hikeDataList = new ArrayList<>();
        List<Long> hikeIdNotCached = new ArrayList<>();
        for (long hikeId : hikeIdList) {
            HikeData hikeData = sLocalCache.getHike(hikeId);
            if (hikeData != null) {
                hikeDataList.add(hikeData);
            } else {
                hikeIdNotCached.add(hikeId);
            }
        }

        // Ask the server
        if (hikeIdNotCached.size() > 0) {
            List<RawHikeData> rawHikeDataList;
            try {
                rawHikeDataList = sDatabaseClient.fetchMultipleHikes(hikeIdNotCached);
            } catch (DatabaseClientException e) {
                throw new DataManagerException(e);
            }

            // Convert and cache HikeData
            for (RawHikeData rawHikeData : rawHikeDataList) {
                hikeDataList.add(processAndCache(rawHikeData));
            }
        }
        return hikeDataList;
    }

    /**
     * Retrieves a list of all hikes in given boundaries
     *
     * @param bounds the boundaries of a rectangle
     * @return a list of hikes in the given rectangle
     * @throws DataManagerException on error
     */
    public List<HikeData> getHikesInWindow(LatLngBounds bounds) throws DataManagerException {

        // Ask the server for the hike Ids
        List<Long> hikeIdList;
        try {
            hikeIdList = sDatabaseClient.getHikeIdsInWindow(bounds);
        } catch (DatabaseClientException e) {
            throw new DataManagerException(e);
        }

        return getMultipleHikes(hikeIdList);
    }

    /**
     * Converts a RawHikeData container into a cacheable HikeData object, caches and returns it
     */
    private HikeData processAndCache(RawHikeData rawHikeData) {
        HikeData hikeData = new DefaultHikeData(rawHikeData);
        sLocalCache.putHike(hikeData);
        return hikeData;
    }

    /**
     * Store a user data object in database and update local cache
     *
     * @param rawUserData - a raw user data object
     */
    public void setUserData(RawUserData rawUserData) throws DataManagerException {

        // update user data in cache and database
        try {
            sDatabaseClient.postUserData(rawUserData);
            UserData defaultUserData = new DefaultUserData(rawUserData);
            sLocalCache.setUserData(defaultUserData);
        } catch (DatabaseClientException e) {
            throw new DataManagerException(e);
        } catch (NullPointerException e) {
            throw new DataManagerException(e.getMessage());
        }
    }

    /**
     * TODO server side needs to be implemented before this can work
     * Change user name
     *
     * @param newName,mailAddress the new user name and mailAddress as identifier
     */
    public void changeUserName(String newName, long userId) throws DataManagerException {

        // get current user data then update the database
        UserData userData = getUserData(userId);
        RawUserData rawUserData = new RawUserData(userData.getUserId(), newName, userData.getMailAddress());
        setUserData(rawUserData);

    }

    /**
     * Retrieve a user data object from cache or database
     * TODO server side needs to be implemented before this can work correctly
     *
     * @param userId - id assigned to identify user
     * @return UserData object
     */
    public UserData getUserData(long userId) throws DataManagerException {

        // check if user data is stored in cache, otherwise return from server
        if (sLocalCache.getUserData(userId) != null &&
                sLocalCache.getUserData(userId).getUserId() == userId) {
            return sLocalCache.getUserData(userId);
        } else {
            try {
                RawUserData rawUserData = sDatabaseClient.fetchUserData(userId);
                UserData userData = new DefaultUserData(rawUserData);
                sLocalCache.setUserData(userData); // update cache
                return userData;
            } catch (DatabaseClientException e) {
                throw new DataManagerException(e);
            } catch (Exception e) {
                throw new DataManagerException(e.getMessage());
            }
        }
    }


    /**
     * Creates the LocalCache and DatabaseClient
     */
    private DataManager() {
        if (sDatabaseClient == null) {
            sDatabaseClient = new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
        }
        if (sLocalCache == null) {
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
