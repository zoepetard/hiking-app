package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.mockServer.MockDataBaseClient;
import ch.epfl.sweng.team7.mockServer.MockServer;

import static junit.framework.TestCase.assertEquals;

public class HikeListTest {
    @Rule
    public ActivityTestRule<HikeListActivity> mActivityRule = new ActivityTestRule<>(
            HikeListActivity.class);

    @Test
    public void testCorrectData() {
        MockServer mockServer = new MockServer();
        MockDataBaseClient mockDataBaseClient = new MockDataBaseClient();
        mockDataBaseClient.mMockServer = mockServer;
        LatLngBounds bounds = new LatLngBounds(new LatLng(0.0, 0.0), new LatLng(5.0, 5.0));
        try {
            mockDataBaseClient.setUp();
            mockDataBaseClient.aHikeCanBePosted();
            mockDataBaseClient.aHikeCanBePosted();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        DataManager dataManager = DataManager.getInstance();
        dataManager.setDatabaseClient(mockServer);
        List<HikeData> hikeDatas = null;
        try {
            hikeDatas = dataManager.getHikesInWindow(bounds);
        } catch (DataManagerException e) {
            e.printStackTrace();
            return;
        }
        assertEquals(hikeDatas.get(0).getHikeId(), 1);
        assertEquals(hikeDatas.get(1).getHikeId(), 2);
        assertEquals(hikeDatas.get(0).getDate(), 123201);
        assertEquals(hikeDatas.get(1).getDate(), 123201);
    }

    // TODO: test UI elements display
}
