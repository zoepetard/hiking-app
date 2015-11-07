package ch.epfl.sweng.team7.gpsTracker;

import android.location.Location;

import ch.epfl.sweng.team7.gpsTracker.container.GeoCoords;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public final class GPSTracker {

    private GeoCoords currentGeoCoords = null;

    public GPSTracker() {

    }

    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (currentGeoCoords == null) {
            throw new NullPointerException("Trying to access a null position");
        }
        return currentGeoCoords;
    }

    public void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            this.currentGeoCoords = GeoCoords.fromLocation(newLocation);
        }
    }

    @Override
    public String toString() {
        if (currentGeoCoords == null) {
            return "No position tracked yet";
        }
        return currentGeoCoords.toString();
    }
}
