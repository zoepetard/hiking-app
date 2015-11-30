package ch.epfl.sweng.team7.network;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

/** Tests whether the app correctly handles proper JSON */
@RunWith(AndroidJUnit4.class)
public class ImproperJSONParsingTest {

    private static final String JSON_OPEN = "{";
    private static final String JSON_HIKE_ID = "  \"hike_id\": 268,";
    private static final String JSON_OWNER_ID =  "  \"owner_id\": 153,";
    private static final String JSON_DATE =  "  \"date\": 123201,";
    private static final String JSON_HIKE_DATA =  "  \"hike_data\": ["
            + "    [0.0, 0.0, 123201, 1.0],"
            + "    [0.1, 0.1, 123202, 2.0],"
            + "    [0.2, 0.0, 123203, 1.1],"
            + "    [0.3,89.9, 123204, 1.2],"
            + "    [0.4, 0.0, 123205, 2.0]"
            + "  ]";
    private static final String JSON_CLOSE =  "}";

    @Test
    public void testNoHikeId() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_OWNER_ID + JSON_HIKE_DATA + JSON_CLOSE));
            fail("JSON parser did not throw exception when Hike Id was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNegativeHikeId() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + "\"hike_id\": -268," + JSON_DATE + JSON_OWNER_ID + JSON_HIKE_DATA + JSON_CLOSE));
            fail("JSON parser did not throw exception when Hike Id was negative.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoOwnerId() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_HIKE_DATA + JSON_CLOSE));
            fail("JSON parser did not throw exception when Owner Id was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNegativeOwnerId() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + "\"owner_id\": -268," + JSON_DATE + JSON_HIKE_ID + JSON_HIKE_DATA + JSON_CLOSE));
            fail("JSON parser did not throw exception when Owner Id was negative.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoDate() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_OWNER_ID + JSON_HIKE_ID + JSON_HIKE_DATA + JSON_CLOSE));
            fail("JSON parser did not throw exception when Date was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoHikeData() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_OWNER_ID + "\"filler\":0" + JSON_CLOSE));
            fail("JSON parser did not throw exception when Data was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoHikeData_NoPoints() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_OWNER_ID + "\"hike_data\":[]" + JSON_CLOSE));
            fail("JSON parser did not throw exception when Data was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoHikeData_OnePoint() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_OWNER_ID + "\"hike_data\":[[\"text\",0.4, 0.0, 123205, 2.0]]" + JSON_CLOSE));
            fail("JSON parser did not throw exception when Data was malformed.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoHikeData_ShortArray() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_OWNER_ID + "\"hike_data\":[[0.4, 0.0, 123205, 2.0],[0.4, 0.0, 123205]]" + JSON_CLOSE));
            fail("JSON parser did not throw exception when Data was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }

    @Test
    public void testNoHikeData_BadArray() throws Exception {
        try {
            RawHikeData.parseFromJSON(new JSONObject(JSON_OPEN + JSON_DATE + JSON_HIKE_ID + JSON_OWNER_ID + "\"hike_data\":[[0.4, 0.0, 123205, 2.0],[0.4, 0.0, {\"elev\":0}]]" + JSON_CLOSE));
            fail("JSON parser did not throw exception when Data was missing.");
        } catch (HikeParseException e) {
            // pass
        }
    }
}
