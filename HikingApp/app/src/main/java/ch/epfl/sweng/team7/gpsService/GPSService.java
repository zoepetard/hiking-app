package ch.epfl.sweng.team7.gpsService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class GPSService extends Service {

    private static final String LOG_FLAG = "GPS_Service";

    private final IBinder mBinder = new LocalBinder();

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
}
