package ch.epfl.sweng.team7.gpsTracker;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
