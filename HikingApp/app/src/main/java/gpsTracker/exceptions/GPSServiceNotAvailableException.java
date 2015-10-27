package gpsTracker.exceptions;

/**
 * Exception thrown by GPSTracker to prevent GPS access without GPS service
 */
public class GPSServiceNotAvailableException extends Exception {

    public GPSServiceNotAvailableException() {
        super();
    }
    public GPSServiceNotAvailableException(String message) {
        super(message);
    }
    public GPSServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
    public GPSServiceNotAvailableException(Throwable cause) {
        super(cause);
    }
}
