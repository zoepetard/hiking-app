package ch.epfl.sweng.team7.mockServer;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Created by pablo on 8/11/15.
 */
@RunWith(AndroidJUnit4.class)
public class MockServerTest extends TestCase {
    public MockServer mMockServer;
    public List<Long> mHikeIds;
    public List<RawHikeData> listRawHikes;
    public RawHikeData mRawHikeData1, mRawHikeData2;
    public long mUserBortId;

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 1,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201, 1.0],\n"
            + "    [0.1, 0.1, 123202, 2.0],\n"
            + "    [0.2, 0.0, 123203, 3.0],\n"
            + "    [0.3,89.9, 123204, 4.0],\n"
            + "    [0.4, 0.0, 123205, 5.0]\n"
            + "  ]\n"
            + "}\n";

    @Before
    public void setUp() throws Exception {
        mMockServer = new MockServer();
        mHikeIds = new ArrayList<>();
        listRawHikes = new ArrayList<>();
        long id1 = 1;
        long id2 = 2;
        mHikeIds.add(id1);
        mHikeIds.add(id2);
        mRawHikeData1 = RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));
        mRawHikeData2 = RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));

        mUserBortId = mMockServer.postUserData(new RawUserData(-1, "bort", "bort@googlemail.com"));
    }

    @Test
    public void testSingleHikeCanBeFetched() throws Exception {
        long hikeId = 1;
        RawHikeData mRawHikeData = mMockServer.fetchSingleHike(1);
        assertEquals(mRawHikeData.getHikeId(), hikeId);
    }


    @Test
    public void testMultipleHikesCanBeFetched() throws Exception {
        List<Long> postHikeIds = new ArrayList<>();
        postHikeIds.add(mMockServer.postHike(mRawHikeData1));
        postHikeIds.add(mMockServer.postHike(mRawHikeData2));
        listRawHikes = mMockServer.fetchMultipleHikes(postHikeIds);
        long hike1 = listRawHikes.get(0).getHikeId();
        long hike2 = listRawHikes.get(1).getHikeId();
        assertEquals(new Long(hike1), postHikeIds.get(0));
        assertEquals(new Long(hike2), postHikeIds.get(1));
        assertTrue(!listRawHikes.isEmpty());

    }

    @Test
    public void testHikeCanBePosted() throws Exception {
        long hikeId = mMockServer.postHike(mRawHikeData1);
        assertEquals(mRawHikeData1.getHikeId(), hikeId);
        assertTrue(mMockServer.hasHike(mRawHikeData1.getHikeId()));
        assertEquals(mMockServer.getHike(hikeId).getHikeId(), mRawHikeData1.getHikeId());
    }

    @Test
    public void testFetchUserByMail() throws Exception {
        String mail = "bort@googlemail.com";
        RawUserData rawUserData = mMockServer.fetchUserData(mail);
        assertEquals("ID mismatch", rawUserData.getUserId(), mUserBortId);
        assertEquals("Wrong user name", rawUserData.getUserName(), "bort");
    }

    @Test
    public void testFetchUserById() throws Exception {
        long id = 1;
        RawUserData rawUserData = mMockServer.fetchUserData(mUserBortId);
        assertEquals("Wrong mail address", rawUserData.getMailAddress(), "bort@googlemail.com");
        assertEquals("Wrong user name", rawUserData.getUserName(), "bort");
    }

}
