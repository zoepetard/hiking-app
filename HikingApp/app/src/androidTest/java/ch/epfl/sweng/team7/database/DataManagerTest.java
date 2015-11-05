package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {
    private static final LatLng DEBUG_LOC_ACCRA = new LatLng(5.615986, -0.171533);
    private static final LatLng DEBUG_LOC_SAOTOME = new LatLng(0.362365, 6.558835);

    @Before
    public void setUp() {
        // TODO waiting for issue #25
        //DataManager.setDatabaseClient(new MockDatabaseClient());
    }

    @Test
    public void testDataManagerCanBeCreated() {
        DataManager dataManager = DataManager.getInstance();
        assertFalse("DataManager Not Created.", dataManager == null);
    }

    @Test
    public void testGetDebugHikeOne() throws Exception {
        final long hikeId = 1;  // ID 1 should always exist
        HikeData hike = DataManager.getInstance().getHike(hikeId);
        assertEquals("Hike ID did not match requested ID", hikeId, hike.getHikeId());
    }

    @Test
    public void testGetHikesInWindow() throws Exception {
        // TODO waiting for issue #25
        //DataManager.getInstance().getHikesInWindow(new LatLngBounds(DEBUG_LOC_ACCRA, DEBUG_LOC_SAOTOME));
    }

    @After
    public void tearDown() {
        DataManager.reset();
    }
}
