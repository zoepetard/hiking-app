package ch.epfl.sweng.team7.gpsTracker;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.team7.gpsTracker.container.GeoCoords;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public class GPSTracker {

    private GeoCoords currentGeoCoords = null;

    public GPSTracker() {

    }

    public LatLng getLatLng() throws NullPointerException {
        if (currentGeoCoords == null) {
            throw new NullPointerException("Trying to access a null position");
        }
        return currentGeoCoords.toLatLng();
    }

    public void updateCurrentLocation(Location newLocation) {
        this.currentGeoCoords = GeoCoords.fromLocation(newLocation);
    }

    @Override
    public String toString() {
        if (currentGeoCoords == null) {
            return "No position tracked yet";
        }
        return currentGeoCoords.toString();
    }
}
