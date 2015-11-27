package ch.epfl.sweng.team7.gpsService;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.GPSPathConverter;
import ch.epfl.sweng.team7.gpsService.NotificationHandler.NotificationHandler;
import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.hikingapp.HikeInfoActivity;
import ch.epfl.sweng.team7.hikingapp.MapActivity;
import ch.epfl.sweng.team7.hikingapp.R;
import ch.epfl.sweng.team7.hikingapp.mapActivityElements.BottomInfoView;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.RawHikeData;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    public static final String NEW_HIKE = "ch.epfl.sweng.team7.gpsService.NEW_HIKE";
    private final static String LOG_FLAG = "GPS_Manager";
    private final static int BOTTOM_TABLE_ACCESS_ID = 2;
    private static GPSManager instance = new GPSManager();

    //GPS stored information
    private GPSPath mGpsPath = null;
    private boolean mIsTracking = false;
    private GPSFootPrint mLastFootPrint = null;


    //GPS service communication
    private Context mContext;
    private GPSService mGpsService;
    private ServiceConnection mServiceConnection;

    private NotificationHandler mNotification;
    private BottomInfoView mInfoDisplay;

    private GPSManager() {
        mInfoDisplay = BottomInfoView.getInstance();
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
        if (mGpsService != null) {
            if (!mIsTracking) {
                startTracking();
            } else {
                stopTracking();
            }
            toggleListeners();
        } else {
            displayToastMessage(mContext.getResources().getString(R.string.gps_service_access_failure));
            Log.d(LOG_FLAG, "Could not access GPSService (null)");
        }
    }

    /**
     * Method called to check on gps status
     * @return true if it is enabled, false otherwise
     */
    public boolean enabled() {
        if (mGpsService != null) {
            return mGpsService.getProviderStatus() && (mLastFootPrint != null);
        }
        return false;
    }

    /**
     * Method called to get the tracking status
     *
     * @return true if it is tracking, false otherwise
     */
    public Boolean tracking() {
        return mIsTracking;
    }

    /**
     * Method called to get user's last known coordinates.
     *
     * @return GeoCoords object containing user's last known coordinates
     * @throws NullPointerException whenever there is no last known position
     */
    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (this.mLastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return this.mLastFootPrint.getGeoCoords();
    }

    /**
     * Method called to start the GPSService, by means of an Intent
     *
     * @param context the context from which the Intent will be sent.
     */
    public void startService(Context context) {
        mContext = context;
        context.startService(new Intent(context, GPSService.class));
        Log.d(LOG_FLAG, "Intent sent to start GPSService");
        mNotification = NotificationHandler.getInstance();
        mNotification.setup(context);
    }

    /**
     * Method called to bind GPSService to a certain Context
     *
     * @param context Context to which the GPSService will be bound to
     */
    public void bindService(Context context) {
        context.bindService(new Intent(context, GPSService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_FLAG, "Intent sent to bind to GPSService");
    }

    /**
     * Method called to unbind GPSService from a certain Context
     *
     * @param context Context from which the GPSService will be unbound
     */
    public void unbindService(Context context) {
        context.unbindService(mServiceConnection);
        Log.d(LOG_FLAG, "Intent sent to unbind GPSService");
    }

    /**
     * Method called to update user's last know coordinates
     *
     * @param newLocation Location object containing GPS fetched data
     */
    protected void updateCurrentLocation(Location newLocation) {
        if (newLocation != null) {
            this.mLastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
            if (this.mIsTracking) {
                mGpsPath.addFootPrint(this.mLastFootPrint);
                mInfoDisplay.setInfoLine(BOTTOM_TABLE_ACCESS_ID, 0, mContext.getResources().getString(R.string.timeElapsedInfo, mGpsPath.timeElapsedInSeconds()));
                mInfoDisplay.setInfoLine(BOTTOM_TABLE_ACCESS_ID, 1, mContext.getResources().getString(R.string.distanceToStart, mGpsPath.distanceToStart()));
            }
        }
    }

    @Override
    public String toString() {
        String gpsPathInformation = (mIsTracking && mGpsPath != null) ? String.format("yes -> %s", mGpsPath.toString()) : "No";
        String lastFootPrintCoords = (this.mLastFootPrint != null) ? this.mLastFootPrint.getGeoCoords().toString() : "null";
        long lastFootPrintTimeStamp = (this.mLastFootPrint != null) ? this.mLastFootPrint.getTimeStamp() : 0;
        return String.format("\n|---------------------------\n" +
                "| Saving to memory: %s\n" +
                "| Last Coordinates: %s\n" +
                "| TimeStamp: %d\n" +
                "|---------------------------", gpsPathInformation, lastFootPrintCoords, lastFootPrintTimeStamp);
    }

    /**
     * Called by the GPSService to access the Context of the app.
     * @return Context
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * Private method to setup communication with the
     * GPSService that will be running in the background.
     */
    private void setupServiceConnection() {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established
                mGpsService = ((GPSService.LocalBinder) service).getService();
                Log.d(LOG_FLAG, "Successfully connected to service");
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected
                mGpsService = null;
                displayToastMessage(mContext.getResources().getString(R.string.gps_service_connection_dropped));
                Log.d(LOG_FLAG, "Connection to service was dropped...");
            }
        };
    }

    /**
     * Method called to start tracking - start
     * storing user's coordinates.
     */
    private void startTracking() {
        this.mIsTracking = true;
        mGpsPath = new GPSPath();
        mInfoDisplay.requestLock(BOTTOM_TABLE_ACCESS_ID);
        mInfoDisplay.setTitle(BOTTOM_TABLE_ACCESS_ID, "Current hike");
        mInfoDisplay.clearInfoLines(BOTTOM_TABLE_ACCESS_ID);
        mInfoDisplay.addInfoLine(BOTTOM_TABLE_ACCESS_ID, "");
        mInfoDisplay.addInfoLine(BOTTOM_TABLE_ACCESS_ID, "");
        mInfoDisplay.show(BOTTOM_TABLE_ACCESS_ID);
        mInfoDisplay.setOnClickListener(BOTTOM_TABLE_ACCESS_ID, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = (MapActivity)mContext;
                if (mLastFootPrint != null) {
                    mapActivity.focusLatLng(mLastFootPrint.getGeoCoords().toLatLng());
                }
            }
        });
        mNotification.display();
    }

    /**
     * Method called to stop tracking - stop
     * storing user's coordinates and store the
     * previous ones.
     */
    private void stopTracking() {
        this.mIsTracking = false;
        mNotification.hide();
        Log.d(LOG_FLAG, "Saving GPSPath to memory: " + mGpsPath.toString());
        displaySavePrompt();
        mInfoDisplay.releaseLock(BOTTOM_TABLE_ACCESS_ID);
        mInfoDisplay.hide(BOTTOM_TABLE_ACCESS_ID);
        mGpsPath = null;
    }

    private void goToHikeEditor() {
        Intent intent = new Intent(mContext, HikeInfoActivity.class);
        intent.putExtra(GPSManager.NEW_HIKE, true);
        mContext.startActivity(intent);
    }

    /**
     * Method used to turn on/off the location
     * listeners inside GPSService.
     */
    private void toggleListeners() {
        if (mIsTracking) {
            mGpsService.enableListeners();
        } else {
            mGpsService.disableListeners();
        }
    }

    /**
     * Method called internally to give feedback to the user
     *
     * @param message message to be displayed inside a Toast.
     */
    protected void displayToastMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Method called to display a Dialog with EditText fields
     * for the user to edit  some hike settings.
     */
    private void displaySavePrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.prompt_title));

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        //setup the horizontal separator
        View lnSeparator = new View(mContext);
        lnSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5));
        lnSeparator.setBackgroundColor(Color.parseColor("#B3B3B3"));
        layout.addView(lnSeparator);

        //setup the hike title input field
        EditText hikeTitle = new EditText(mContext);
        hikeTitle.setHint(mContext.getResources().getString(R.string.prompt_title_hint));
        hikeTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(hikeTitle);

        //setup the hike comment input field
        EditText hikeComment = new EditText(mContext);
        hikeComment.setHint(mContext.getResources().getString(R.string.prompt_comment_hint));
        hikeComment.setInputType(InputType.TYPE_CLASS_TEXT);
        hikeComment.setSingleLine(false);
        layout.addView(hikeComment);

        builder.setView(layout);

        builder.setPositiveButton(mContext.getResources().getString(R.string.button_save_hike), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO call storeHike() after issue #86 is fixed
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.button_cancel_save), null);
        builder.show();
    }

    /**
     * Method called to store recorded hike
     */
    private void storeHike() {
        RawHikeData rawHikeData = null;
        try {
            rawHikeData = GPSPathConverter.toRawHikeData(mGpsPath);
        } catch (Exception e) {
            //TODO
        }
        try {
            storeHikeInDB(rawHikeData);
        } catch (DatabaseClientException e) {
            //TODO, we need the button to store hikes to show the error message to the user.
        }
    }

    /**
     * Method to store in DB the RawHikeData converted from the GPS object
     *
     * @param rawHikeData
     */
    private void storeHikeInDB(RawHikeData rawHikeData) throws DatabaseClientException {
        DataManager dataManager = DataManager.getInstance();
        try {
            dataManager.postHike(rawHikeData);
        } catch (DataManagerException e) {
            //TODO
        }
    }
}
