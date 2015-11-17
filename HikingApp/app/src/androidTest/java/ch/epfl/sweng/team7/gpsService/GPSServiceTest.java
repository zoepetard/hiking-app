package ch.epfl.sweng.team7.gpsService;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import ch.epfl.sweng.team7.hikingapp.MapActivity;

public class GPSServiceTest extends ActivityInstrumentationTestCase2<MapActivity> {

    public GPSServiceTest() {
        super(MapActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testServiceUnbindOnActivityChange() {
        getActivity();
        assertTrue(GPSService.isBound);
        //TODO change to other activity
        assertFalse(GPSService.isBound);
    }
}
