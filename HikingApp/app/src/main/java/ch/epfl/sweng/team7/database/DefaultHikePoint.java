/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import ch.epfl.sweng.team7.network.RawHikePoint;

public class DefaultHikePoint implements HikePoint {

    private final static String LOG_FLAG = "DB_DefaultHikePoint";

    private final LatLng mPosition;
    private final Date mTime;
    private final double mElevation;

    public DefaultHikePoint(LatLng position, Date time, double elevation) {
        mPosition = position;
        mTime = time;
        mElevation = elevation;

    }

    public DefaultHikePoint(RawHikePoint rawHikePoint) {
        mPosition = rawHikePoint.getPosition();
        mTime = rawHikePoint.getTime();
        mElevation = rawHikePoint.getElevation();
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public double getElevation() {
        return mElevation;
    }

    /**
     * @return Time of this point as UTC timestamp
     */
    public Date getTime() {
        return mTime;
    }
}
