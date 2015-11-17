package ch.epfl.sweng.team7.network;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ch.epfl.sweng.team7.database.DummyHikeBuilder;


/**
 * Tests whether communication with the backend
 * server works. These tests may fail if the
 * backend server is not available.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackendTest extends TestCase {

    private static final double EPS_DOUBLE = 1e-10;
    public static final String SERVER_URL = "http://footpath-1104.appspot.com";//"http://10.0.3.2:8080";//

    /**
     * Test the {@link DefaultNetworkProvider}
     */
    @Test
    public void testCanOpenNetwork() throws IOException {

        // Create a URL
        URL url = new URL("http://www.epfl.ch");

        // Get a DefaultNetworkProvider connection
        DefaultNetworkProvider networkProvider = new DefaultNetworkProvider();
        HttpURLConnection connection = networkProvider.getConnection(url);
        connection.connect();
        connection.disconnect();
    }

    /**
     * Test the {@link NetworkDatabaseClient} get_hike function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testGetHike() throws Exception {
        final long hikeId = 1;
        DatabaseClient dbClient = createDatabaseClient();
        RawHikeData hikeData = dbClient.fetchSingleHike(hikeId);
        assertEquals(hikeId, hikeData.getHikeId());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_hike function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostHike() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        RawHikeData hikeData = createHikeData();
        hikeData.setHikeId(2);
        final long hikeId = dbClient.postHike(hikeData);
        assertEquals(hikeId, hikeData.getHikeId());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_hike and get_hike functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndGetHike() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        RawHikeData hikeData = createHikeData();

        // post a hike
        hikeData.setHikeId(3);
        final long hikeId = dbClient.postHike(hikeData);
        assertEquals(hikeId, hikeData.getHikeId());

        Thread.sleep(1000);

        // retrieve the same hike
        RawHikeData serverHikeData = dbClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(serverHikeData.getOwnerId(), hikeData.getOwnerId());
        assertEquals(serverHikeData.getDate(), hikeData.getDate());
        assertEquals(serverHikeData.getHikePoints().size(), hikeData.getHikePoints().size());
        for(int i = 0; i < hikeData.getHikePoints().size(); ++i) {
            assertEquals(hikeData.getHikePoints().get(i).getPosition().latitude,
                    serverHikeData.getHikePoints().get(i).getPosition().latitude, EPS_DOUBLE);
            assertEquals(hikeData.getHikePoints().get(i).getPosition().longitude,
                    serverHikeData.getHikePoints().get(i).getPosition().longitude, EPS_DOUBLE);
            assertEquals(hikeData.getHikePoints().get(i).getTime(),
                    serverHikeData.getHikePoints().get(i).getTime());
        }
    }

    @Test
    public void testPopulateDatabase() throws Exception {
        //PopulateDatabase.run(createDatabaseClient());
    }

    @Test
    public void testGetHikesInWindow() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90,-179), new LatLng(90,179));
        List<Long> hikeList = dbClient.getHikeIdsInWindow(bounds);

        assertTrue("No hikes found on server", hikeList.size() > 0);
        RawHikeData rawHikeData = dbClient.fetchSingleHike(hikeList.get(0));
        boolean onePointInBox = false;
        for(RawHikePoint p : rawHikeData.getHikePoints()) {
            if(bounds.contains(p.getPosition())) {
                onePointInBox = true;
                break;
            }
        }
        assertTrue("Returned hike has no point in window", onePointInBox);

        Log.d("BackendTestLog", "Found " + hikeList.size() + " Hikes");
        for(Long l : hikeList) {
            Log.d("BackendTestLog", "Found Hike "+l);
        }
    }

    @Test
    public void testGetHikesInWindow_AtSouthPole() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90,-180), new LatLng(-89,180));
        List<Long> hikeList = dbClient.getHikeIdsInWindow(bounds);
        assertEquals("Found Hike at South Pole", 0, hikeList.size());
    }

    @Test
    public void testGetHikesInWindow_InAtlantic() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        LatLngBounds bounds = new LatLngBounds(new LatLng(42.840628, -49.093879), new LatLng(55.971414, -18.178352));
        List<Long> hikeList = dbClient.getHikeIdsInWindow(bounds);
        assertEquals("Found Hike in the Atlantic.", 0, hikeList.size());
    }

    // TODO test backend reaction to malformed input
    // TODO test other backend interface (like post_hikes)

    /**
     * Create a valid HikeData object
     * @return a HikeData object
     */
    private static RawHikeData createHikeData() throws HikeParseException {
        return DummyHikeBuilder.buildRawHikeData(1);
    }

    /**
     * Create a valid DatabaseClient object
     * @return a DatabaseClient object
     */
    private static DatabaseClient createDatabaseClient() throws DatabaseClientException {
        return new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
    }
}


