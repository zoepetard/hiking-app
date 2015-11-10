/**
 * Created by zoepetard on 09/11/15.
 */

package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static org.junit.Assert.*;

public class DefaultHikeDataTest {

    private long hikeId = 1;
    private long ownerId = 1;
    private Date date = new Date(1000101);
    private List<RawHikePoint> rawHikePoints;
    private LatLng startLocation = new LatLng(0,0);
    private LatLng finishLocation = new LatLng(15, 15);
    private RawHikeData mRawHikeData;
    private static final double EPS_DOUBLE = 1e-10;

    @Before
    public void setUp() throws Exception {
        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(1000101), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(10,10), new Date(1000102), 3.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(1000103), 2.0));
        mRawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
    }
    @Test
    public void testIdAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Mismatched hike ID", hikeId, defaultHikeData.getHikeId());
    }

    @Test
    public void testOwnerAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Mismatched owner ID", ownerId, defaultHikeData.getOwnerId());
    }

    @Test
    public void testDateAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Mismatched date", date, defaultHikeData.getDate());
    }

    @Test
    public void testHikePointsAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);

        List<DefaultHikePoint> hikePoints = new ArrayList<>();
        hikePoints.add(new DefaultHikePoint(new LatLng(0,0), new Date(1000101), 1.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(10,10), new Date(1000102), 2.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(15, 15), new Date(1000103), 3.0));
        for (int i = 0; i < rawHikePoints.size(); i++) {
            DefaultHikePoint expectedHikePoint = hikePoints.get(i);
            DefaultHikePoint calcHikePoint = (DefaultHikePoint)defaultHikeData.getHikePoints().get(i);
            assertEquals("Mismatched latitude of a hike point", expectedHikePoint.getPosition().latitude, calcHikePoint.getPosition().latitude, 0);
            assertEquals("Mismatched longitude of a hike point", expectedHikePoint.getPosition().longitude, calcHikePoint.getPosition().longitude, 0);
        }
    }

    @Test
    public void testDistanceAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect distance", 2345000, defaultHikeData.getDistance(), 5000);
    }

    @Test
    public void testBoundingBoxAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        LatLngBounds.Builder boundingBoxBuilder =  new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0,0)).include(new LatLng(15, 15));
        assertEquals("Incorrect bounding box", boundingBoxBuilder.build(), defaultHikeData.getBoundingBox());
    }

    @Test
    public void testHikeLocationAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        LatLngBounds.Builder boundingBoxBuilder =  new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0, 0)).include(new LatLng(15, 15));
        LatLng center = boundingBoxBuilder.build().getCenter();
        assertEquals("Incorrect representative hike location", center, defaultHikeData.getHikeLocation());
    }

    @Test
    public void testStartLocationAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect representative start location", startLocation, defaultHikeData.getStartLocation());
    }

    @Test
    public void testFinishLocationAccess() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect representative finish location", finishLocation, defaultHikeData.getFinishLocation());
    }

    @Test
    public void testGetMinElevation() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect min elevation", 1.0, defaultHikeData.getMinElevation(), EPS_DOUBLE);
    }

    @Test
    public void testGetMaxElevation() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect max elevation", 3.0, defaultHikeData.getMaxElevation(), EPS_DOUBLE);
    }

    @Test
    public void testGetElevationGain() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect elevation gain", 2.0, defaultHikeData.getElevationGain(), EPS_DOUBLE);
    }

    @Test
    public void testGetElevationLoss() {
        DefaultHikeData defaultHikeData = new DefaultHikeData(mRawHikeData);
        assertEquals("Incorrect elevation loss", 1.0, defaultHikeData.getElevationLoss(), EPS_DOUBLE);
    }

}