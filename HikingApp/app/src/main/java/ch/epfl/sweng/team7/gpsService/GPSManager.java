package ch.epfl.sweng.team7.gpsService;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.database.Annotation;
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
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Class used to read device's GPS-related information
 * (such has latitude, longitude, altitude and time).
 */
public final class GPSManager {

    public static final String NEW_HIKE = "ch.epfl.sweng.team7.gpsService.NEW_HIKE";
    private final static String LOG_FLAG = "GPS_Manager";
    private final static int BOTTOM_TABLE_ACCESS_ID = 2;
    private final static GPSManager instance = new GPSManager();

    //GPS stored information
    private GPSPath mGpsPath = null;
    private boolean mIsTracking = false;
    private boolean mIsPaused = false;
    private GPSFootPrint mLastFootPrint = null;


    //GPS service communication
    private Context mContext;

    private GPSService mGpsService;
    private ServiceConnection mServiceConnection;
    private GPSService gpsService;
    private ServiceConnection serviceConnection;

    private Annotation annotation;
    private List<Annotation> listAnnotations = new ArrayList<>();
    private RawHikeData rawHikeData;



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
     * Method called to toggle tracking pause
     * on/off, according to previous state.
     */
    public void togglePause() {
        mIsPaused = !mIsPaused;
        if (!mIsPaused) {
            mGpsPath.addFootPrint(mLastFootPrint, true);
        }
    }

    /**
     * Method called to check on gps status
     * @return true if it is enabled, false otherwise
     */
    public boolean enabled() {
        return (mGpsService != null && mGpsService.getProviderStatus() && (mLastFootPrint != null));
    }

    /**
     * Method called to get the tracking status
     *
     * @return true if it is tracking, false otherwise
     */
    public boolean tracking() {
        return mIsTracking;
    }

    /**
     * Method called to get the paused status
     *
     * @return true if it is paused, false otherwise
     */
    public boolean paused() {
        return mIsPaused;
    }

    /**
     * Method called to get user's last known coordinates.
     *
     * @return GeoCoords object containing user's last known coordinates
     * @throws NullPointerException whenever there is no last known position
     */
    public GeoCoords getCurrentCoords() throws NullPointerException {
        if (mLastFootPrint == null) {
            throw new NullPointerException("Trying to access a null gps footprint");
        }
        return mLastFootPrint.getGeoCoords();
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
            mLastFootPrint = new GPSFootPrint(GeoCoords.fromLocation(newLocation), newLocation.getTime());
            if (mIsTracking && !mIsPaused) {
                mGpsPath.addFootPrint(mLastFootPrint);
                mInfoDisplay.setInfoLine(BOTTOM_TABLE_ACCESS_ID, 0, mContext.getResources().getString(R.string.timeElapsedInfo, mGpsPath.timeElapsedInSeconds()));
                mInfoDisplay.setInfoLine(BOTTOM_TABLE_ACCESS_ID, 1, mContext.getResources().getString(R.string.distanceToStart, mGpsPath.distanceToStart()));
            }
        }
    }

    /**
     * Method to add annotations
     * @param annotation
     */
    public void addAnnotation(Annotation annotation){
        listAnnotations.add(annotation);
    }

    @Override
    public String toString() {
        String gpsPathInformation = (mIsTracking && !mIsPaused && mGpsPath != null) ? String.format("yes -> %s", mGpsPath.toString()) : "No";
        String lastFootPrintCoords = (mLastFootPrint != null) ? mLastFootPrint.getGeoCoords().toString() : "null";
        long lastFootPrintTimeStamp = (mLastFootPrint != null) ? mLastFootPrint.getTimeStamp() : 0;
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
        mIsTracking = true;
        mIsPaused = false;
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
                MapActivity mapActivity = (MapActivity) mContext;
                if (mLastFootPrint != null) {
                    mapActivity.startFollowingUser();
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
        mIsTracking = false;
        mIsPaused = false;
        mNotification.hide();
        Log.d(LOG_FLAG, "Saving GPSPath to memory: " + mGpsPath.toString());
        try {
            rawHikeData = GPSPathConverter.toRawHikeData(mGpsPath);
        } catch (Exception e) {

        }

        displaySavePrompt();
        mInfoDisplay.releaseLock(BOTTOM_TABLE_ACCESS_ID);
        mInfoDisplay.hide(BOTTOM_TABLE_ACCESS_ID);
        mGpsPath = null;
    }


    /**
     * Method called after stopping a hike tracking.
     * This method should take the user to an editable Activity.
     */

    private void goToHikeEditor() {
        Intent intent = new Intent(mContext, HikeInfoActivity.class);
        intent.putExtra(GPSManager.NEW_HIKE, true);
        mContext.startActivity(intent);
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
                //TODO storePictures()
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.button_cancel_save), null);
        builder.show();
    }

    /**
     * Method used to turn on/off the location
     * listeners inside GPSService.
     */
    private void toggleListeners() {
        if (gpsService != null) {
            if (mIsTracking) {
                gpsService.enableListeners();
            } else {
                gpsService.disableListeners();
            }
        } else {
            Log.d(LOG_FLAG, "Could not access GPSService (null)");
        }
    }


    private void storeHike() {
        try {
            rawHikeData = GPSPathConverter.toRawHikeData(mGpsPath);
            rawHikeData.setAnnotations(listAnnotations);
            Log.d(LOG_FLAG, "GPS PATH CONVERTED");
            new StoreHikeTask().execute(rawHikeData);
        } catch (Exception e) {
            Log.d(LOG_FLAG, "CANNOT CONVERT GPS PATH");
        }
    }

    private void storePictures(List<Annotation> annotations) {
        if(annotations != null || annotations.size() > 1){
            for (int i = 0; i < annotations.size(); i++){
                new StorePictureTask().execute(annotations.get(i));
            }
        }
    }

    public void createAnnotation(String text) {
        RawHikePoint rawHikePoint = GPSPathConverter.getHikePointsFromGeoCoords(mLastFootPrint);
        Annotation annotation = new Annotation(rawHikePoint, text, null);
        listAnnotations.add(annotation);
        Log.d(LOG_FLAG, "Text annotation added to the list");
    }

    public void createPicture(Drawable drawable) {
        RawHikePoint rawHikePoint = GPSPathConverter.getHikePointsFromGeoCoords(mLastFootPrint);
        if(listAnnotations.size() > 0) {
            if (listAnnotations.get(listAnnotations.size() - 1).getRawHikePoint().getPosition().equals(rawHikePoint.getPosition())) {
                listAnnotations.get(listAnnotations.size() - 1).setPicture(drawable);
            }
        }else{
            Annotation annotation = new Annotation(rawHikePoint, null, drawable);
            listAnnotations.add(annotation);
        }
        Log.d(LOG_FLAG, "Picture annotation added to the list");
    }


    /**
     * Asynchronous task to make the post request to the server.
     */


    private class StoreHikeTask extends AsyncTask<RawHikeData, Long, Long> {
        @Override
        protected Long doInBackground(RawHikeData... rawHikeData) {
            long hikeId;
            DataManager dataManager = DataManager.getInstance();
            try {
                hikeId = dataManager.postHike(rawHikeData[0]);
                rawHikeData[0].setHikeId(hikeId);
                Log.d(LOG_FLAG, "Hike Post correctly");
                return hikeId;
            } catch (DataManagerException e) {
                Log.d(LOG_FLAG, "Error while posting hike");
            }
            return null;
        }

        protected void onPostExecute(Long... hikeId) {
        }
    }

    /**
     * Asynchronous task to make the post request to the server.
     */

    private class StorePictureTask extends AsyncTask<Annotation, Long, Long> {
        @Override
        protected Long doInBackground(Annotation... pictures) {
            long hikeId;
            DataManager dataManager = DataManager.getInstance();
            try {
                hikeId = dataManager.postPicture(pictures[0].getPicture());
                Log.d(LOG_FLAG, "Picture post correctly");
                return hikeId;
            } catch (DataManagerException e) {
                Log.d(LOG_FLAG, "Error while posting hike");
            }
            return null;
        }
        protected void onPostExecute(Long... hikeId) {
        }

    }

}
