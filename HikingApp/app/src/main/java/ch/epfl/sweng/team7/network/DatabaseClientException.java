/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on SwEngHomework3 QuizClientException class
 */

package ch.epfl.sweng.team7.network;

/**
 * Thrown to indicate a problem encountered by a {@link DatabaseClient} when
 * communicating to the footpath server.
 *
 */
public class DatabaseClientException extends Exception {

    private final static String LOG_FLAG = "Network_DatabaseClientException";
    private static final long serialVersionUID = 1L;
    
    public DatabaseClientException() {
        super();
    }
    
    public DatabaseClientException(String message) {
        super(message);
    }
    
    public DatabaseClientException(Throwable throwable) {
        super(throwable);
    }
}
