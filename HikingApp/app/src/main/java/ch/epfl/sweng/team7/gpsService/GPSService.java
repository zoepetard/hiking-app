package ch.epfl.sweng.team7.gpsService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GPSService extends Service {

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access.
     */
    public class LocalBinder extends Binder {
        GPSService getService() {
            return GPSService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
