package ch.epfl.sweng.team7.network;

import android.graphics.drawable.Drawable;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.authentication.LoginRequest;
import ch.epfl.sweng.team7.authentication.SignedInUser;
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
    private static final String SERVER_URL = "https://footpath-1104.appspot.com";//"http://10.0.3.2:8080";//
    private DatabaseClient mDatabaseClient;

    @Before
    public void setUp() throws Exception {
        mDatabaseClient = createDatabaseClient();
        mDatabaseClient.loginUser(new LoginRequest("bort@googlemail.com", "Bort", ""));
    }

    /**
     * Test the {@link DefaultNetworkProvider}
     */
    @Test
    public void testCanOpenNetwork() throws IOException {

        // Create a URL
        URL url = new URL("https://www.epfl.ch");

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
        List<RawHikeComment> newHikeComments = new ArrayList<>();
        RawHikeData newHikeData = new RawHikeData(hikeId, hikeData.getOwnerId(), new Date(), newHikePoints, newHikeComments, "", null);
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
        RawHikeData hikeData = createHikeData();

        // post a hike
        final long hikeId = mDatabaseClient.postHike(hikeData);
        waitForServerSync();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//new LatLng(-90,-179), new LatLng(90,179));
        for(RawHikePoint p : hikeData.getHikePoints()) {
            boundsBuilder.include(p.getPosition());
        }
        LatLngBounds bounds = boundsBuilder.build();

        List<Long> hikeList = mDatabaseClient.getHikeIdsInWindow(bounds);
        mDatabaseClient.deleteHike(hikeId);

        assertTrue("No hikes found on server", hikeList.size() > 0);
        assertTrue(new ArrayList<>(hikeList).contains(hikeId));
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
        assertTrue("Server should set positive user ID, is "+userId, userId > 0);

        waitForServerSync();
        mDatabaseClient.deleteUser(userId);
        waitForServerSync();
    }

    @Test
    public void testGetUserData() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID, is "+userId, userId > 0);

        waitForServerSync();
        RawUserData serverRawUserData = mDatabaseClient.fetchUserData(userId);

        assertEquals(userId, serverRawUserData.getUserId());
        assertEquals(rawUserData.getMailAddress(), serverRawUserData.getMailAddress());
        assertEquals(rawUserData.getUserName(), serverRawUserData.getUserName());
    }

    @Test
    public void testDeleteUser() throws Exception {
        RawUserData rawUserData = createUserData();
        long userId = mDatabaseClient.postUserData(rawUserData);
        assertTrue("Server should set positive user ID, is "+userId, userId > 0);

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
        Long userId = SignedInUser.getInstance().getId();
        List<RawHikeComment> newHikeComments = new ArrayList<>();
        RawHikeData hikeData = new RawHikeData(-1, userId, new Date(), createHikeData().getHikePoints(), newHikeComments, "", null);

        final long hikeId = mDatabaseClient.postHike(hikeData);

        waitForServerSync();
        List<Long> hikeList = mDatabaseClient.getHikeIdsOfUser(userId);
        assertEquals(hikeList.get(0), Long.valueOf(hikeId));
    }

    @Test
    public void testLoginUser() throws Exception {
        SignedInUser signedInUser = SignedInUser.getInstance();
        assertTrue("User not logged in", signedInUser.getLoggedIn());
        assertTrue("User ID not set", signedInUser.getId() > 0);
        RawUserData rawUserData = mDatabaseClient.fetchUserData(signedInUser.getId());

        assertEquals(rawUserData.getUserId(), signedInUser.getId());
        assertEquals(rawUserData.getMailAddress(), signedInUser.getMailAddress());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_image and get_image functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndGetImage() throws Exception {
        Drawable initialImage = loadDebugImage();

        // post an image
        final long imageId = mDatabaseClient.postImage(initialImage);

        waitForServerSync();

        // retrieve the same image
        Drawable serverImage = mDatabaseClient.getImage(imageId);

        assertEquals(serverImage.getBounds().left, initialImage.getBounds().left);
        assertEquals(serverImage.getBounds().right, initialImage.getBounds().right);
        assertEquals(serverImage.getBounds().top, initialImage.getBounds().top);
        assertEquals(serverImage.getBounds().bottom, initialImage.getBounds().bottom);

        waitForServerSync();
        mDatabaseClient.deleteImage(imageId);
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_image function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testUpdateImage() throws Exception {
        Drawable initialImage = loadDebugImage();

        // post an image
        final long imageId = mDatabaseClient.postImage(initialImage);

        waitForServerSync();

        // retrieve the same image
        final long secondImageId = mDatabaseClient.postImage(initialImage, imageId);

        assertEquals(imageId, secondImageId);

        waitForServerSync();
        mDatabaseClient.deleteImage(imageId);
    }

    /**
     * Test the {@link NetworkDatabaseClient} delete_image function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testDeleteImage() throws Exception {
        Drawable initialImage = loadDebugImage();

        // post an image
        final long imageId = mDatabaseClient.postImage(initialImage);

        waitForServerSync();
        mDatabaseClient.deleteImage(imageId);

        waitForServerSync();
        try {
            mDatabaseClient.getImage(imageId);
            fail("Image found in database after deleting it");
        } catch (DatabaseClientException e) {
            // pass
        }
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_comment function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostComment() throws Exception {
        RawHikeData hikeData = createHikeData();
        final long hikeId = mDatabaseClient.postHike(hikeData);
        assertTrue(hikeId > 0);

        waitForServerSync();

        RawHikeComment hikeComment = new RawHikeComment(RawHikeComment.COMMENT_ID_UNKNOWN,
                hikeId, SignedInUser.getInstance().getId(), "test comment");
        final long commentId = mDatabaseClient.postComment(hikeComment);
        assertTrue(commentId > 0);

        waitForServerSync();
        RawHikeData serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);
        mDatabaseClient.deleteHike(hikeId);

        waitForServerSync();
        List<RawHikeComment> comments = serverHikeData.getAllComments();
        assertEquals(1, comments.size());
        assertEquals("test comment", comments.get(0).getCommentText());
        mDatabaseClient.deleteComment(commentId);
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_comment function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testDeleteComment() throws Exception {
        RawHikeData hikeData = createHikeData();
        final long hikeId = mDatabaseClient.postHike(hikeData);
        assertTrue(hikeId > 0);

        waitForServerSync();
        RawHikeComment hikeComment = new RawHikeComment(RawHikeComment.COMMENT_ID_UNKNOWN,
                hikeId, SignedInUser.getInstance().getId(), "test comment");
        final long commentId = mDatabaseClient.postComment(hikeComment);
        assertTrue(commentId > 0);

        mDatabaseClient.deleteComment(commentId);

        waitForServerSync();
        RawHikeData serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        List<RawHikeComment> comments = serverHikeData.getAllComments();
        assertEquals(comments.size(), 0);
        mDatabaseClient.deleteHike(hikeId);
    }

    // TODO(simon) test backend reaction to malformed input


    /**
     * Test the {@link NetworkDatabaseClient} post_vote function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostVote() throws Exception {
        RawHikeData hikeData = createHikeData();

        // post a hike
        final long hikeId = mDatabaseClient.postHike(hikeData);

        waitForServerSync();

        // retrieve the same hike
        RawHikeData serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(0, serverHikeData.getRating().getVoteCount());
        assertFalse(serverHikeData.getRating().userHasVoted());

        mDatabaseClient.postVote(new RatingVote(hikeId, 2));

        waitForServerSync();

        // retrieve the same hike
        serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(1, serverHikeData.getRating().getVoteCount());
        assertEquals(2, serverHikeData.getRating().getDisplayRating(), EPS_DOUBLE);
        assertTrue(serverHikeData.getRating().userHasVoted());

        mDatabaseClient.postVote(new RatingVote(hikeId, 5));

        waitForServerSync();

        // retrieve the same hike
        serverHikeData = mDatabaseClient.fetchSingleHike(hikeId);

        // Compare
        assertEquals(1, serverHikeData.getRating().getVoteCount());
        assertEquals(5, serverHikeData.getRating().getDisplayRating(), EPS_DOUBLE);
        assertTrue(serverHikeData.getRating().userHasVoted());

        mDatabaseClient.deleteHike(hikeId);
    }

    @Test
    public void testSearchHikes() throws Exception {
        // Creates a hike with title "test"
        RawHikeData hikeData = createHikeData();

        // post a hike
        final long hikeId = mDatabaseClient.postHike(hikeData);


        waitForServerSync();
        List<Long> hikeIdsWithTest = mDatabaseClient.getHikeIdsWithKeywords("test plop");
        List<Long> hikeIdsWithoutTest = mDatabaseClient.getHikeIdsWithKeywords("quetzacuatl blobby");

        assertTrue(new ArrayList<>(hikeIdsWithTest).contains(hikeId));
        assertFalse(new ArrayList<>(hikeIdsWithoutTest).contains(hikeId));

        mDatabaseClient.deleteHike(hikeId);
    }

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
    private static RawUserData createUserData() {
        return new RawUserData(SignedInUser.getInstance().getId(), "Bort", "bort@googlemail.com");
    }

    /**
     * Create a valid DatabaseClient object
     * @return a DatabaseClient object
     */
    private static DatabaseClient createDatabaseClient() throws Exception {
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
        URL url = new URL("http://www.vidipedija.com/images/thumb/a/a5/Android-logo.jpg/120px-Android-logo.jpg");
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



