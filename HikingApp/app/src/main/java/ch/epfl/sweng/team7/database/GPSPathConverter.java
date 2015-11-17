package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.network.RawHikeComment;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;

import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Class to adapt the GPSpath to our own representation of the Hike (RawHikeData)
 */
public class GPSPathConverter {

    private final static String LOG_FLAG = "GPS_Adapter";
    private final static int FIRST_FOOT_PRINT = 0;

    /**
     * Method to adapt the GPSPath to RawHikeData
     *
     * @param gpsPath
     */
    public static RawHikeData toRawHikeData(GPSPath gpsPath) throws Exception {
        //The date of the hike is the time stamp of the first FootPrint
        if (gpsPath.getFootPrintCount() > 0) {
            Date hikeDate = new Date(gpsPath.getPath().get(FIRST_FOOT_PRINT).getTimeStamp());
            List<RawHikePoint> rawHikePoints = getHikePointsFromGPS(gpsPath);
            //Waiting for #iss56, by now is 0 by default
            long ownerId = 0;
            List<RawHikeComment> newHikeComments = new ArrayList<>();
            List<Annotation> mAnnotations = null;
            return new RawHikeData(RawHikeData.HIKE_ID_UNKNOWN, ownerId, hikeDate, rawHikePoints, newHikeComments, "", mAnnotations);
        } else {
            throw new ArrayIndexOutOfBoundsException("GPS path is empty");
        }
    }

    /**
     * Method to convert the FootPrints into HikePoints
     *
     * @param gpsPath
     */
    private static List<RawHikePoint> getHikePointsFromGPS(GPSPath gpsPath) {
        List<RawHikePoint> hikePoints = new ArrayList<>();
        for (GPSFootPrint gpsFootPrint : gpsPath.getPath()) {
            LatLng position = gpsFootPrint.getGeoCoords().toLatLng();
            Double elevation = gpsFootPrint.getGeoCoords().getAltitude();
            Date date = new Date(gpsFootPrint.getTimeStamp());
            String comment = null;
            hikePoints.add(new RawHikePoint(position, date, elevation, comment));
        }
        return hikePoints;
    }

    /**
     * Method to create a rawHikePOints
     * @param geoCoords
     * @return
     */
    public static RawHikePoint getHikePointsFromGeoCoords(GeoCoords geoCoords){
        if(geoCoords != null){
            LatLng position = geoCoords.toLatLng();
            Double elevation = geoCoords.getAltitude();
            return new RawHikePoint(position, null, elevation);
        }else{
            throw new NullPointerException("No footprint to add to the picture");
        }
    }
}
