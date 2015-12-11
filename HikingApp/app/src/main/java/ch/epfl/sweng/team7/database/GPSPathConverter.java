package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.network.RawHikeComment;
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
     */
    public static RawHikeData toRawHikeData(GPSPath gpsPath) throws Exception {
        //The date of the hike is the time stamp of the first FootPrint
        if (gpsPath.getFootPrintCount() > 0) {
            Date hikeDate = new Date(gpsPath.getPath().get(FIRST_FOOT_PRINT).getTimeStamp());
            List<RawHikePoint> rawHikePoints = getHikePointsFromGPS(gpsPath);
            long ownerId = SignedInUser.getInstance().getId();
            List<RawHikeComment> newHikeComments = new ArrayList<>();
            List<Annotation> annotations = null;
            return new RawHikeData(RawHikeData.HIKE_ID_UNKNOWN, ownerId, hikeDate, rawHikePoints, newHikeComments, "", annotations);
        } else {
            throw new ArrayIndexOutOfBoundsException("GPS path is empty");
        }
    }

    /**
     * Method to convert the FootPrints into HikePoints
     */
    private static List<RawHikePoint> getHikePointsFromGPS(GPSPath gpsPath) {
        List<RawHikePoint> hikePoints = new ArrayList<>();
        for (GPSFootPrint gpsFootPrint : gpsPath.getPath()) {
            LatLng position = gpsFootPrint.getGeoCoords().toLatLng();
            Double elevation = gpsFootPrint.getGeoCoords().getAltitude();
            Date date = new Date(gpsFootPrint.getTimeStamp());
            hikePoints.add(new RawHikePoint(position, date, elevation));
        }
        return hikePoints;
    }

    /**
     * Method to create a rawHikePoints
     */
    public static RawHikePoint getHikePointsFromGeoCoords(GeoCoords geoCoords){
        if(geoCoords != null){
            //Get the actual date
            Date date = new Date();
            LatLng position = geoCoords.toLatLng();
            Double elevation = geoCoords.getAltitude();
            return new RawHikePoint(position, date, elevation);
        }else{
            throw new NullPointerException("No footprint to add to the picture");
        }
    }
}
