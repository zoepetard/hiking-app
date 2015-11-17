package ch.epfl.sweng.team7.gpsService;

import static org.junit.Assert.*;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.team7.hikingapp.MapActivity;

public class GPSServiceTest {

    @Rule
    public ActivityTestRule<MapActivity> mActivityRule = new ActivityTestRule<>(
            MapActivity.class);

    @Test
    public void testServiceUnbindOnActivityChange() {
        assertTrue(GPSService.isBound);
        //TODO change to other activity
        assertFalse(GPSService.isBound);
    }
}
