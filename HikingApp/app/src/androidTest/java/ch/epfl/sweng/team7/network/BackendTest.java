package ch.epfl.sweng.team7.network;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A {@link TestCase} implementation for JUnit that
 * tests communication with a SwEng quiz server.
 */
public class BackendTest extends TestCase {

    /**
     * Test the {@link DefaultNetworkProvider}
     */
    public void testCanOpenNetwork() throws IOException {

        // Create a URL
        URL url = new URL("http://www.epfl.ch");

        // Get a DefaultNetworkProvider connection
        DefaultNetworkProvider networkProvider = new DefaultNetworkProvider();
        HttpURLConnection connection = networkProvider.getConnection(url);
        connection.connect();
        connection.disconnect();
    }

    // TODO the remaining code is for QuizClient testing, and needs to be changed to test
    // TODO the behavior of the NetworkDatabaseClient.
    /**
     * Test the {@link NetworkDatabaseClient}
     */
    /*public void testGetRandomQuestion() throws DatabaseClientException {

        // This test assumes that the server is online and returns good results.
        NetworkDatabaseClient dbClient = new NetworkDatabaseClient(
                "https://sweng-quiz.appspot.com/quizquestions/random",
                new DefaultNetworkProvider());
        TrackData trackData = dbClient.fetchSingleTrack(0);
        assertEquals(1, trackData.getSolutionIndex());
    }*/

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



