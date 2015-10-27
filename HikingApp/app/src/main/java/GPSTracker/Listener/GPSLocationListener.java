package GPSTracker.Listener;

import android.location.Location;

import com.google.android.gms.location.LocationListener;

public class GPSLocationListener implements LocationListener {

    //updated automatically when user's location changes
    private Location currentLocation = null;

    public GPSLocationListener(Location initialLocation) {
        currentLocation = initialLocation;
    }

    public Location getCurLocation() {
        return this.currentLocation;
    }

    /*///////////////////////////////////////////
    ///////// Interface's default methods ///////
    ///////////////////////////////////////////*/

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }
}
