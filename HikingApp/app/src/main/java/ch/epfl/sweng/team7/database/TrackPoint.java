/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015.
 */

package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

/**
 * Stores data for one point in a track representing a hike.
 */
public class TrackPoint {
    private LatLng mPosition;
    private long mTime;         // UTC Timestamp
    // potentially more data, like links to text and pictures

    public TrackPoint(LatLng position, long time) {
        mPosition = position;
        mTime = time;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    /**
     * @return Time of this point as UTC timestamp
     */
    public long getTime() {
        return mTime;
    }
}
