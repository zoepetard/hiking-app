package ch.epfl.sweng.team7.database;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/** Tests whether the app correctly handles proper JSON */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProperJSONParsingTest {

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

    private static final long EXPECTED_TRACK_ID = 268L;
    private static final long EXPECTED_OWNER_ID = 153L;
    private static final long EXPECTED_DATE = 123201;
    private List<Double> properTrackPointsX;
    private List<Double> properTrackPointsY;
    private List<Long> properTrackPointsT;

    @Before
    public void setUp() throws Exception {
        properTrackPointsX = new ArrayList<Double>();
        properTrackPointsX.add(0.0);
        properTrackPointsX.add(0.1);
        properTrackPointsX.add(0.2);
        properTrackPointsX.add(0.3);
        properTrackPointsX.add(0.4);
        properTrackPointsY = new ArrayList<Double>();
        properTrackPointsY.add(0.0);
        properTrackPointsY.add(0.1);
        properTrackPointsY.add(0.0);
        properTrackPointsY.add(89.9);
        properTrackPointsY.add(0.0);
        properTrackPointsT = new ArrayList<Long>();
        properTrackPointsT.add(123201L);
        properTrackPointsT.add(123202L);
        properTrackPointsT.add(123203L);
        properTrackPointsT.add(123204L);
        properTrackPointsT.add(123205L);
    }

    /** test that track ID is correctly parsed */
    @Test
    public void testProperTrackId() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        assertEquals("Track ID does not match",
                EXPECTED_TRACK_ID, t.getTrackId());
    }

    /** test that owner ID is correctly parsed */
    @Test
    public void testProperOwnerId() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        assertEquals("Owner ID does not match",
                EXPECTED_OWNER_ID, t.getOwnerId());
    }

    /** test that date is correctly parsed */
    @Test
    public void testProperDate() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        assertEquals("Date does not match",
                EXPECTED_DATE, t.getDate().getTime());
    }

    /** test that trackpoints are correctly parsed */
    @Test
    public void testProperTrackPointSize() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        List<TrackPoint> tp = t.getTrackPoints();
        assertEquals("TrackPoints size does not match",
                properTrackPointsT.size(), tp.size());
    }

    /** test that trackpoints are correctly parsed */
    @Test
    public void testProperTrackPointLatLng() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        List<TrackPoint> tp = t.getTrackPoints();
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("TrackPoints latitude does not match",
                    properTrackPointsX.get(i), tp.get(i).getPosition().latitude);
            assertEquals("TrackPoints longitude does not match",
                    properTrackPointsY.get(i), tp.get(i).getPosition().longitude);
        }
    }

    /** test that trackpoints are correctly parsed */
    @Test
    public void testProperTrackPointDate() throws JSONException {
        TrackData t = TrackData.parseFromJSON(new JSONObject(PROPER_JSON_ONETRACK));
        List<TrackPoint> tp = t.getTrackPoints();
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("TrackPoints date does not match",
                    properTrackPointsT.get(i).longValue(), tp.get(i).getTime().getTime());
        }
    }

}
