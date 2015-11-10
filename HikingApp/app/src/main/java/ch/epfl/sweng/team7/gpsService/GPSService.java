package ch.epfl.sweng.team7.gpsService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Class used as a 'background task'. It is in charge of
 * updating user's current position, according to GPS information,
 * throughout all the app's activities.
 */
public class GPSService extends Service {

    private static final String LOG_FLAG = "GPS_Service";

    private final IBinder mBinder = new LocalBinder();
    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * Class for clients to access.
     */
    public class LocalBinder extends Binder {
        GPSService getService() {
            return GPSService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(LOG_FLAG, "GPSService has started");
        gpsSetup();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_FLAG, "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_FLAG, "GPSService has stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void gpsSetup() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                gps.updateLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Log.d(LOG_FLAG, "Could not request location updates");
        }
    }
}
