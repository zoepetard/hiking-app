/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 04 Nov 2015
 */
package ch.epfl.sweng.team7.database;

/**
 * The LocalCache saves a limited number of hike objects from the online database.
 * All database access should be routed through the cache.
 * Database access may be blocking and must not be called from a UI thread.
 */
public interface LocalCache {
}
