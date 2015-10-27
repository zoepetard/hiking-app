package GPSTracker.Exceptions;

/**
 * Exception thrown by GPSTracker to prevent GPS access without GPS service
 */
public class GPSServiceNotAvailable extends Exception {

    public GPSServiceNotAvailable() {
        super();
    }
    public GPSServiceNotAvailable(String message) {
        super(message);
    }
    public GPSServiceNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }
    public GPSServiceNotAvailable(Throwable cause) {
        super(cause);
    }
}
