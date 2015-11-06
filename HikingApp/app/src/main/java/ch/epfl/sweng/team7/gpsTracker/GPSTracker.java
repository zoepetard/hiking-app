package ch.epfl.sweng.team7.gpsTracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public class GPSTracker {

    private Location currentLocation = null;

    public GPSTracker() {
        
    }

    public LatLng getLatLng() throws NullPointerException {
        if (currentLocation == null) {
            throw new NullPointerException("Trying to access a null position");
        }
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    public void updateCurrentLocation(Location newLocation) {
        this.currentLocation = newLocation;
    }

    @Override
    public String toString() {
        if (currentLocation == null) {
            return "No position tracked yet";
        }
        return currentLocation.toString();
    }
}
