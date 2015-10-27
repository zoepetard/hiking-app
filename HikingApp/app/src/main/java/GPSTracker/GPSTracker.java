package GPSTracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;

import GPSTracker.Exceptions.GPSServiceNotAvailable;
import GPSTracker.Exceptions.NoPositionTrackedException;
import GPSTracker.Listener.GPSLocationListener;

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
     * @throws GPSServiceNotAvailable exception thrown if GPS service is not available
     */
    public GPSTracker(Context activityContext) throws GPSServiceNotAvailable {
        this.activityContext = activityContext;
        locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw new GPSServiceNotAvailable();

        if (checkUserPermission()) {
            locationListener = new GPSLocationListener(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_TIME_INTERVAL, UPDATE_MIN_DISTANCE, (LocationListener)locationListener);
    }

    /**
     * Method used to get user's location
     * @return new LatLng object containing information about the device's current position
     * @throws NoPositionTrackedException exception thrown when there is no position tracked yet
     */
    public LatLng getLatLng() throws NoPositionTrackedException {

        Location currentLocation = fetchCurrentLocation();

        /* This check is redundant, because an instance of
         * this class cannot be created if there is no GPS service.
         * Knowing there is GPS service, when this method is called,
         * means that onLocationChange method was called at least
         * once - so there should be a location already stored.
         */
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

    private boolean checkUserPermission() {
        if (activityContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activityContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
