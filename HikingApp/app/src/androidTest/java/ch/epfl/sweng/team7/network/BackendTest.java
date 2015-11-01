package ch.epfl.sweng.team7.network;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.epfl.sweng.team7.database.TrackData;


/**
 * A {@link TestCase} implementation for JUnit that
 * tests communication with a SwEng quiz server.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackendTest extends TestCase {

    private static final String PROPER_JSON_ONETRACK = "{\n"
            + "  \"track_id\": 268,\n"
            + "  \"owner_id\": 153,\n"
            + "  \"date\": 123201,\n"
            + "  \"track_data\": [\n"
            + "    [0.0, 0.0, 123201],\n"
            + "    [0.1, 0.1, 123202],\n"
            + "    [0.2, 0.0, 123203],\n"
            + "    [0.3,89.9, 123204],\n"
            + "    [0.4, 0.0, 123205]\n"
            + "  ]\n"
            + "}\n";
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
     * Test the {@link NetworkDatabaseClient}
     */
    //@Test
    public void testGetTrack() throws Exception {
        long trackId = 1;

        // This test assumes that the server is online and returns good results.
        NetworkDatabaseClient dbClient = new NetworkDatabaseClient(
                SERVER_URL, new DefaultNetworkProvider());
        TrackData trackData = dbClient.fetchSingleTrack(trackId);
        assertEquals(trackId, trackData.getTrackId());
    }

    // TODO the remaining code is for QuizClient testing, and needs to be changed to test
    // TODO the behavior of the NetworkDatabaseClient.
    /**
     * Test the {@link NetworkQuizClient} for failure of URL parser
     */
    /*public void testGetRandomQuestion_BrokenURL() {

        // This test assumes that the server is online and returns good results.
        NetworkQuizClient quizClient = new NetworkQuizClient(
                "https//sweng-quiz.appspot.com/quizquestions/random",
                new DefaultNetworkProvider());
        try {
            quizClient.fetchRandomQuestion();
        }
        catch (QuizClientException e) {
            // A MalformedURLException is the expected outcome
            assertTrue("QuizClientException: " + e.getMessage(),
                    e.getMessage().equals("MalformedURLException"));
            return;
        }
        fail("Malformed URL threw no exception");
    }*/

    /**
     * Test the creation of QuizQuestion objects
     */
    /*public void testQuizQuestionCreate() throws JSONException {

        QuizQuestion quizQuestion = CreateQuizQuestion();

        assertEquals("quizQuestion.getID", quizQuestion.getID(), 17005);
        assertTrue("quizQuestion.getBody",
                quizQuestion.getBody().equals("What is the capital of Antigua and Barbuda?"));
        assertEquals("quizQuestion.getAnswers.size", quizQuestion.getAnswers().size(), 4);
        assertTrue("quizQuestion.getAnswers", quizQuestion.getAnswers().get(3).equals("Plymouth"));
        assertEquals("quizQuestion.getSolutionIndex", quizQuestion.getSolutionIndex(), 2);
        assertTrue("quizQuestion.getTags", quizQuestion.getTags().get(0).equals("capitals"));
        assertTrue("quizQuestion.getOwner", quizQuestion.getOwner().equals("sweng"));
    }*/

    /**
     * Create a valid QuizQuestion object
     * @return a QuizQuestion object
     */
    /*private static QuizQuestion CreateQuizQuestion() throws JSONException {
        JSONObject jsonObject = new JSONObject(createSampleInput());
        return QuizQuestion.parseFromJSON(jsonObject);
    }*/

    /**
     * Create the sample message from the forum in good JSON format
     * @return a sample string
     */
    /*private static String createSampleInput() {

        return "{ \"id\": 17005,"
                + "\"question\": \"What is the capital of Antigua and Barbuda?\","
                + "\"answers\": [ \"Chisinau\", \"Saipan\", \"St. John's\", \"Plymouth\" ],"
                + "\"solutionIndex\": 2, \"tags\": [ \"capitals\", \"geography\", \"countries\" ],"
                + "\"owner\": \"sweng\" }";
    }*/
}



