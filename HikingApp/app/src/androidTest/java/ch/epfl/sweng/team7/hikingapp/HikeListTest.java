package ch.epfl.sweng.team7.hikingapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.mockServer.MockServer;

import static junit.framework.TestCase.assertEquals;

public class HikeListTest {
    //Rule
    /*public ActivityTestRule<HikeListActivity> mActivityRule = new ActivityTestRule<>(
            HikeListActivity.class);*/

    @Test
    public void testCorrectData() throws Exception {
        LatLngBounds bounds = new LatLngBounds(new LatLng(0.0, 0.0), new LatLng(5.0, 5.0));

        DataManager.setDatabaseClient(new MockServer());
        DataManager dataManager = DataManager.getInstance();
        List<HikeData> hikeDatas = null;

        hikeDatas = dataManager.getHikesInWindow(bounds);

        assertEquals(hikeDatas.get(0).getHikeId(), 1);
        assertEquals(hikeDatas.get(0).getDate().getTime(), 123201);
    }

    // TODO: test UI elements display
}
