/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.network.RawHikeData;

public class DefaultHikeData {

    // Some debug locations, TODO remove later
    private static final LatLng DEBUG_LOC_ACCRA = new LatLng(5.615986, -0.171533);
    private static final LatLng DEBUG_LOC_SAOTOME = new LatLng(0.362365, 6.558835);

    /**
     * A HikeData object is created from a RawHikeData, but calculates much more information
     * that can later be queried by the user interface.
     */
    public DefaultHikeData(RawHikeData rawHikeData) {
        // TODO evaluate rawHikeData
    }
    /**
     * @return the hike ID.
     */
    public long getHikeId() {
        return 0;   //TODO implement
    }

    /**
     * @return the owner ID.
     */
    public long getOwnerId() {
        return 0;   //TODO implement
    }

    /**
     * @return the date of the hike
     */
    public Date getDate() {
        return new Date();  //TODO implement
    }

    /**
     * @return the rating of the hike
     * This feature is not yet implemented in the backend
     */
    public double getRating() {
        return 3;
    }

    /**
     * @return an ordered list of the waypoints on this hike
     */
    public List<HikePoint> getHikePoints() {
        return new ArrayList<HikePoint>();  // TODO implement
    }

    /**
     * @return the total distance covered by the hike
     */
    public double getDistance() {
        return 0; //TODO implement
    }

    /**
     * @return the total elevation gain,
     * which is the sum of all position elevation differences, ignoring all downhill
     */
    public double getElevationGain() {
        return 0; //TODO implement
    }

    /**
     * @return the total elevation loss,
     * which is the sum of all negative elevation differences, ignoring all uphill
     */
    public double getElevationLoss() {
        return 0; //TODO implement
    }

    public double getMaxElevation() {
        return 0; //TODO implement
    }

    public double getMinElevation() {
        return 0; //TODO implement
    }

    /**
     * @return a box around the hike.
     * Queried by the backend/interface to see whether hike should be displayed.
     */
    public LatLngBounds getBoundingBox() {
        return new LatLngBounds(DEBUG_LOC_ACCRA, DEBUG_LOC_SAOTOME);//TODO implement
    }

    /**
     * @return a representative point for this hike, where the pin will be placed on the map
     */
    public LatLng getHikeLocation() {
        return DEBUG_LOC_ACCRA;
    }

    public LatLng getStartLocation() {
        return DEBUG_LOC_ACCRA;
    }

    public LatLng getFinishLocation() {
        return DEBUG_LOC_SAOTOME;
    }
}
