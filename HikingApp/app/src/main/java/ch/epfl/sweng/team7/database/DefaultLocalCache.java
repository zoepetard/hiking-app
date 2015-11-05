/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 04 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.epfl.sweng.team7.network.RawHikeData;


class DefaultLocalCache implements LocalCache {
    private final int HIKES_CACHE_MAX_SIZE = 100;//TODO this should be higher
    private final HashMap<Long,HikeData> mHikesCache = new FixedSizeHashMap<>(HIKES_CACHE_MAX_SIZE);

    public DefaultLocalCache()  {
        if(false) {
            final String PROPER_JSON_ONEHIKE = "{\n"
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
            try {
                Thread.sleep(10); // this code should fail execution when called from UI thread
            } catch(InterruptedException e) {
                // pass
            }
            try {
                mHikesCache.put(new Long(1), new DefaultHikeData(RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE))));
            } catch(JSONException e) {
            }
        }
    }

    public boolean hasHike(long hikeId) {
        return mHikesCache.containsKey(hikeId);
    }

    public HikeData getHike(long hikeId) {
        return mHikesCache.get(hikeId);
    }

    public void putHike(HikeData hikeData) {
        if(hikeData != null) {
            mHikesCache.put(hikeData.getHikeId(), hikeData);
        }
    }

    public int cachedHikesCount() {
        return mHikesCache.size();
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
}
