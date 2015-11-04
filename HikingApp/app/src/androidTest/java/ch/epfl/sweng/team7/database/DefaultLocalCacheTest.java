package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/** Tests whether the app correctly handles proper JSON */
@RunWith(AndroidJUnit4.class)
public class DefaultLocalCacheTest {

    /** test that */
    @Test
    public void testDefaultLocalCacheCanBeCreated() throws LocalCacheException {
        LocalCacheProvider.configureReset();
        LocalCache lc = LocalCacheProvider.getLocalCache();
        assertFalse("Local Cache Not Created.", lc == null);
    }

    /** test that hike is correctly returned */
    @Test
    public void testGetDebugHikeOne() throws LocalCacheException {
        LocalCacheProvider.configureReset();
        //LocalCacheProvider.configureDatabaseClient(new LocalDatabaseClient()); TODO waiting for issue #25
        final long hikeId = 1;  // ID 1 should always exist
        HikeData hike = LocalCacheProvider.getLocalCache().getHikeById(hikeId);
        assertEquals("Hike ID did not match requested ID", hikeId, hike.getHikeId());
    }
}
