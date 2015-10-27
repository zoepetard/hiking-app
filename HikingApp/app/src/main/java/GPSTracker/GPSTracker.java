package GPSTracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import GPSTracker.Exceptions.GPSServiceNotAvailable;
import GPSTracker.Exceptions.NoPositionTrackedException;

/**
 * Class used to fetch device's GPS-related information
 * (such has latitude, longitude and altitude)
 */
public class GPSTracker implements LocationListener {

    private Context activityContext = null;
    private LocationManager locationManager = null;

    //updated automatically when user's location changes
    private Location currentLocation = null;

    /**
     * Class' constructor
     * @param activityContext the context (or activity) from which the GPSTracker was instantiated from
     * @throws GPSServiceNotAvailable exception thrown if GPS service is not available
     */
    public GPSTracker(Context activityContext) throws GPSServiceNotAvailable {
        this.activityContext = activityContext;
        locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) throw new GPSServiceNotAvailable();
    }

    /**
     * Method used to get user's location
     * @return new LatLng object containing information about the device's current position
     * @throws NoPositionTrackedException exception thrown when there is no position tracked yet
     */
    public LatLng getLatLng() throws NoPositionTrackedException {

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
        if (currentLocation == null) {
            return "No position tracked yet";
        }
        return currentLocation.toString();
    }

    /*///////////////////////////////////////////
    ///////// Interface's default methods ///////
    ///////////////////////////////////////////*/

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
