package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to adapt the GPSpath to our own representation of the Hike (RawHikeData)
 */
public class GPSAdapter  {

    private final static String LOG_FLAG = "GPS_Adapter";
    private RawHikeData mRawHikeData;
    private RawHikePoint mRawHikePoint;
    private List<RawHikePoint> hikePoints;
    private final static int FIRST_FOOT_PRINT = 0;
    private NetworkDatabaseClient mNetworkDatabaseClient;
    private static final String SERVER_URL = "http://footpath-1104.appspot.com";//"http://10.0.3.2:8080";
    private DefaultNetworkProvider mDefaultNetworkProvider;

    /**
     * Method to adapt the GPSPath to RawHikeData
     * @param gpsPath
     */
    public void adaptGPSPathToRawHikeData(GPSPath gpsPath){
        //The date of the hike is the time stamp of the first FootPrint
        Date hikeDate = new Date(gpsPath.getPath().get(FIRST_FOOT_PRINT).getTimeStamp());
        getHikePointsFromGPS(gpsPath);
        //Waiting for #iss56, by now its 0 by default
        long ownerId = 0;
        long hikeId = 1;
        mRawHikeData = new RawHikeData(hikeId,ownerId,hikeDate,hikePoints);
        storeHike(mRawHikeData);

    }

    private void storeHike(RawHikeData mRawHikeData) {
        mDefaultNetworkProvider = new DefaultNetworkProvider();
        mNetworkDatabaseClient = new NetworkDatabaseClient(SERVER_URL, mDefaultNetworkProvider );
        try {
            mNetworkDatabaseClient.postHike(mRawHikeData);
        } catch (DatabaseClientException e) {
            e.printStackTrace();
        }
    }

    private void getHikePointsFromGPS(GPSPath gpsPath) {
        hikePoints = new ArrayList<>();
        for (GPSFootPrint mGPSFootPrint: gpsPath.getPath()){
            LatLng position =  mGPSFootPrint.getGeoCoords().toLatLng();
            Double elevation = mGPSFootPrint.getGeoCoords().getAltitude();
            Date mDate = new Date(mGPSFootPrint.getTimeStamp());
            mRawHikePoint = new RawHikePoint(position, mDate, elevation);
            hikePoints.add(mRawHikePoint);
        }

    }
}
