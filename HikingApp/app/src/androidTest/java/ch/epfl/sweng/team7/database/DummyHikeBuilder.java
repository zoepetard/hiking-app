package ch.epfl.sweng.team7.database;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.team7.network.RawHikeData;

public class DummyHikeBuilder {

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 143,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201, 1.0],\n"
            + "    [0.1, 0.1, 123202, 2.0],\n"
            + "    [0.2, 0.0, 123203, 1.1],\n"
            + "    [0.3,89.9, 123204, 1.2],\n"
            + "    [0.4, 0.0, 123205, 2.0]\n"
            + "  ]\n"
            + "}\n";

    public static RawHikeData buildRawHikeData(long hikeId) throws JSONException {
        RawHikeData rawHikeData = RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));
        rawHikeData.setHikeId(hikeId);
        return rawHikeData;
    }

    public static HikeData buildDefaultHikeData(long hikeId) throws JSONException {
        return new DefaultHikeData(buildRawHikeData(hikeId));
    }
}
