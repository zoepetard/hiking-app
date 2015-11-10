/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public interface HikePoint {
    LatLng getPosition();
    double getElevation();
    Date getTime();
}
