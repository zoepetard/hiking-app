package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
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
     * @param gpsPath
     */

    public static RawHikeData toRawHikeData(GPSPath gpsPath) {
        RawHikeData mRawHikeData;
        //The date of the hike is the time stamp of the first FootPrint
        if (gpsPath.getFootPrintCount() > 0) {
            Date hikeDate = new Date(gpsPath.getPath().get(FIRST_FOOT_PRINT).getTimeStamp());
            List<RawHikePoint> rawHikePoints = getHikePointsFromGPS(gpsPath);
            //Waiting for #iss56, by now is 0 by default
            long ownerId = 0;
            mRawHikeData = new RawHikeData(RawHikeData.HIKE_ID_UNKNOWN, ownerId, hikeDate, rawHikePoints);
            return mRawHikeData;
        }else{
            throw new ArrayIndexOutOfBoundsException("GPS path is empty");
        }
    }


    /**
     * Method to convert the FootPrints into HikePoints
     *
     * @param gpsPath
     */
    private static List<RawHikePoint> getHikePointsFromGPS(GPSPath gpsPath) {
        List<RawHikePoint> hikePoints;
        hikePoints = new ArrayList<>();
        RawHikePoint mRawHikePoint;
        for (int i = 0; i < gpsPath.getFootPrintCount(); i++) {
            LatLng position = gpsPath.getPath().get(i).getGeoCoords().toLatLng();
            Double elevation = gpsPath.getPath().get(i).getGeoCoords().getAltitude();
            Date mDate = new Date(gpsPath.getPath().get(i).getTimeStamp());
            mRawHikePoint = new RawHikePoint(position, mDate, elevation);
            hikePoints.add(mRawHikePoint);
        }
        return hikePoints;
    }

}
