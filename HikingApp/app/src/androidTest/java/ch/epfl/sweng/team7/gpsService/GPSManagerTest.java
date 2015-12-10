package ch.epfl.sweng.team7.gpsService;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GPSManagerTest {

    private GPSManager gpsManager;

    @Before
    public void setup() {
        gpsManager = GPSManager.getInstance();
    }

    @Test
    public void testEqualityAfterBeforeNullArg() {
        assertTrue(locationUpdate());
        GeoCoords prevLocation = gpsManager.getCurrentCoords();
        gpsManager.updateCurrentLocation(null);
        GeoCoords afterLocation = gpsManager.getCurrentCoords();
        assertEquals(prevLocation, afterLocation);
    }

    @Test
    public void testBooleanValues() {
        if (!gpsManager.enabled()) {
            assertEquals(gpsManager.paused(), false);
            assertEquals(gpsManager.tracking(), false);
        }
    }

    private boolean locationUpdate() {
        Location newLocation = new Location("");
        newLocation.setLatitude(10);
        newLocation.setLongitude(10);
        gpsManager.updateCurrentLocation(newLocation);
        return gpsManager.getCurrentCoords().toLatLng().equals(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
    }
}
