package ch.epfl.sweng.team7.gpsService.containers;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GPSFootPrintTest {

    @Test(expected = NullPointerException.class)
    public void testInitializationWithNullGeoCoords() {
        new GPSFootPrint(null, 0);
    }

    @Test
    public void testInitializationEquality() {
        GeoCoords newCoords = new GeoCoords(10, 0, 0);
        GPSFootPrint footPrint = new GPSFootPrint(newCoords, 0);
        assertEquals(footPrint.getGeoCoords(), newCoords);
    }
}
