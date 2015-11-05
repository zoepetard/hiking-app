package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;


/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {

    @Test
    public void testDataManagerCanBeCreated() {
        DataManager dataManager = DataManager.getInstance();
        assertFalse("DataManager Not Created.", dataManager == null);
    }

    @Test
    public void testGetDebugHikeOne() throws LocalCacheException {
        //DataManager.configureDatabaseClient(new LocalDatabaseClient()); TODO waiting for issue #25
        final long hikeId = 1;  // ID 1 should always exist
        //HikeData hike = DataManager.getInstance().getHikeById(hikeId);
        //assertEquals("Hike ID did not match requested ID", hikeId, hike.getHikeId());
    }

    @After
    public void tearDown() {
        DataManager.reset();
    }
}
