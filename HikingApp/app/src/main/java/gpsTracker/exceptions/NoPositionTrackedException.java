package gpsTracker.exceptions;

/**
 * Exception thrown by GPSTracker to prevent a null location access
 */
public class NoPositionTrackedException extends Exception {

    public NoPositionTrackedException() {
        super();
    }
    public NoPositionTrackedException(String message) {
        super(message);
    }
    public NoPositionTrackedException(String message, Throwable cause) {
        super(message, cause);
    }
    public NoPositionTrackedException(Throwable cause) {
        super(cause);
    }
}
