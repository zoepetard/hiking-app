package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/** Tests whether the app correctly handles proper JSON */
@RunWith(AndroidJUnit4.class)
public class DefaultLocalCacheTest {

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 268,\n"
            + "  \"owner_id\": 153,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201],\n"
            + "    [0.1, 0.1, 123202],\n"
            + "    [0.2, 0.0, 123203],\n"
            + "    [0.3,89.9, 123204],\n"
            + "    [0.4, 0.0, 123205]\n"
            + "  ]\n"
            + "}\n";

    private static final long EXPECTED_HIKE_ID = 268L;
    private static final long EXPECTED_OWNER_ID = 153L;
    private static final long EXPECTED_DATE = 123201;
    private static final double EPS_DOUBLE = 1e-10;

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
