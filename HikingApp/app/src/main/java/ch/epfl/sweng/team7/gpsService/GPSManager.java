package ch.epfl.sweng.team7.gpsService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.GPSPathConverter;
import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.RawHikeData;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    private final static String LOG_FLAG = "GPS_Manager";
    private static GPSManager instance = new GPSManager();

    //GPS stored information
    private GPSPath gpsPath = null;
    private boolean isTracking = false;
    private GPSFootPrint lastFootPrint = null;


    //GPS service communication
    private GPSService gpsService;
    private ServiceConnection serviceConnection;


    private GPSManager() {
        setupServiceConnection();
    }

    public static GPSManager getInstance() {
        return instance;
    }

    /**
     * Method called to toggle hike tracking
     * on/off, according to previous state.
     */
    public void toggleTracking() {
        if (!isTracking) {
            startTracking();
        } else {
            stopTracking();
        }
        toggleListeners();
    }

    /**
     * Method called to get user's last known coordinates.
     *
     * @return GeoCoords object containing user's last known coordinates
     * @throws NullPointerException whenever there is no last known position
     */
    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (this.lastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return this.lastFootPrint.getGeoCoords();
    }

    /**
     * Method called to start the GPSService, by means of an Intent
     *
     * @param context the context from which the Intent will be sent.
     */
    public void startService(Context context) {
        context.startService(new Intent(context, GPSService.class));
        Log.d(LOG_FLAG, "Intent sent to start GPSService");
    }

    /**
     * Method called to bind GPSService to a certain Context
     * @param context Context to which the GPSService will be bound to
     */
    public void bindService(Context context) {
        context.bindService(new Intent(context, GPSService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_FLAG, "Intent sent to bind to GPSService");
    }

    /**
     * Method called to unbind GPSService from a certain Context
     * @param context Context from which the GPSService will be unbound
     */
    public void unbindService(Context context) {
        context.unbindService(serviceConnection);
        Log.d(LOG_FLAG, "Intent sent to unbind GPSService");
    }

    /**
     * Method called to update user's last know coordinates
     *
     * @param newLocation Location object containing GPS fetched data
     */
    protected void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            this.lastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
            if (this.isTracking) gpsPath.addFootPrint(this.lastFootPrint);
        }
    }

    @Override
    public String toString() {
        String gpsPathInformation = (isTracking && gpsPath != null) ? String.format("yes -> %s", gpsPath.toString()) : "No";
        String lastFootPrintCoords = (this.lastFootPrint != null) ? this.lastFootPrint.getGeoCoords().toString() : "null";
        long lastFootPrintTimeStamp = (this.lastFootPrint != null) ? this.lastFootPrint.getTimeStamp() : 0;
        return String.format("\n|---------------------------\n" +
                "| Saving to memory: %s\n" +
                "| Last Coordinates: %s\n" +
                "| TimeStamp: %d\n" +
                "|---------------------------", gpsPathInformation, lastFootPrintCoords, lastFootPrintTimeStamp);
    }

    /**
     * Private method to setup communication with the
     * GPSService that will be running in the background.
     */
    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established
                gpsService = ((GPSService.LocalBinder) service).getService();
                Log.d(LOG_FLAG, "Successfully connected to service");
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected
                gpsService = null;
                Log.d(LOG_FLAG, "Connection to service was dropped...");
            }
        };
    }

    /**
     * Method called to start tracking - start
     * storing user's coordinates.
     */
    private void startTracking() {
        this.isTracking = true;
        gpsPath = new GPSPath();
    }

    /**
     * Method called to stop tracking - stop
     * storing user's coordinates and store the
     * previous ones.
     */
    private void stopTracking() {
        this.isTracking = false;
        RawHikeData rawHikeData = null;
        try {
            rawHikeData = GPSPathConverter.toRawHikeData(gpsPath);
        } catch (Exception e) {
            //TODO
        }
        try {
            storeHike(rawHikeData);
        } catch (DatabaseClientException e) {
            //TODO, we need the button to store hikes to show the error message to the user.
        }
        Log.d(LOG_FLAG, "Saving GPSPath to memory: " + gpsPath.toString());
        gpsPath = null;
    }

    /**
     * Method used to turn on/off the location
     * listeners inside GPSService.
     */
    private void toggleListeners() {
        if (gpsService != null) {
            if (isTracking) {
                gpsService.enableListeners();
            } else {
                gpsService.disableListeners();
            }
        } else {
            Log.d(LOG_FLAG, "Could not access GPSService (null)");
        }
    }

    /**
     * Method to store in DB the RawHikeData converted from the GPS object
     *
     * @param rawHikeData
     */
    private void storeHike(RawHikeData rawHikeData) throws DatabaseClientException {
        DataManager dataManager = DataManager.getInstance();
        try {
            dataManager.postHike(rawHikeData);
        } catch (DataManagerException e) {
            //TODO
        }
    }
}