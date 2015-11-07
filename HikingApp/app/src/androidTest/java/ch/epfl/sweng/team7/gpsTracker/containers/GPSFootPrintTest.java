package ch.epfl.sweng.team7.gpsTracker.containers;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GPSFootPrintTest {

    @Test(expected = NullPointerException.class)
    public void testInitializationWithNullGeoCoords() {
        new GPSFootPrint(null, 0);
    }
}
