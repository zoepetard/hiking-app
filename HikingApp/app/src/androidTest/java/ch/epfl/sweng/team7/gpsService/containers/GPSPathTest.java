package ch.epfl.sweng.team7.gpsService.containers;

import static org.junit.Assert.*;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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

    @Test
    public void testFootPrintBegCut() {
        gpsPath = new GPSPath();
        GeoCoords coords = new GeoCoords(0, 0, 0);
        for (int i = 0; i < 4; i++) {
            gpsPath.addFootPrint(new GPSFootPrint(coords, 0));
        }
        gpsPath.removeFootPrintsBefore(2);
        assertEquals(gpsPath.getFootPrintCount(), 2);
    }

    @Test
    public void testFootPrintEndCut() {
        gpsPath = new GPSPath();
        GeoCoords coords = new GeoCoords(0, 0, 0);
        for (int i = 0; i < 4; i++) {
            gpsPath.addFootPrint(new GPSFootPrint(coords, 0));
        }
        gpsPath.removeFootPrintsAfter(2);
        assertEquals(gpsPath.getFootPrintCount(), 2);
    }

    @Test
    public void testCutBiggerThanSizeBefore() {
        gpsPath = new GPSPath();
        GeoCoords coords = new GeoCoords(0, 0, 0);
        for (int i = 0; i < 4; i++) {
            gpsPath.addFootPrint(new GPSFootPrint(coords, 0));
        }
        long sizeBefore = gpsPath.getFootPrintCount();
        gpsPath.removeFootPrintsBefore((int) sizeBefore + 1);
        long sizeAfter = gpsPath.getFootPrintCount();
        assertEquals(sizeAfter, sizeBefore);
    }

    @Test
    public void testCutBiggerThanSizeAfter() {
        gpsPath = new GPSPath();
        GeoCoords coords = new GeoCoords(0, 0, 0);
        for (int i = 0; i < 4; i++) {
            gpsPath.addFootPrint(new GPSFootPrint(coords, 0));
        }
        long sizeBefore = gpsPath.getFootPrintCount();
        gpsPath.removeFootPrintsAfter((int) sizeBefore + 1);
        long sizeAfter = gpsPath.getFootPrintCount();
        assertEquals(sizeAfter, sizeBefore);
    }

    @Test
    public void testDistanceToStart() {
        gpsPath = new GPSPath();
        GeoCoords coords1 = new GeoCoords(1, 1, 1);
        GeoCoords coords2 = new GeoCoords(2, 1, 1);
        gpsPath.addFootPrint(new GPSFootPrint(coords1, 0));
        gpsPath.addFootPrint(new GPSFootPrint(coords2, 0));
        assertEquals(gpsPath.distanceToStart(), 110575, 0.1f);
    }
}
