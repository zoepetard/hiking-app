package ch.epfl.sweng.team7.network;

import android.graphics.drawable.Drawable;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.database.DummyHikeBuilder;
import ch.epfl.sweng.team7.hikingapp.SignedInUser;


/**
 * Tests whether communication with the backend
 * server works. These tests may fail if the
 * backend server is not available.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackendTest extends TestCase {

    private static final double EPS_DOUBLE = 1e-10;
    public static final String SERVER_URL = "http://10.0.3.2:8080";//"http://footpath-1104.appspot.com";//
    DatabaseClient mDatabaseClient;

    @Before
    public void setUp() throws Exception {
        mDatabaseClient = createDatabaseClient();
    }

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
        connection.setConnectTimeout(2000);
        connection.connect();
        connection.disconnect();
    }

    /**
     * Test the {@link NetworkDatabaseClient} get_hike function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testGetHike() throws Exception {
        final long hikeId = getExistingHikeID(mDatabaseClient);
        RawHikeData hikeData = mDatabaseClient.fetchSingleHike(hikeId);
        assertEquals(hikeId, hikeData.getHikeId());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_hike function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostHike() throws Exception {
        RawHikeData hikeData = createHikeData();
        final long hikeId = mDatabaseClient.postHike(hikeData);
        assertTrue(hikeId > 0);

        waitForServerSync();
        mDatabaseClient.deleteHike(hikeId);
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_hike and get_hike functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndGetHike() throws Exception {
        RawHikeData hikeData = createHikeData();

        // post a hike
        final long hikeId = mDatabaseClient.postHike(hikeData);

        waitForServerSync();

        // retrieve the same hike
        RawHikeData serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(serverHikeData.getHikeId(), hikeId);
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

        waitForServerSync();
        mDatabaseClient.deleteHike(hikeId);
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_hike and get_hike functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndUpdateHike() throws Exception {
        RawHikeData hikeData = createHikeData();

        // post a hike
        final long hikeId = mDatabaseClient.postHike(hikeData);

        // Prepare a modified Hike
        List<RawHikePoint> newHikePoints = hikeData.getHikePoints();
        newHikePoints.remove(2);
        RawHikeData newHikeData = new RawHikeData(hikeId, hikeData.getOwnerId(), new Date(), newHikePoints);

        waitForServerSync();

        // post a modified hike with the same ID
        final long newHikeId = mDatabaseClient.postHike(newHikeData);
        assertEquals(newHikeId, hikeId);
        assertTrue(newHikeData.getHikePoints().size() != hikeData.getHikePoints().size());

        // retrieve the same hike
        RawHikeData serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(serverHikeData.getHikeId(), newHikeData.getHikeId());
        assertEquals(serverHikeData.getHikePoints().size(), newHikeData.getHikePoints().size());
        for(int i = 0; i < newHikeData.getHikePoints().size(); ++i) {
            assertEquals(newHikeData.getHikePoints().get(i).getPosition().latitude,
                    serverHikeData.getHikePoints().get(i).getPosition().latitude, EPS_DOUBLE);
            assertEquals(newHikeData.getHikePoints().get(i).getPosition().longitude,
                    serverHikeData.getHikePoints().get(i).getPosition().longitude, EPS_DOUBLE);
            assertEquals(newHikeData.getHikePoints().get(i).getTime(),
                    serverHikeData.getHikePoints().get(i).getTime());
        }

        waitForServerSync();
        mDatabaseClient.deleteHike(hikeId);
    }

    @Test
    public void testPopulateDatabase() throws Exception {
        //PopulateDatabase.run(createDatabaseClient());
    }

    @Test
    public void testGetHikesInWindow() throws Exception {
        waitForServerSync();
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90,-179), new LatLng(90,179));
        List<Long> hikeList = mDatabaseClient.getHikeIdsInWindow(bounds);

        assertTrue("No hikes found on server", hikeList.size() > 0);
        RawHikeData rawHikeData = mDatabaseClient.fetchSingleHike(hikeList.get(0));
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
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90,-180), new LatLng(-89,180));
        List<Long> hikeList = mDatabaseClient.getHikeIdsInWindow(bounds);
        assertEquals("Found Hike at South Pole", 0, hikeList.size());
    }

    @Test
    public void testGetHikesInWindow_InAtlantic() throws Exception {
        LatLngBounds bounds = new LatLngBounds(new LatLng(42.840628, -49.093879), new LatLng(55.971414, -18.178352));
        List<Long> hikeList = mDatabaseClient.getHikeIdsInWindow(bounds);
        assertEquals("Found Hike in the Atlantic.", 0, hikeList.size());
    }

    @Test
    public void testDeleteHike() throws Exception {
        RawHikeData rawHikeData = createHikeData();
        long hikeId = mDatabaseClient.postHike(rawHikeData);
        assertTrue("Server should set positive hike ID", hikeId >= 0);

        waitForServerSync();
        mDatabaseClient.deleteHike(hikeId);

        waitForServerSync();
        try {
            mDatabaseClient.fetchSingleHike(hikeId);
            fail("Found hike in the database after deleting it.");
        } catch (DatabaseClientException e) {
            // pass
        }
    }

    @Test
    public void testPostUserData() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID", userId >= 0);

        waitForServerSync();
        mDatabaseClient.deleteUser(userId);
    }

    @Test
    public void testGetUserData() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID", userId >= 0);

        waitForServerSync();
        RawUserData serverRawUserData = mDatabaseClient.fetchUserData(userId);

        assertEquals(userId, serverRawUserData.getUserId());
        assertEquals(rawUserData.getMailAddress(), serverRawUserData.getMailAddress());
        assertEquals(rawUserData.getUserName(), serverRawUserData.getUserName());

        waitForServerSync();
        mDatabaseClient.deleteUser(userId);
    }

    @Test
    public void testDeleteUser() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID", userId >= 0);

        waitForServerSync();
        mDatabaseClient.deleteUser(userId);

        waitForServerSync();
        try {
            mDatabaseClient.fetchUserData(userId);
            fail("Found user in the database after deleting him.");
        } catch (DatabaseClientException e) {
            // pass
        }
    }

    @Test
    public void testGetHikeIdsOfUser() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);

        waitForServerSync();
        RawHikeData hikeData = new RawHikeData(-1, userId, new Date(), createHikeData().getHikePoints());
        final long hikeId = mDatabaseClient.postHike(hikeData);

        waitForServerSync();
        List<Long> hikeList = ((NetworkDatabaseClient) mDatabaseClient).getHikeIdsOfUser(userId);
        assertEquals(hikeList.get(0), Long.valueOf(hikeId));
    }

    @Test
    public void testLoginUser() throws Exception {

        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID", userId >= 0);

        SignedInUser signedInUser = SignedInUser.getInstance();
        signedInUser.init(-1, "Name Unset", rawUserData.getMailAddress());

        waitForServerSync();

        mDatabaseClient.loginUser();

        waitForServerSync();
        mDatabaseClient.deleteUser(userId);

        assertEquals(userId, signedInUser.getId());
        assertEquals(rawUserData.getMailAddress(), signedInUser.getMailAddress());
        assertEquals(rawUserData.getUserName(), signedInUser.getUserName());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_image and get_image functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndGetImage() throws Exception {
        Drawable initialImage = loadDebugImage();

        // post a hike
        final long imageId = mDatabaseClient.postImage(initialImage);

        waitForServerSync();

        // retrieve the same hike
        Drawable serverImage = mDatabaseClient.getImage(imageId);

        assertEquals(serverImage.getBounds().left, initialImage.getBounds().left);
        assertEquals(serverImage.getBounds().right, initialImage.getBounds().right);
        assertEquals(serverImage.getBounds().top, initialImage.getBounds().top);
        assertEquals(serverImage.getBounds().bottom, initialImage.getBounds().bottom);

        waitForServerSync();
        mDatabaseClient.deleteImage(imageId);
    }

    // TODO(simon) test backend reaction to malformed input


    /**
     * Create a valid HikeData object
     * @return a HikeData object
     */
    private static RawHikeData createHikeData() throws HikeParseException {
        return DummyHikeBuilder.buildRawHikeData(RawHikeData.HIKE_ID_UNKNOWN);
    }

    /**
     * Create a valid RawUserData object
     * @return a RawUserData object
     */
    private static RawUserData createUserData() throws HikeParseException {
        return new RawUserData(-1, "Bort", "bort@googlemail.com");
    }

    /**
     * Create a valid DatabaseClient object
     * @return a DatabaseClient object
     */
    private static DatabaseClient createDatabaseClient() throws DatabaseClientException {
        return new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
    }

    /**
     * Get the ID of some valid hike from the server
     */
    private static long getExistingHikeID(DatabaseClient dbClient) throws DatabaseClientException {
        waitForServerSync();
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90,-179), new LatLng(90,179));
        List<Long> hikeList = dbClient.getHikeIdsInWindow(bounds);
        assertTrue("No hikes found on server", hikeList.size() > 0);
        return hikeList.get(0);
    }

    // TODO(simon) change: temporary: download some picture from the internet
    private static Drawable loadDebugImage() throws Exception {
        URL url = new URL("http://quarknet.de/fotos/landschaft/himmel/engelsfluegel.jpg");
        URLConnection ucon = url.openConnection();
        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        return Drawable.createFromStream(bis, "");
    }

    /**
     * Wait a short time to make sure the server database is in sync.
     */
    private static void waitForServerSync() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //pass
        }
    }
}



