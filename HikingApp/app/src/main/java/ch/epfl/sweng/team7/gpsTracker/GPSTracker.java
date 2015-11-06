package ch.epfl.sweng.team7.gpsTracker;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.team7.gpsTracker.container.GeoCoords;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public class GPSTracker {

    private GeoCoords currentLocation = null;

    public GPSTracker() {

    }

    public LatLng getLatLng() throws NullPointerException {
        if (currentLocation == null) {
            throw new NullPointerException("Trying to access a null position");
        }
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    public void updateCurrentLocation(Location newLocation) {
        double latitude = newLocation.getLatitude();
        double longitude = newLocation.getLongitude();
        double altitude = (newLocation.hasAltitude())?newLocation.getAltitude():0;
        this.currentLocation = new GeoCoords(latitude, longitude, altitude);
    }

    @Override
    public String toString() {
        if (currentLocation == null) {
            return "No position tracked yet";
        }
        return currentLocation.toString();
    }
}
