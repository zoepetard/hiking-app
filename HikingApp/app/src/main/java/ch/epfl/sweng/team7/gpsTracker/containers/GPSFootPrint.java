package ch.epfl.sweng.team7.gpsTracker.containers;

public class GPSFootPrint {

    private GeoCoords geoCoords;
    private long timeStamp;

    public GPSFootPrint(GeoCoords geoCoords, long timeStamp) {
        this.geoCoords = geoCoords;
        this.timeStamp = timeStamp;
    }
}
