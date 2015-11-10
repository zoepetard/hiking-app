package ch.epfl.sweng.team7.gpsService;

import android.location.Location;
import android.util.Log;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    private static GPSManager instance = new GPSManager();

    private GPSPath gpsPath = null;
    private boolean isTracking = false;
    private GPSFootPrint lastFootPrint = null;

    public static GPSManager getInstance() {
        return instance;
    }

    public void toggleTracking() {
        if (!isTracking) {
            startTracking();
        } else {
            stopTracking();
        }
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
        String gpsPathInformation = (isTracking && gpsPath != null)?String.format("yes -> %s", gpsPath.toString()):"No";
        String lastFootPrintCoords = (this.lastFootPrint != null)?this.lastFootPrint.getGeoCoords().toString():"null";
        long lastFootPrintTimeStamp = (this.lastFootPrint != null)?this.lastFootPrint.getTimeStamp():0;
        return String.format("\n|---------------------------\n" +
                "| Saving to memory: %s\n" +
                "| Last Coordinates: %s\n" +
                "| TimeStamp: %d\n" +
                "|---------------------------", gpsPathInformation, lastFootPrintCoords, lastFootPrintTimeStamp);
    }

    private GPSManager() {}

    private void startTracking() {
        this.isTracking = true;
        gpsPath = new GPSPath();
    }

    private void stopTracking() {
        this.isTracking = false;
        //TODO send GPSPath to another class, maybe DB, to store it in memory/upload it
        Log.d("LocationUpdate", "Saving GPSPath to memory: " + gpsPath.toString());
        gpsPath = null;
    }
}
