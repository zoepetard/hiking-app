/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015.
 */

package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

/**
 * Stores data for one point in a track representing a hike.
 */
public class TrackPoint {
    private LatLng mPosition;
    private Date mTime;         // UTC Timestamp
    // potentially more data, like links to text and pictures

    public TrackPoint(LatLng position, Date time) {
        mPosition = position;
        mTime = time;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    /**
     * @return Time of this point as UTC timestamp
     */
    public Date getTime() {
        return mTime;
    }

    public static TrackPoint parseFromJSON(JSONArray jsonArray) throws JSONException {
        //throw new JSONException("Not implemented");
        return new TrackPoint(null, null);
    }
}
