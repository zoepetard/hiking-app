package ch.epfl.sweng.team7.database;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.network.HikeParseException;
import ch.epfl.sweng.team7.network.RawHikeData;

public class DummyHikeBuilder {
    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": %d,\n"
            + "  \"owner_id\": %d,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201, 1.0],\n"
            + "    [0.1, 0.1, 123202, 2.0],\n"
            + "    [0.2, 0.0, 123203, 1.1],\n"
            + "    [0.3, 0.9, 123204, 1.2],\n"
            + "    [0.4, 0.0, 123205, 2.0]\n"
            + "  ],\n"
            + "  \"comments\": [\n"
            + "  ],\n"
            + "  \"title\": \"test hike title\"\n"
            + "  ]\n"

            + "  \"annotations\": [\n"
            + "    {\"point\":[0.0, 0.0, 123201, 1.0], \"text_annotation\": \"blablablabla\", \"picture_id\": 13},\n"
            + "    {\"point\":[0.1, 0.1, 123202, 2.0], \"text_annotation\": \"bleblebleble\", \"picture_id\": 14},\n"
            + "    {\"point\":[0.2, 0.0, 123203, 1.1], \"text_annotation\": \"blibliblibli\", \"picture_id\": 15}\n"
            + "  ]\n"
            + "}\n";

    public static RawHikeData buildRawHikeData(long hikeId) throws HikeParseException {
        try {
            String properJsonOneHike = String.format(PROPER_JSON_ONEHIKE, hikeId, SignedInUser.getInstance().getId());
            RawHikeData rawHikeData = RawHikeData.parseFromJSON(new JSONObject(properJsonOneHike));
            return rawHikeData;
        } catch(JSONException e) {
            throw new HikeParseException(e);
        }
    }

    public static HikeData buildDefaultHikeData(long hikeId) throws HikeParseException {
        return new DefaultHikeData(buildRawHikeData(hikeId));
    }
}
