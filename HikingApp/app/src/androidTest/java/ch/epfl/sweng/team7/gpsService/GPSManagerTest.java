package ch.epfl.sweng.team7.gpsService;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testInitialValues() {
        assertEquals(gpsManager.paused(), false);
        assertEquals(gpsManager.tracking(), false);
        assertEquals(gpsManager.enabled(), false);
    }

    @Test
    public void testLocationUpdate() {
        Location newLocation = new Location("");
        newLocation.setLatitude(10);
        newLocation.setLongitude(10);
        gpsManager.updateCurrentLocation(newLocation);
        assertEquals(gpsManager.getCurrentCoords().toLatLng(), new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
    }
}
