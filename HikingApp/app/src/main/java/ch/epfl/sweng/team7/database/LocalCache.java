/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 04 Nov 2015
 */
package ch.epfl.sweng.team7.database;



import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * The LocalCache saves a limited number of hike objects from the online database.
 * All database access should be routed through the cache.
 * Database access may be blocking and must not be called from a UI thread.
 */
interface LocalCache {

    boolean hasHike(long hikeId);

    /**
     * Get a hike with a known identifier.
     *
     * @param hikeId the identifier
     * @return a valid HikeData object or null if it doesn't exist
     */
    HikeData getHike(long hikeId);

    void putHike(HikeData hikeData);

    int cachedHikesCount();

    void setUserData(UserData userData);

    UserData getUserData(long userId);

    void removeHike(long hikeId);
    
    List<Long> searchHike(String query);

    Drawable getPicture(long pictureId);

    void putPicture(Drawable picture, long pictureId);

}

