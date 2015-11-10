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
import ch.epfl.sweng.team7.network.RawHikePoint;

import static android.location.Location.distanceBetween;

public class DefaultHikeData implements HikeData {

    private long mHikeId;    // Database hike ID of this hike
    private long mOwnerId;   // Database user ID of owner
    private Date mDate;      // A UTC time stamp
    private List<HikePoint> mHikePoints;
    private double mDistance;
    private LatLngBounds mBoundingBox;
    private LatLng mHikeLocation;
    private LatLng mStartLocation;
    private LatLng mFinishLocation;
    private ElevationBounds mElevationBounds;

    /**
     * A HikeData object is created from a RawHikeData, but calculates much more information
     * that can later be queried by the user interface.
     */
    public DefaultHikeData(RawHikeData rawHikeData) {
        mHikeId = rawHikeData.getHikeId();
        mOwnerId = rawHikeData.getOwnerId();
        mDate = rawHikeData.getDate();

        List<RawHikePoint> rawHikePoints = rawHikeData.getHikePoints();
        mHikePoints = new ArrayList<>();
        for (RawHikePoint rawHikePoint : rawHikePoints){
            mHikePoints.add(new DefaultHikePoint(rawHikePoint));
        }

        mDistance = calculateDistance(rawHikePoints);
        mBoundingBox = calculateBoundingBox(rawHikePoints);
        mHikeLocation = getBoundingBox().getCenter();
        mStartLocation = rawHikePoints.get(0).getPosition();
        mFinishLocation = rawHikePoints.get(rawHikePoints.size() - 1).getPosition();
        mElevationBounds = calculateElevationBounds(rawHikePoints);
    }
    /**
     * @return the hike ID.
     */
    public long getHikeId() {
        return mHikeId;
    }

    /**
     * @return the owner ID.
     */
    public long getOwnerId() {
        return mOwnerId;
    }

    /**
     * @return the date of the hike
     */
    public Date getDate() {
        return mDate;
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
        return mHikePoints;
    }

    /**
     * @return the total distance covered by the hike
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * @return the total elevation gain,
     * which is the sum of all position elevation differences, ignoring all downhill
     * This feature is not yet implemented in the backend
     */
    public double getElevationGain() {
        return 0; //TODO implement
    }

    /**
     * @return the total elevation loss,
     * which is the sum of all negative elevation differences, ignoring all uphill
     * This feature is not yet implemented in the backend
     */
    public double getElevationLoss() {
        return 0; //TODO implement
    }

    /**
     * * This feature is not yet implemented in the backend
     */
    public double getMaxElevation() {
        return 0; //TODO implement
    }

    /**
     * * This feature is not yet implemented in the backend
     */
    public double getMinElevation() {
        return 0; //TODO implement
    }

    /**
     * @return a box around the hike.
     * Queried by the backend/interface to see whether hike should be displayed.
     */
    public LatLngBounds getBoundingBox() {
        return mBoundingBox;
    }

    /**
     * @return a representative point for this hike, where the pin will be placed on the map
     */
    public LatLng getHikeLocation() {
        return mHikeLocation;
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public LatLng getFinishLocation() {
        return mFinishLocation;
    }

    private double calculateDistance(List<RawHikePoint> rawHikePoints) {
        double distance = 0;
        for (int i = 0; i < rawHikePoints.size() - 1; i++){
            LatLng currentLoc = rawHikePoints.get(i).getPosition();
            LatLng nextLoc = rawHikePoints.get(i + 1).getPosition();
            float[] distanceBetween = new float[1];
            distanceBetween(currentLoc.latitude, currentLoc.longitude,
                    nextLoc.latitude, nextLoc.longitude, distanceBetween);
            distance += (double) distanceBetween[0];
        }
        return distance;
    }

    private LatLngBounds calculateBoundingBox(List<RawHikePoint> rawHikePoints) {
        LatLngBounds.Builder boundingBoxBuilder =  new LatLngBounds.Builder();
        for (RawHikePoint rawHikePoint : rawHikePoints){
            LatLng pos = rawHikePoint.getPosition();
            boundingBoxBuilder.include(pos);
        }
        return boundingBoxBuilder.build();
    }

    private ElevationBounds calculateElevationBounds(List<RawHikePoint> rawHikePoints) {

        ElevationBounds elevationBounds = new ElevationBounds();

        // Initialize elevation
        double lastElevation = rawHikePoints.get(0).getElevation();
        elevationBounds.mMaxElevation = lastElevation;
        elevationBounds.mMinElevation = lastElevation;

        // Traverse hike points in order
        for(int i = 1; i < rawHikePoints.size(); ++i) {
            double thisElevation = rawHikePoints.get(i).getElevation();

            if(thisElevation > lastElevation) {
                elevationBounds.mElevationGain += (thisElevation - lastElevation);
            } else {
                elevationBounds.mElevationLoss -= (thisElevation - lastElevation);
            }

            if(thisElevation > elevationBounds.mMaxElevation) {
                elevationBounds.mMaxElevation = thisElevation;
            }

            if(thisElevation < elevationBounds.mMinElevation) {
                elevationBounds.mMinElevation = thisElevation;
            }
        }
        return elevationBounds;
    }

    // A simple storage container for elevation-related data
    private class ElevationBounds {
        public double mMinElevation;
        public double mMaxElevation;
        public double mElevationGain;
        public double mElevationLoss;
    }

}
