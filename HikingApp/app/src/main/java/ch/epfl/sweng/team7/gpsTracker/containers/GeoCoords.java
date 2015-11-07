package ch.epfl.sweng.team7.gpsTracker.containers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class that represents Geographic coordinates with latitude, longitude and altitude.
 * It is meant to be used solely as a container.
 */
public class GeoCoords {

    private double latitude;
    private double longitude;
    private double altitude;

    /**
     * Class's constructor with separated latitude, longitude and altitude arguments
     */
    public GeoCoords(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    /**
     * Class' constructor with LatLng and altitude as arguments.
     * To be used directly with GoogleMaps API values.
     */
    public GeoCoords(LatLng latLng, double altitude) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.altitude = altitude;
    }

    /**
     * Method called to get a copy GeoCoords as LatLng
     * @return LatLng object
     */
    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    /**
     * Method used to create a GeoCoords object from a Location
     * @param location source Location
     * @return new GeoCoords object
     */
    public static GeoCoords fromLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = (location.hasAltitude())?location.getAltitude():0;
        return new GeoCoords(latitude, longitude, altitude);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", this.latitude, this.longitude, this.altitude);
    }

    @Override
    public boolean equals(Object object) {
        GeoCoords other = (GeoCoords)object;
        if (other.getLatitude() == this.latitude && other.getLongitude() == this.longitude && other.getAltitude() == this.altitude) {
            return true;
        }
        return false;
    }
}
