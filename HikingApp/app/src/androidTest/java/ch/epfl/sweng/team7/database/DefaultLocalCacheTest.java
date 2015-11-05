package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;

/** Tests the local cache for hikes */
@RunWith(AndroidJUnit4.class)
public class DefaultLocalCacheTest {

    @Test
    public void testDefaultLocalCacheCanBeCreated() throws LocalCacheException {
        LocalCache lc = new DefaultLocalCache();
        assertFalse("Local Cache Not Created.", lc == null);
    }
}