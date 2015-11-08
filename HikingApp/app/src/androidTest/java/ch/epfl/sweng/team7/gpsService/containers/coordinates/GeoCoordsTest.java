package ch.epfl.sweng.team7.gpsService.containers.coordinates;

import static org.junit.Assert.*;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GeoCoordsTest {

    @Test(expected = NullPointerException.class)
    public void testInstantiationFromNullLocation() {
        GeoCoords.fromLocation(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiationFromNullLatLng() {
        new GeoCoords(null, 0);
    }

    @Test
    public void testConversions() {
        GeoCoords coords = new GeoCoords(2, 3, 4);
        LatLng latLng = new LatLng(2, 3);
        assertEquals(new GeoCoords(latLng, 4), coords);
        assertEquals(latLng, coords.toLatLng());
    }
}
