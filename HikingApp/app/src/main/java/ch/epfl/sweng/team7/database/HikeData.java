/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.network.Rating;

/**
 * This is the interface that the UI should program against.
 * A HikeData object is usually created from a RawHikeData, but calculates much more information
 * that can later be queried by the user interface.
 */
public interface HikeData {

    /**
     * @return the hike ID.
     */
    long getHikeId();

    /**
     * @return the owner ID.
     */
    long getOwnerId();

    /**
     * @return the date of the hike
     */
    Date getDate();

    /**
     * @return the rating of the hike
     * This feature is not yet implemented in the backend
     */
    Rating getRating();

    /**
     * @return an ordered list of the waypoints on this hike
     */
    List<HikePoint> getHikePoints();

    /**
     * @return the total distance covered by the hike
     */
    double getDistance();

    /**
     * @return the total elevation gain,
     * which is the sum of all position elevation differences, ignoring all downhill
     */
    double getElevationGain();

    /**
     * @return the total elevation loss,
     * which is the sum of all negative elevation differences, ignoring all uphill
     */
    double getElevationLoss();

    double getMaxElevation();
    double getMinElevation();

    /**
     * @return a box around the hike.
     * Queried by the backend/interface to see whether hike should be displayed.
     */
    LatLngBounds getBoundingBox();

    /**
     * @return a representative point for this hike, where the pin will be placed on the map
     */
    LatLng getHikeLocation();
    LatLng getStartLocation();
    LatLng getFinishLocation();
}
