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

import ch.epfl.sweng.team7.hikingapp.R;

/**
 * Class used as a 'background task'. It is in charge of
 * updating user's current position, according to GPS information,
 * throughout all the app's activities.
 */
public class GPSService extends Service {

    public static boolean isBound = false;
    private static final String LOG_FLAG = "GPS_Service";

    private final IBinder mBinder = new LocalBinder();
    private GPSManager gps;
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
        isBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return true; // ensures onRebind is called
    }

    @Override
    public void onRebind(Intent intent) {
        isBound = true;
    }

    /**
     * Method called by GPSManager to check on location provider status
     * @return true if at least one is enabled, false otherwise
     */
    protected boolean getProviderStatus() {
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    /**
     * Method called from within GPSManager to control when
     * location updates are necessary.
     */
    protected void enableListeners() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            gps.displayToastMessage(gps.getContext().getResources().getString(R.string.gps_location_updates_failure));
            Log.d(LOG_FLAG, "Could not request location updates");
        }
    }

    /**
     * Method called from within GPSManager to control when
     * location updates are no longer necessary.
     */
    protected void disableListeners() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            Log.d(LOG_FLAG, "Could not cancel location updates");
        }
    }

    /**
     * Method called once to setup GPS related variables and
     * automatic updates from both network and gps providers.
     */
    private void gpsSetup() {
        gps = GPSManager.getInstance();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gps.updateCurrentLocation(location);
                Log.d(LOG_FLAG, "GPS status: " + gps.toString());
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
        };

        try {
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                gps.updateCurrentLocation(gpsLocation);
            } else {
                Location netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                gps.updateCurrentLocation(netLocation);
            }
        } catch (SecurityException e) {
            Log.d(LOG_FLAG, "Could not request location from providers");
        }
    }
}
