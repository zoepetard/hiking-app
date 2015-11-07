package ch.epfl.sweng.team7.gpsService;

import android.location.Location;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public abstract class GPSManager {

    private static GPSFootPrint lastFootPrint = null;

    public static GeoCoords getCurrentCoords() throws NullPointerException {
        if (lastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return lastFootPrint.getGeoCoords();
    }

    public static void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            lastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
        }
    }

    @Override
    public String toString() {
        if (lastFootPrint == null) {
            return "No footprint stored yet";
        }
        return lastFootPrint.toString();
    }
}
