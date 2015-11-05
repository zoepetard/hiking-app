package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DefaultLocalCacheTest {

    @Test
    public void testDefaultLocalCacheCanBeCreated() {
        LocalCache lc = new DefaultLocalCache();
        assertFalse("Local Cache Not Created.", lc == null);
    }

    @Test
    public void testInsertHike() throws Exception {
        LocalCache lc = new DefaultLocalCache();
        lc.putHike(DummyHikeBuilder.buildDefaultHikeData(1));
        assertTrue(lc.hasHike(1));
    }

    @Test
    public void testInsertTwoHikes() throws Exception {
        LocalCache lc = new DefaultLocalCache();
        lc.putHike(DummyHikeBuilder.buildDefaultHikeData(1));
        lc.putHike(DummyHikeBuilder.buildDefaultHikeData(2));
        assertTrue("Hike 1 not inserted", lc.hasHike(1));
        // TODO uncomment after iss28: Fails because DefaultHikeData returns dummy values
        //assertTrue("Hike 2 not inserted", lc.hasHike(2));
    }

    @Test
    public void testCachedHikesCount() throws Exception {
        LocalCache lc = new DefaultLocalCache();
        assertEquals(0, lc.cachedHikesCount());
        lc.putHike(DummyHikeBuilder.buildDefaultHikeData(1));
        assertEquals(1, lc.cachedHikesCount());
        // TODO uncomment after iss28: Fails because DefaultHikeData returns dummy values
        //lc.addHike(DummyHikeBuilder.buildDefaultHikeData(2));
        //assertEquals(2, lc.cachedHikesCount());
    }
}