package gps_tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import gps_tracker.exceptions.GPSServiceNotAvailableException;
import gps_tracker.exceptions.NoPositionTrackedException;
import gps_tracker.listener.GPSLocationListener;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public class GPSTracker {

    private static final long UPDATE_MIN_TIME_INTERVAL = 5000L;
    private static final float UPDATE_MIN_DISTANCE = 15.0f;

    private Context activityContext = null;
    private LocationManager locationManager = null;
    private GPSLocationListener locationListener = null;

    /**
     * Class' constructor
     * @param activityContext the context (or activity) from which the GPSTracker was instantiated from
     * @throws GPSServiceNotAvailableException exception thrown if GPS service is not available
     */
    public GPSTracker(Context activityContext) throws GPSServiceNotAvailableException {
        this.activityContext = activityContext;
        locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw new GPSServiceNotAvailableException();

        try {
            locationListener = new GPSLocationListener(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        } catch (SecurityException e) {
            //TODO display a warning saying that the GPS service cannot be accessed
        }
        //TODO call requestLocationUpdates to set up automatic position updating
    }

    /**
     * Method used to get user's location
     * @return new LatLng object containing information about the device's current position
     * @throws NoPositionTrackedException exception thrown when there is no position tracked yet
     */
    public LatLng getLatLng() throws NoPositionTrackedException {

        Location currentLocation = fetchCurrentLocation();

        if (currentLocation == null) {
            throw new NoPositionTrackedException();
        }
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    @Override
    public String toString() {
        Location currentLocation = fetchCurrentLocation();
        if (currentLocation == null) {
            return "No position tracked yet";
        }
        return currentLocation.toString();
    }

    private Location fetchCurrentLocation() {
        return (locationListener != null)?locationListener.getCurLocation():null;
    }
}
