package ch.epfl.sweng.team7.gpsService;

import android.location.Location;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    private GPSPath gpsPath = null;
    private boolean isTracking = false;
    private GPSFootPrint lastFootPrint = null;
    private static GPSManager instance = new GPSManager();

    public static GPSManager getInstance() {
        return instance;
    }

    public void toggleTracking() {
        if (!isTracking) {
            starTracking();
        } else {
            stopTracking();
        }
    }

    private void starTracking() {
        this.isTracking = true;
        gpsPath = new GPSPath();
    }

    private void stopTracking() {
        this.isTracking = false;
        //TODO send GPSPath to another class, maybe DB, to store it in memory/upload it
        gpsPath = null;
    }

    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (this.lastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return this.lastFootPrint.getGeoCoords();
    }

    public void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            this.lastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
            if (this.isTracking) gpsPath.addFootPrint(this.lastFootPrint);
        }
    }

    @Override
    public String toString() {
        String lastFootPrintCoords = (this.lastFootPrint != null)?this.lastFootPrint.getGeoCoords().toString():"null";
        long lastFootPrintTimeStamp = (this.lastFootPrint != null)?this.lastFootPrint.getTimeStamp():0;
        return String.format("\n|---------------------------\n" +
                             "| Saving to memory: %b\n" +
                             "| Last Coordinates: %s\n" +
                             "| TimeStamp: %d\n" +
                             "|---------------------------", this.isTracking, lastFootPrintCoords, lastFootPrintTimeStamp);
    }

    private GPSManager() {}
}
