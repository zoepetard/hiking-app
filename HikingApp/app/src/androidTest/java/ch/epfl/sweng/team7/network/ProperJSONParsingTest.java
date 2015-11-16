package ch.epfl.sweng.team7.network;

import android.support.test.runner.AndroidJUnit4;

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
public class ProperJSONParsingTest {

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 268,\n"
            + "  \"owner_id\": 153,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201, 1.0],\n"
            + "    [0.1, 0.1, 123202, 2.0],\n"
            + "    [0.2, 0.0, 123203, 1.1],\n"
            + "    [0.3,89.9, 123204, 1.2],\n"
            + "    [0.4, 0.0, 123205, 2.0]\n"
            + "  ]\n"
            + "}\n";

    private static final long EXPECTED_HIKE_ID = 268L;
    private static final long EXPECTED_OWNER_ID = 153L;
    private static final long EXPECTED_DATE = 123201;
    private static final double EPS_DOUBLE = 1e-10;
    private List<Double> properHikePointsX;
    private List<Double> properHikePointsY;
    private List<Double> properHikePointsZ;
    private List<Long> properHikePointsT;

    @Before
    public void setUp() throws Exception {
        properHikePointsX = new ArrayList<Double>();
        properHikePointsX.add(0.0);
        properHikePointsX.add(0.1);
        properHikePointsX.add(0.2);
        properHikePointsX.add(0.3);
        properHikePointsX.add(0.4);
        properHikePointsY = new ArrayList<Double>();
        properHikePointsY.add(0.0);
        properHikePointsY.add(0.1);
        properHikePointsY.add(0.0);
        properHikePointsY.add(89.9);
        properHikePointsY.add(0.0);
        properHikePointsZ = new ArrayList<Double>();
        properHikePointsZ.add(1.0);
        properHikePointsZ.add(2.0);
        properHikePointsZ.add(1.1);
        properHikePointsZ.add(1.2);
        properHikePointsZ.add(2.0);
        properHikePointsT = new ArrayList<Long>();
        properHikePointsT.add(123201L);
        properHikePointsT.add(123202L);
        properHikePointsT.add(123203L);
        properHikePointsT.add(123204L);
        properHikePointsT.add(123205L);
    }

    /** test that hike ID is correctly parsed */
    @Test
    public void testProperHikeId() throws HikeParseException {
        RawHikeData t = createHikeData();
        assertEquals("Hike ID does not match",
                EXPECTED_HIKE_ID, t.getHikeId());
    }

    /** test that owner ID is correctly parsed */
    @Test
    public void testProperOwnerId() throws HikeParseException {
        RawHikeData t = createHikeData();
        assertEquals("Owner ID does not match",
                EXPECTED_OWNER_ID, t.getOwnerId());
    }

    /** test that date is correctly parsed */
    @Test
    public void testProperDate() throws HikeParseException {
        RawHikeData t = createHikeData();
        assertEquals("Date does not match",
                EXPECTED_DATE, t.getDate().getTime());
    }

    /** test that hikepoints are correctly parsed */
    @Test
    public void testProperHikePointSize() throws HikeParseException {
        RawHikeData t = createHikeData();
        List<RawHikePoint> tp = t.getHikePoints();
        assertEquals("HikePoints size does not match",
                properHikePointsT.size(), tp.size());
    }

    /** test that hikepoints are correctly parsed */
    @Test
    public void testProperHikePointLatLng() throws HikeParseException {
        RawHikeData t = createHikeData();
        List<RawHikePoint> tp = t.getHikePoints();
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("HikePoints latitude does not match",
                    properHikePointsX.get(i), tp.get(i).getPosition().latitude, EPS_DOUBLE);
            assertEquals("HikePoints longitude does not match",
                    properHikePointsY.get(i), tp.get(i).getPosition().longitude, EPS_DOUBLE);
        }
    }

    /** test that hikepoints are correctly parsed */
    @Test
    public void testProperHikePointDate() throws Exception {
        RawHikeData t = createHikeData();
        List<RawHikePoint> tp = t.getHikePoints();
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("HikePoints date does not match",
                    properHikePointsT.get(i).longValue(), tp.get(i).getTime().getTime());
        }
    }

    /** test that hikepoints are correctly parsed */
    @Test
    public void testProperHikePointElevation() throws Exception {
        RawHikeData t = createHikeData();
        List<RawHikePoint> tp = t.getHikePoints();
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("HikePoints elevation does not match",
                    properHikePointsZ.get(i), tp.get(i).getElevation(), EPS_DOUBLE);
        }
    }

    /** test that parsing back from RawHikeData to JSON works */
    @Test
    public void testParseBackToJSON() throws Exception {
        RawHikeData t = createHikeData();
        JSONObject j = t.toJSON();

        assertEquals("Hike ID does not match",
                t.getHikeId(), j.getLong("hike_id"));
        assertEquals("Owner ID does not match",
                t.getOwnerId(), j.getLong("owner_id"));
        assertEquals("Date does not match",
                t.getDate().getTime(), j.getLong("date"));

        List<RawHikePoint> tp = t.getHikePoints();
        assertEquals("Point Size does not match",
                tp.size(), j.getJSONArray("hike_data").length());
        for(int i = 0; i < tp.size(); ++i) {
            assertEquals("HikePoints latitude does not match",
                    tp.get(i).getPosition().latitude,
                    j.getJSONArray("hike_data").getJSONArray(i).getDouble(0), EPS_DOUBLE);
            assertEquals("HikePoints longitude does not match",
                    tp.get(i).getPosition().longitude,
                    j.getJSONArray("hike_data").getJSONArray(i).getDouble(1), EPS_DOUBLE);
            assertEquals("HikePoints date does not match",
                    tp.get(i).getTime().getTime(),
                    j.getJSONArray("hike_data").getJSONArray(i).getLong(2), EPS_DOUBLE);
        }
    }

    /**
     * Create a valid HikeData object
     * @return a HikeData object
     */
    private static RawHikeData createHikeData() throws HikeParseException {
        try {
            return RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));
        } catch(JSONException e) {
            throw new HikeParseException(e);
        }
    }
}
