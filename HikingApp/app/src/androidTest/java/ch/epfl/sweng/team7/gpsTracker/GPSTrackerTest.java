package ch.epfl.sweng.team7.gpsTracker;

import static org.junit.Assert.*;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.team7.gpsTracker.containers.coordinates.GeoCoords;

@RunWith(AndroidJUnit4.class)
public class GPSTrackerTest {

    private GPSTracker gpsTracker;

    @Before
    public void setup() {
        gpsTracker = new GPSTracker();
    }

    @Test(expected = NullPointerException.class)
    public void testNullPositionInitialization() {
        gpsTracker.getCurrentCoords();
    }

    @Test(expected = NullPointerException.class)
    public void testNullLocationParameter() {
        gpsTracker.updateCurrentLocation(null);
        gpsTracker.getCurrentCoords();
    }
}
