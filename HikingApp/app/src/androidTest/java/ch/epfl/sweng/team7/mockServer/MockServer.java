package ch.epfl.sweng.team7.mockServer;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.RawHikeData;

import static org.mockito.Mockito.mock;

/**
 * Created by pablo on 6/11/15.
 */
public class MockServer implements DatabaseClient{

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
    private final int HIKES_CACHE_MAX_SIZE = 100;//TODO this should be higher
    private final HashMap<Long,RawHikeData> mHikeDataBase = new FixedSizeHashMap<>(HIKES_CACHE_MAX_SIZE);



    public boolean hasHike(long hikeId) {
        return mHikeDataBase.containsKey(hikeId);
    }

    public RawHikeData getHike(long hikeId) {
        return mHikeDataBase.get(hikeId);
    }

    public void putHike(RawHikeData rawHikeData) {
        if(rawHikeData != null) {
            mHikeDataBase.put(rawHikeData.getHikeId(), rawHikeData);
        }
    }

    private class FixedSizeHashMap<K,V> extends LinkedHashMap<K,V> {
        private final int MAX_ENTRIES;

        FixedSizeHashMap(int maxEntries) {
            super(16, 0.75f, true);
            MAX_ENTRIES = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }


    @Override
    public RawHikeData fetchSingleHike(long hikeId) throws DatabaseClientException {
        //Hike 1 should always exists
        if(hikeId == 1 && !hasHike(hikeId)){
            return createMockHikeOne();
        }

        else if(hasHike(hikeId)) {
            return getHike(hikeId);
        }else{
            throw new DatabaseClientException("No hike on the server with that ID");
        }
    }

    /**
     * Create mock hike number 1.
     * @return mockRawHike
     */
    private RawHikeData createMockHikeOne() {

        //Create mock Hike
        //RawHikeData mockRawHikeData = mock(RawHikeData.class);
        //return RawHikeData.parseFromJSON(PROPER_JSON_ONEHIKE);

    return null;
    }

    @Override
    public List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException {
        List<RawHikeData> mListRawHikeData = new ArrayList<RawHikeData>();
        for(int i = 0; i<hikeIds.size(); i++){
            if(hasHike(hikeIds.get(i))){
                mListRawHikeData.add(getHike(hikeIds.get(i)));
            }else{
                throw new DatabaseClientException("The hike with ID: "+ hikeIds.get(i)+ "it's not yet" +
                        "in the server");
            }
        }
        return mListRawHikeData;
    }

    @Override
    public List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }

    @Override
    public long postHike(RawHikeData hike) throws DatabaseClientException {
        putHike(hike);
        return hike.getHikeId();
    }


}
