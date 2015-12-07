package ch.epfl.sweng.team7.hikingapp;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.mockServer.MockServer;

public class HikeListTest extends ActivityInstrumentationTestCase2<HikeListActivity> {

    private HikeListActivity hikeListActivity;
    private List<HikeData> hikeDatas;
    private HikeData hikeData;

    public HikeListTest() {
        super(HikeListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        hikeListActivity = getActivity();
        LatLngBounds bounds = new LatLngBounds(new LatLng(0.0, 0.0), new LatLng(5.0, 5.0));

        DataManager.setDatabaseClient(new MockServer());
        DataManager dataManager = DataManager.getInstance();

        hikeDatas = dataManager.getHikesInWindow(bounds);
        hikeData = hikeDatas.get(0);
    }

    public void testCorrectData() throws Exception {
        assertEquals(hikeData.getHikeId(), 1);
        assertEquals(hikeData.getDate().getTime(), 123201);
    }

    public void testCustomListAdapter() throws Exception {
        CustomListAdapter adapter = new CustomListAdapter(hikeListActivity, hikeDatas);
        assertNotNull(adapter);
        assertEquals(hikeDatas.size(), adapter.getCount());

        final ListView listView = (ListView) hikeListActivity.findViewById(R.id.hike_list_view);
        assertNotNull("The list was not loaded", listView);
    }

}
