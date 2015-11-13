package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.mockServer.MockServer;
import ch.epfl.sweng.team7.mockServer.MockServerTest;

import static junit.framework.TestCase.assertEquals;

public class HikeListTest {
    @Rule
    public ActivityTestRule<HikeListActivity> mActivityRule = new ActivityTestRule<>(
            HikeListActivity.class);

    @Test
    public void testCorrectData() throws Exception {
        MockServer mockServer = new MockServer();
        MockServerTest mockServerTest = new MockServerTest();
        mockServerTest.mMockServer = mockServer;
        LatLngBounds bounds = new LatLngBounds(new LatLng(0.0, 0.0), new LatLng(5.0, 5.0));

        mockServerTest.setUp();
        mockServerTest.testHikeCanBePosted();
        mockServerTest.testHikeCanBePosted();

        DataManager dataManager = DataManager.getInstance();
        dataManager.setDatabaseClient(mockServer);
        List<HikeData> hikeDatas = null;

        hikeDatas = dataManager.getHikesInWindow(bounds);

        assertEquals(hikeDatas.get(0).getHikeId(), 1);
        assertEquals(hikeDatas.get(1).getHikeId(), 2);
        assertEquals(hikeDatas.get(0).getDate(), 123201);
        assertEquals(hikeDatas.get(1).getDate(), 123201);
    }

    // TODO: test UI elements display
}
