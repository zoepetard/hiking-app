package ch.epfl.sweng.team7.gpsService.containers;

import static org.junit.Assert.*;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

@RunWith(AndroidJUnit4.class)
public class GPSPathTest {

    private GPSPath gpsPath;

    @Test
    public void testSizeOnEmptyAndAfterAddPath() {
        gpsPath = new GPSPath();
        assertEquals(0, gpsPath.getFootPrintCount());
        gpsPath.addFootPrint(new GPSFootPrint(new GeoCoords(0, 0, 0), 0));
        assertEquals(1, gpsPath.getFootPrintCount());
    }

    @Test
    public void testSizeAfterNullAdd() {
        gpsPath = new GPSPath();
        gpsPath.addFootPrint(null);
        assertEquals(0, gpsPath.getFootPrintCount());
    }
}