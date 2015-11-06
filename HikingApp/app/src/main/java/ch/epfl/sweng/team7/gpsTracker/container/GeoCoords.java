package ch.epfl.sweng.team7.gpsTracker.container;

import com.google.android.gms.maps.model.LatLng;

public class GeoCoords {

    private double latitude;
    private double longitude;
    private double altitude;

    public GeoCoords(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public GeoCoords(LatLng latLng, double altitude) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.altitude = altitude;
    }

    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
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
}
