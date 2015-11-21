package ch.epfl.sweng.team7.gpsService.containers;

import android.location.Location;

import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

/**
 * Class that contains all the information needed to
 * store every time we read the gps state.
 */
public class GPSFootPrint {

    private final static String LOG_FLAG = "GPS_FootPrint";

    private GeoCoords geoCoords;    //latitude, longitude and altitude
    private long timeStamp;         //time in milliseconds since January 1, 1970

    public GPSFootPrint(GeoCoords geoCoords, long timeStamp) throws NullPointerException {
        if (geoCoords == null) throw new NullPointerException("Cannot create footprint from null coordinates");
        this.geoCoords = geoCoords;
        this.timeStamp = timeStamp;
    }

    public GeoCoords getGeoCoords() {
        return this.geoCoords;
    }

    /**
     * Method called to convert a GPSFootprint to a Location
     * @return Location object
     */
    public Location toLocation() {
        Location location = new Location("");
        location.setTime(timeStamp);
        location.setLongitude(geoCoords.getLongitude());
        location.setLatitude(geoCoords.getLatitude());
        location.setAltitude(geoCoords.getAltitude());
        return location;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public int hashCode() {
        return this.geoCoords.hashCode() * (int)this.timeStamp;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof GPSFootPrint)) return false;
        GPSFootPrint other = (GPSFootPrint)object;
        return (other.getGeoCoords() == this.getGeoCoords() && other.getTimeStamp() == this.getTimeStamp());
    }

    @Override
    public String toString() {
        return String.format("[Coords: %s | Time: %d]", geoCoords.toString(), timeStamp);
    }
}
