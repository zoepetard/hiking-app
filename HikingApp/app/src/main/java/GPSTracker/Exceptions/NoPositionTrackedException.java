package GPSTracker.Exceptions;

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
