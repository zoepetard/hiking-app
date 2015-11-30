package ch.epfl.sweng.team7.database;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.hikingapp.MapActivity;
import ch.epfl.sweng.team7.mockServer.MockServer;
import ch.epfl.sweng.team7.network.RawHikeComment;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;
import ch.epfl.sweng.team7.network.RawUserData;

import static java.lang.Thread.sleep;


/**
 * Tests the local cache for hikes
 */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest extends InstrumentationTestCase {
    private static final LatLng DEBUG_LOC_ACCRA = new LatLng(5.615986, -0.171533);
    private static final LatLng DEBUG_LOC_SAOTOME = new LatLng(0.362365, 6.558835);
    private long mNewHikeId, mNewHikeId2;
    private GPSManager gpsManager;

    @Rule
    public ActivityTestRule<MapActivity> mActivityRule = new ActivityTestRule<>(
            MapActivity.class);

    @Before
    public void setUp() throws Exception {
        MockServer mockServer = new MockServer();
        List<RawHikePoint> newHikePoints = new ArrayList<>();
        newHikePoints.add(new RawHikePoint(new LatLng(2., 10.), new Date(), 0.0));
        newHikePoints.add(new RawHikePoint(new LatLng(2., 11.), new Date(), 0.0));
        List<RawHikeComment> newHikeComments = new ArrayList<>();
        RawHikeData newHike = new RawHikeData(-1, 15, new Date(), newHikePoints, newHikeComments, "");
        RawHikeData newHike2 = new RawHikeData(-1, 15, new Date(), newHikePoints, newHikeComments, "");
        mNewHikeId = mockServer.postHike(newHike);
        DataManager.setDatabaseClient(mockServer);
        newHike2.setTitle("Hike2");
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
    public void testSearchHikes() throws DataManagerException {
        DataManager dataManager = DataManager.getInstance();
        List<HikeData> hikeDataList = dataManager.searchHike("Hike2");

        assertEquals("Hike not found", hikeDataList.get(0).getTitle(), "Hike2");
    }

    @Test
    public void testFailedToFetchUserData() throws DataManagerException {
        try {
            DataManager dataManager = DataManager.getInstance();
            long unknownId = -1;
            dataManager.getUserData(unknownId);
            fail("Unknown user ID didn't trigger exception.");
        } catch (DataManagerException e) {
            // pass
        }
    }


    @Test
    public void testFailedToPostUserData() throws Exception {
        boolean exceptionIsThrown = false;

        try {
            // TODO this test should be in RawUserDataTest
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
