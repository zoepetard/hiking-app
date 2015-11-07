package ch.epfl.sweng.team7.gpsService;

import android.location.Location;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    private boolean isTracking = false;
    private GPSFootPrint lastFootPrint = null;
    private static GPSManager instance = new GPSManager();

    public static GPSManager getInstance() {
        return instance;
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
        }
    }

    @Override
    public String toString() {
        String lastFootPrintString = (this.lastFootPrint != null)?this.lastFootPrint.toString():"null";
        return String.format("[-------------------]\n" +
                             "Saving to memory: %b\n" +
                             "Last FootPrint: %s\n" +
                             "[-------------------]", this.isTracking, lastFootPrintString);
    }

    private GPSManager() {}
}
