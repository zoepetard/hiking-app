package ch.epfl.sweng.team7.network;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.epfl.sweng.team7.database.TrackData;


/**
 * Tests whether communication with the backend
 * server works. These tests may fail if the
 * backend server is not available.
 */
@RunWith(AndroidJUnit4.class)
public class BackendTest extends TestCase {

    private static final String PROPER_JSON_ONETRACK = "{\n"
            + "  \"track_id\": 143,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"track_data\": [\n"
            + "    [0.0, 0.0, 123201],\n"
            + "    [0.1, 0.1, 123202],\n"
            + "    [0.2, 0.0, 123203],\n"
            + "    [0.3,89.9, 123204],\n"
            + "    [0.4, 0.0, 123205]\n"
            + "  ]\n"
            + "}\n";
    private static final double EPS_DOUBLE = 1e-10;
    public static final String SERVER_URL = "http://10.0.3.2:8080";

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
     * Test the {@link NetworkDatabaseClient} get_track function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testGetTrack() throws Exception {
        final long trackId = 1;
        DatabaseClient dbClient = createDatabaseClient();
        TrackData trackData = dbClient.fetchSingleTrack(trackId);
        assertEquals(trackId, trackData.getTrackId());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_track function
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostTrack() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        TrackData trackData = createTrackData();
        trackData.setTrackId(2);
        final long trackId = dbClient.postTrack(trackData);
        assertEquals(trackId, trackData.getTrackId());
    }

    /**
     * Test the {@link NetworkDatabaseClient} post_track and get_track functions
     * This test assumes that the server is online and returns good results.
     */
    @Test
    public void testPostAndGetTrack() throws Exception {
        DatabaseClient dbClient = createDatabaseClient();
        TrackData trackData = createTrackData();

        // post a track
        trackData.setTrackId(3);
        final long trackId = dbClient.postTrack(trackData);
        assertEquals(trackId, trackData.getTrackId());

        Thread.sleep(1000);

        // retrieve the same track
        TrackData serverTrackData = dbClient.fetchSingleTrack(trackId);

        // Compare
        assertEquals(serverTrackData.getOwnerId(), trackData.getOwnerId());
        assertEquals(serverTrackData.getDate(), trackData.getDate());
        assertEquals(serverTrackData.getTrackPoints().size(), trackData.getTrackPoints().size());
        for(int i = 0; i < trackData.getTrackPoints().size(); ++i) {
            assertEquals(trackData.getTrackPoints().get(i).getPosition().latitude,
                    serverTrackData.getTrackPoints().get(i).getPosition().latitude, EPS_DOUBLE);
            assertEquals(trackData.getTrackPoints().get(i).getPosition().longitude,
                    serverTrackData.getTrackPoints().get(i).getPosition().longitude, EPS_DOUBLE);
            assertEquals(trackData.getTrackPoints().get(i).getTime(),
                    serverTrackData.getTrackPoints().get(i).getTime());
        }
    }

    // TODO test backend reaction to malformed input
    // TODO test other backend interface (like post_tracks)

    /**
     * Create a valid TrackData object
     * @return a TrackData object
     */
    private static TrackData createTrackData() throws JSONException {
        return TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
    }

    /**
     * Create a valid DatabaseClient object
     * @return a DatabaseClient object
     */
    private static DatabaseClient createDatabaseClient() throws DatabaseClientException {
        return new NetworkDatabaseClient(SERVER_URL, new DefaultNetworkProvider());
    }
}



