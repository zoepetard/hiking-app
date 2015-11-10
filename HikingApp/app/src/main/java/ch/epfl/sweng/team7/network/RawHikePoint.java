/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015.
 */

package ch.epfl.sweng.team7.network;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

/**
 * Stores data for one point in a hike.
 */
public class RawHikePoint {
    private LatLng mPosition;
    private Date mTime;         // UTC Timestamp

    public RawHikePoint(LatLng position, Date time) {
        mPosition = position;
        mTime = time;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    // TODO implement
    public double getElevation() { return 0; }

    /**
     * @return Time of this point as UTC timestamp
     */
    public Date getTime() {
        return mTime;
    }

    /**
     * @return JSONArray [double lat, double lng, long date]. Storing a JSON array instead of
     *                   a full JSON object reduces the communication/storage data size of a hike.
     * @throws JSONException
     */
    public JSONArray toJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(mPosition.latitude).put(mPosition.longitude).put(mTime.getTime());
        return jsonArray;
    }

    /**
     * Parse a RawHikePoint from an appropriate JSON object
     * @param jsonArray [double lat, double lng, long date]
     * @return a valid RawHikePoint object
     * @throws JSONException
     */
    public static RawHikePoint parseFromJSON(JSONArray jsonArray) throws JSONException {
        LatLng latLng = new LatLng(jsonArray.getDouble(0), jsonArray.getDouble(1));
        Date date = new Date(jsonArray.getLong(2));
        return new RawHikePoint(latLng, date);
    }
}
