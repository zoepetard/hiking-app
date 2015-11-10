package ch.epfl.sweng.team7.mockServer;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Created by pablo on 6/11/15.
 */
public class MockServer implements DatabaseClient {

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 1,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201],\n"
            + "    [0.1, 0.1, 123202],\n"
            + "    [0.2, 0.0, 123203],\n"
            + "    [0.3,89.9, 123204],\n"
            + "    [0.4, 0.0, 123205]\n"
            + "  ]\n"
            + "}\n";
    //Same as DefaultLocalCache
    private final int HIKES_CACHE_MAX_SIZE = 100;
    private final HashMap<Long, RawHikeData> mHikeDataBase = new FixedSizeHashMap<>(HIKES_CACHE_MAX_SIZE);
    private int mAssignedHikeID = 10;


    public boolean hasHike(long hikeId) {
        return mHikeDataBase.containsKey(hikeId);
    }

    public RawHikeData getHike(long hikeId) {
        return mHikeDataBase.get(hikeId);
    }

    public void putHike(RawHikeData rawHikeData) {
        if (rawHikeData != null) {
            mHikeDataBase.put(rawHikeData.getHikeId(), rawHikeData);
        }
    }


    private static class FixedSizeHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int MAX_ENTRIES;

        FixedSizeHashMap(int maxEntries) {
            super(16 /*initial size*/, 0.75f /*initial load factor*/, true /*update on access*/);
            MAX_ENTRIES = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }

    /**
     * Method to fetch a single RawHikeData with the given hikeID
     *
     * @param hikeId The numeric ID of one hike in the database
     * @throws DatabaseClientException
     */
    @Override
    public RawHikeData fetchSingleHike(long hikeId) throws DatabaseClientException {
        //Hike 1 should always exists
        if (hikeId == 1 && !hasHike(hikeId)) {
            return createMockHikeOne();
        } else if (hasHike(hikeId)) {
            return getHike(hikeId);
        } else {
            throw new DatabaseClientException("No hike on the server with that ID");
        }
    }

    /**
     * Create mock hike number 1 (should always exist).
     *
     * @return mockRawHike
     */
    private RawHikeData createMockHikeOne() throws DatabaseClientException {
        try {
            return createHikeData();
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Return a list of of RawHikeData with the given hikeIds
     *
     * @param hikeIds The numeric IDs of multiple hikes in the database
     * @throws DatabaseClientException
     */
    @Override
    public List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException {
        List<RawHikeData> mListRawHikeData = new ArrayList<RawHikeData>();
        for (int i = 0; i < hikeIds.size(); i++) {
            if (hasHike(hikeIds.get(i))) {
                mListRawHikeData.add(getHike(hikeIds.get(i)));
            } else {
                throw new DatabaseClientException("The hike with ID: " + hikeIds.get(i) + " it's not yet " +
                        "in the server");
            }
        }
        return mListRawHikeData;
    }

    /**
     * Return the hikeIds of hikes that are in the given window
     *
     * @param bounds Boundaries (window) of the
     * @return
     * @throws DatabaseClientException
     */
    @Override
    public List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException {
        if (mHikeDataBase != null && mHikeDataBase.size() > 0) {
            List<Long> hikeIdsInWindow = new ArrayList<>();
            for (RawHikeData rawHikeData : mHikeDataBase.values()) {
                for (RawHikePoint rawHikePoint : rawHikeData.getHikePoints()) {
                    if (bounds.contains(rawHikePoint.getPosition())) {
                        hikeIdsInWindow.add(rawHikeData.getHikeId());
                        break;
                    }
                }
            }
            return hikeIdsInWindow;
        } else {
            throw new DatabaseClientException("There are no hikes on the database yet");
        }

    }

    /**
     * Method to post a hike in the database. The database assigns a hike ID and returns that.
     *
     * @param hike to post. ID is ignored, because hike will be assigned a new ID.
     * @throws DatabaseClientException
     */
    @Override
    public long postHike(RawHikeData hike) throws DatabaseClientException {
        long hikeId = 2;
        if(hasHike(hikeId)) {
            hikeId = mAssignedHikeID;
            mAssignedHikeID++;
        }
        hike.setHikeId(hikeId);
        putHike(hike);
        return hikeId;
    }

    private static RawHikeData createHikeData() throws JSONException {
        return RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));
    }
}
