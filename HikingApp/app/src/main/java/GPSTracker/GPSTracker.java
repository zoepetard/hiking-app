package GPSTracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import GPSTracker.Exceptions.GPSServiceNotAvailable;
import GPSTracker.Exceptions.NoPositionTrackedException;

public class GPSTracker implements LocationListener {

    private Context activityContext = null;
    private LocationManager locationManager = null;

    private Location currentLocation = null;

    public GPSTracker(Context activityContext) throws GPSServiceNotAvailable {
        this.activityContext = activityContext;
        locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) throw new GPSServiceNotAvailable();
    }

    public LatLng getLatLng() throws NoPositionTrackedException {
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
