package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.mockServer.MockServer;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {
    private static final LatLng DEBUG_LOC_ACCRA = new LatLng(5.615986, -0.171533);
    private static final LatLng DEBUG_LOC_SAOTOME = new LatLng(0.362365, 6.558835);

    @Before
    public void setUp() throws Exception {
        MockServer mockServer = new MockServer();
        List<RawHikePoint> newHikePoints = new ArrayList<>();
        newHikePoints.add(new RawHikePoint(new LatLng(2.,10.), new Date(), 0.0));
        newHikePoints.add(new RawHikePoint(new LatLng(2.,11.), new Date(), 0.0));
        RawHikeData newHike = new RawHikeData(2, 15, new Date(), newHikePoints);
        mockServer.postHike(newHike);
        DataManager.setDatabaseClient(mockServer);
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
        LatLngBounds window = new LatLngBounds(DEBUG_LOC_SAOTOME, DEBUG_LOC_ACCRA);
        List<HikeData> hikeDatas = DataManager.getInstance().getHikesInWindow(window);
        assertEquals(1, hikeDatas.size());
        assertEquals(2, hikeDatas.get(0).getHikeId());
    }

    @After
    public void tearDown() {
        DataManager.reset();
    }
}
