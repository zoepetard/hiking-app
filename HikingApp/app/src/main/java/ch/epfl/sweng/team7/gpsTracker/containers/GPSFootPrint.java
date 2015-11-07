package ch.epfl.sweng.team7.gpsTracker.containers;

import ch.epfl.sweng.team7.gpsTracker.containers.coordinates.GeoCoords;

/**
 * Class that contains all the information needed to
 * store every time we read the gps state.
 */
public class GPSFootPrint {

    private GeoCoords geoCoords;    //latitude, longitude and altitude
    private long timeStamp;         //time in milliseconds since January 1, 1970

    public GPSFootPrint(GeoCoords geoCoords, long timeStamp) throws NullPointerException {
        if (geoCoords == null) throw new NullPointerException("Cannot create footprint from null coordinates");
        this.geoCoords = geoCoords;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return String.format("[Coords: %s | Time: %d]", geoCoords.toString(), timeStamp);
    }
}
