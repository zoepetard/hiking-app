package ch.epfl.sweng.team7.hikingapp;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.mockServer.MockServer;

public class HikeListTest extends ActivityInstrumentationTestCase2<HikeListActivity> {
    // Commented out because not currently needed. Might be used in issue 60.
    //Rule
    /*public ActivityTestRule<HikeListActivity> mActivityRule = new ActivityTestRule<>(
            HikeListActivity.class);*/

    private HikeListActivity hikeListActivity;
    private List<HikeData> hikeDatas;
    private HikeData hikeData;
    private GridLayout gridLayout;

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
        TableRow hikeRow = hikeListActivity.getHikeRow(1, hikeData);

        gridLayout =  (GridLayout) hikeRow.getVirtualChildAt(0);
    }

    public void testCorrectData() throws Exception {
        assertEquals(hikeDatas.get(0).getHikeId(), 1);
        assertEquals(hikeDatas.get(0).getDate().getTime(), 123201);
    }

   public void testHikeRowFormat() throws Exception {
       assertEquals(3, gridLayout.getRowCount());
       assertEquals(2, gridLayout.getColumnCount());

       int gridChildren = gridLayout.getChildCount();
       assertEquals(4, gridChildren);
   }

    public void testHikeRowContent() throws Exception {
        String nameExpected = "Hike #" + Double.toString(hikeData.getHikeId());
        String nameActual = ((TextView) gridLayout.getChildAt(1)).getText().toString();
        assertEquals(nameExpected, nameActual);

        double distance = hikeData.getDistance();
        String distanceExpected = "Distance: " + Double.toString(distance / 1000) + "km";
        String distanceActual = ((TextView) gridLayout.getChildAt(2)).getText().toString();
        assertEquals(distanceExpected, distanceActual);

        String ratingExpected = "Rating: " + Double.toString(hikeData.getRating().getDisplayRating());
        String ratingActual = ((TextView) gridLayout.getChildAt(3)).getText().toString();
        assertEquals(ratingExpected, ratingActual);
    }

}
