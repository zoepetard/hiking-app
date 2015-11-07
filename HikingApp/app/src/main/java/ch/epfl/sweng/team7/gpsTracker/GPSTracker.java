package ch.epfl.sweng.team7.gpsTracker;

import android.location.Location;

import ch.epfl.sweng.team7.gpsTracker.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsTracker.containers.coordinates.GeoCoords;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public final class GPSTracker {

    private GPSFootPrint lastFootPrint = null;

    public GPSTracker() {

    }

    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (lastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return lastFootPrint.getGeoCoords();
    }

    public void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            this.lastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
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
