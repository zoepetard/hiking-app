package ch.epfl.sweng.team7.gpsService;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GPSManagerTest {

    private GPSManager gpsManager;

    @Before
    public void setup() {
        gpsManager = GPSManager.getInstance();
    }

    @Test(expected = NullPointerException.class)
    public void testNullPositionInitialization() {
        gpsManager.getCurrentCoords();
    }

    @Test(expected = NullPointerException.class)
    public void testNullLocationParameter() {
        gpsManager.updateCurrentLocation(null);
        gpsManager.getCurrentCoords();
    }
}
