/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class DefaultHikePoint implements HikePoint {

    private LatLng mPosition;
    private Date mTime;

    public DefaultHikePoint(LatLng position, Date time) {
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


}
