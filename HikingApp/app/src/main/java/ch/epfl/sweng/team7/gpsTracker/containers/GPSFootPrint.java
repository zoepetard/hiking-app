package ch.epfl.sweng.team7.gpsTracker.containers;

import ch.epfl.sweng.team7.gpsTracker.containers.coordinates.GeoCoords;

public class GPSFootPrint {

    private GeoCoords geoCoords;
    private long timeStamp;

    public GPSFootPrint(GeoCoords geoCoords, long timeStamp) {
        this.geoCoords = geoCoords;
        this.timeStamp = timeStamp;
    }
}
