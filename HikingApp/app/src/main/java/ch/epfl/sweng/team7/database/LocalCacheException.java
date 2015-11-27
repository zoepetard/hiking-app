/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 04 Nov 2015
 */

package ch.epfl.sweng.team7.database;

/**
 * Thrown to indicate a problem encountered by the {@link LocalCache} when
 * communicating to the footpath server.
 */
class LocalCacheException extends Exception {

    private final static String LOG_FLAG = "DB_LocalCacheException";
    private static final long serialVersionUID = 1L;

    public LocalCacheException() {
        super();
    }

    public LocalCacheException(String message) {
        super(message);
    }

    public LocalCacheException(Throwable throwable) {
        super(throwable);
    }
}
