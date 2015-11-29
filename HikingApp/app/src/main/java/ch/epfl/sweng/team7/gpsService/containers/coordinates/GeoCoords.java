package ch.epfl.sweng.team7.gpsService.containers.coordinates;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class that represents Geographic coordinates with mLatitude, mLongitude and mAltitude.
 * It is meant to be used solely as a container.
 */
public class GeoCoords {

    private final static String LOG_FLAG = "GPS_GeoCoords";

    private double mLatitude;
    private double mLongitude;
    private double mAltitude;

    /**
     * Class's constructor with separated mLatitude, mLongitude and mAltitude arguments
     */
    public GeoCoords(double latitude, double longitude, double altitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
    }

    /**
     * Class' constructor with LatLng and mAltitude as arguments.
     * To be used directly with GoogleMaps API values.
     */
    public GeoCoords(LatLng latLng, double altitude) throws NullPointerException {
        if (latLng == null) {
            throw new NullPointerException("Cannot create GeoCoords from null LatLng");
        }
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
        mAltitude = altitude;
    }

    /**
     * Method called to get a copy GeoCoords as LatLng
     * @return LatLng object
     */
    public LatLng toLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }

    /**
     * Method used to create a GeoCoords object from a Location
     * @param location source Location
     * @return new GeoCoords object
     */
    public static GeoCoords fromLocation(Location location) throws NullPointerException {
        if (location == null) throw new NullPointerException("Cannot create GeoCoords from null Location");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = (location.hasAltitude())?location.getAltitude():0;
        return new GeoCoords(latitude, longitude, altitude);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", mLatitude, mLongitude, mAltitude);
    }

    @Override
    public int hashCode() {
        int latParcel = (int)((mLatitude != 0) ? mLatitude  : 1);
        int lngParcel = (int)((mLongitude != 0)? mLongitude : 1);
        int altParcel = (int)((mAltitude != 0) ? mAltitude  : 1);
        return latParcel * lngParcel * altParcel;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof GeoCoords)) return false;
        GeoCoords other = (GeoCoords)object;
        return (other.getLatitude() == mLatitude && other.getLongitude() == mLongitude && other.getAltitude() == mAltitude);
    }
}
