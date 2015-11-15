package ch.epfl.sweng.team7.database;

import android.accounts.NetworkErrorException;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Class to adapt the GPSpath to our own representation of the Hike (RawHikeData)
 */
public class GPSPathConverter {

    private final static String LOG_FLAG = "GPS_Adapter";
    private final static int FIRST_FOOT_PRINT = 0;
    private static final String SERVER_URL = "http://footpath-1104.appspot.com";//"http://10.0.3.2:8080";

    private static RawHikeData mRawHikeData;
    private static RawHikePoint mRawHikePoint;
    private static List<RawHikePoint> hikePoints;
    private static NetworkDatabaseClient mNetworkDatabaseClient;
    private static DefaultNetworkProvider mDefaultNetworkProvider;

    /**
     * Method to adapt the GPSPath to RawHikeData
     * @param gpsPath
     */
    //public static RawHikeData toRawHikeData()
    public static RawHikeData toRawHikeData(GPSPath gpsPath) {
        //The date of the hike is the time stamp of the first FootPrint
        Date hikeDate = new Date(gpsPath.getPath().get(FIRST_FOOT_PRINT).getTimeStamp());
        setHikePointsFromGPS(gpsPath);
        //Waiting for #iss56, by now is 0 by default
        long ownerId = 0;
        mRawHikeData = new RawHikeData(RawHikeData.HIKE_ID_UNKNOWN, ownerId, hikeDate, hikePoints);
        storeHike(mRawHikeData);
        return mRawHikeData;
    }

    /**
     * Method to store in DB the RawHikeData converted from the GPS object
     *
     * @param mRawHikeData
     */
    private static void storeHike(RawHikeData mRawHikeData) {
        mDefaultNetworkProvider = new DefaultNetworkProvider();
        mNetworkDatabaseClient = new NetworkDatabaseClient(SERVER_URL, mDefaultNetworkProvider);
        try {
            mNetworkDatabaseClient.postHike(mRawHikeData);
        } catch (DatabaseClientException e) {
            e.printStackTrace();
        }


    }

    /**
     * Method to convert the FootPrints into HikePoints
     *
     * @param gpsPath
     */
    private static void setHikePointsFromGPS(GPSPath gpsPath) {
        hikePoints = new ArrayList<>();
        for (GPSFootPrint mGPSFootPrint : gpsPath.getPath()) {
            LatLng position = mGPSFootPrint.getGeoCoords().toLatLng();
            Double elevation = mGPSFootPrint.getGeoCoords().getAltitude();
            Date mDate = new Date(mGPSFootPrint.getTimeStamp());
            mRawHikePoint = new RawHikePoint(position, mDate, elevation);
            hikePoints.add(mRawHikePoint);
        }

    }

    /**
     * Method to get the RawHikeData
     * @return the RawHike if its avalaible or null instead
     */
    public RawHikeData getRawHikeData() {
        return mRawHikeData;
    }
}
