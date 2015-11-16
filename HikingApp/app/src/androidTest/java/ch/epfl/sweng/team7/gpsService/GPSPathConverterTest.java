package ch.epfl.sweng.team7.gpsService;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.database.GPSPathConverter;
import ch.epfl.sweng.team7.gpsService.containers.GPSFootPrint;
import ch.epfl.sweng.team7.gpsService.containers.GPSPath;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static org.junit.Assert.*;

/**
 * Class to create Mock GPS information to test GPSPathConverter
 */
public class GPSPathConverterTest {

    private GPSPath mGPSPath;
    private RawHikeData mRawHikeDataConverted, mRawHikeDataOriginal;
    private List<RawHikePoint> rawHikePoints;
    private LatLng startLocation;
    private long hikeId;
    private long ownerId = 0;
    private Date date1 = new Date(100);
    private double epsilon = 0;



    @Before
    public void setUp() throws Exception {
        mGPSPath = new GPSPath();
        startLocation =  new LatLng(0,0);
        hikeId = -1;
        ownerId = 0;
        mGPSPath.addFootPrint(new GPSFootPrint(new GeoCoords(0,0,0), 100));
        mGPSPath.addFootPrint(new GPSFootPrint(new GeoCoords(1,1,1), 200));
        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(100), 0));
        mRawHikeDataOriginal = new RawHikeData(hikeId, ownerId, date1, rawHikePoints);
        mRawHikeDataConverted = GPSPathConverter.toRawHikeData(mGPSPath);
    }
    @Test
    public void testFootPrintsAreConverted() throws Exception{
        LatLng position = mRawHikeDataConverted.getHikePoints().get(0).getPosition();
        Date mDate = mRawHikeDataConverted.getHikePoints().get(0).getTime();
        double elevation = mRawHikeDataConverted.getHikePoints().get(0).getElevation();
        RawHikePoint mRawHikePointConverted = new RawHikePoint(position, mDate, elevation);
        assertEquals(mRawHikePointConverted.getPosition(),mRawHikeDataOriginal.getHikePoints().get(0).getPosition());
        assertEquals(mRawHikePointConverted.getElevation(), mRawHikeDataOriginal.getHikePoints().get(0).getElevation(),
                    epsilon);

        assertEquals(mRawHikePointConverted.getTime(), mRawHikeDataOriginal.getHikePoints().get(0).getTime());
        assertEquals(mRawHikeDataConverted.getDate(), mRawHikeDataOriginal.getDate());
        assertEquals(mRawHikeDataConverted.getOwnerId(), mRawHikeDataOriginal.getOwnerId());
        assertEquals(mRawHikeDataConverted.getHikeId(), mRawHikeDataOriginal.getHikeId());


    }
}


