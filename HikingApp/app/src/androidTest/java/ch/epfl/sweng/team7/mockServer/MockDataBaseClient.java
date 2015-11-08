package ch.epfl.sweng.team7.mockServer;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.network.RawHikeData;

/**
 * Created by pablo on 8/11/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MockDataBaseClient extends TestCase {
    public MockServer mMockServer;
    public List<Long> mHikeIds;
    public List<RawHikeData> listRawHikes;
    public RawHikeData mRawHikeData;

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 143,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201],\n"
            + "    [0.1, 0.1, 123202],\n"
            + "    [0.2, 0.0, 123203],\n"
            + "    [0.3,89.9, 123204],\n"
            + "    [0.4, 0.0, 123205]\n"
            + "  ]\n"
            + "}\n";

    @Before
    public void setUp() throws Exception{
        mMockServer = new MockServer();
        mHikeIds = new ArrayList<>();
        listRawHikes = new ArrayList<>();
        long id1 = 1;
        long id2 = 2;
        mHikeIds.add(id1);
        mHikeIds.add(id2);
        mRawHikeData = RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));

    }

    @Test
    public void aSingleHikeCanBeFetched() throws Exception{
        long hikeId = 1;
        RawHikeData mRawHikeData = mMockServer.fetchSingleHike(1);
        assertEquals(mRawHikeData.getHikeId(), hikeId);
    }



    @Test
    public void multipleHikesCanBeFetched() throws Exception{
        listRawHikes = mMockServer.fetchMultipleHikes(mHikeIds);
        long hike1 = listRawHikes.get(1).getHikeId();
        long hike2 = listRawHikes.get(2).getHikeId();
        //assertEquals(hike1, mHikeIds.get(1));
        //assertEquals(hike1, mHikeIds.get(2));


    }

    public void aHikeCanBePosted() throws Exception{
        long hikeId = mMockServer.postHike(mRawHikeData);
        assertEquals(mRawHikeData.getHikeId(), hikeId);
        assertTrue(!listRawHikes.isEmpty());

    }
}
