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
import ch.epfl.sweng.team7.network.RawUserData;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {
    private static final LatLng DEBUG_LOC_ACCRA = new LatLng(5.615986, -0.171533);
    private static final LatLng DEBUG_LOC_SAOTOME = new LatLng(0.362365, 6.558835);
    private long mNewHikeId, mNewHikeId2;

    @Before
    public void setUp() throws Exception {
        MockServer mockServer = new MockServer();
        List<RawHikePoint> newHikePoints = new ArrayList<>();
        newHikePoints.add(new RawHikePoint(new LatLng(2.,10.), new Date(), 0.0));
        newHikePoints.add(new RawHikePoint(new LatLng(2.,11.), new Date(), 0.0));
        RawHikeData newHike = new RawHikeData(2, 15, new Date(), newHikePoints);
        RawHikeData newHike2 = new RawHikeData(3, 15, new Date(), newHikePoints);
        mNewHikeId = mockServer.postHike(newHike);
        DataManager.setDatabaseClient(mockServer);
        mNewHikeId2 = DataManager.getInstance().postHike(newHike2);
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
        assertEquals(2, hikeDatas.size());
        assertEquals(mNewHikeId, hikeDatas.get(0).getHikeId());
    }

    @Test
    public void testPostHike() throws Exception{
        List<RawHikePoint> newHikePoints = new ArrayList<>();
        newHikePoints.add(new RawHikePoint(new LatLng(3.,12.), new Date(), 0.0));
        newHikePoints.add(new RawHikePoint(new LatLng(4., 13.), new Date(), 0.0));
        RawHikeData hike = new RawHikeData(11, 15, new Date(), newHikePoints);
        assertEquals(DataManager.getInstance().getHike(mNewHikeId2).getHikeId(), hike.getHikeId());

    }
    @Test
    public void testFailedToFetchUserData() throws DataManagerException {
        boolean exceptionIsThrown = false;

        try {
            DataManager dataManager = DataManager.getInstance();
            long unknownId = -1;
            dataManager.getUserData(unknownId);
        } catch (NullPointerException e) {
            exceptionIsThrown = true;
        }

        assertEquals("Exception wasn't thrown ", true, exceptionIsThrown);

    }


    @Test
    public void testFailedToPostUserData() throws Exception {
        boolean exceptionIsThrown = false;

        try {
            RawUserData rawUserData = new RawUserData(-3, "a", "gmail.com"); // bad data
            DataManager dataManager = DataManager.getInstance();
            dataManager.setUserData(rawUserData);
        } catch (IllegalArgumentException e) {
            exceptionIsThrown = true;
        }

        assertEquals("Exception wasn't thrown + ", true, exceptionIsThrown);


    }

    /* TODO add after server side is Implemented
    @Test
    public void testGetUserData() throws Exception {
    }

    @Test
    public void testChangeUserName() throws Exception {
    }

    @Test
    public void testSetUserData() throws Exception {
    }
    */

    @After
    public void tearDown() {
        DataManager.reset();
    }
}
