/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 05 Nov 2015
 */

package ch.epfl.sweng.team7.database;

/**
 * Thrown to indicate a problem encountered by the {@link LocalCache} when
 * communicating to the footpath server.
 */
public class DataManagerException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataManagerException() {
        super();
    }

    public DataManagerException(String message) {
        super(message);
    }

    public DataManagerException(Throwable throwable) {
        super(throwable);
    }
}
