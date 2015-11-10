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
    private Date date = new Date(100, 01, 01);
    private List<RawHikePoint> rawHikePoints;
    private LatLng startLocation = new LatLng(0,0);
    private LatLng finishLocation = new LatLng(15, 15);

    @Before
    public void setUp() throws Exception {
        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(100, 01, 01), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(10,10), new Date(100, 01, 01), 2.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(100, 01, 01), 3.0));

    }
    @Test
    public void testIdAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Mismatched hike ID", hikeId, defaultHikeData.getHikeId());
    }

    @Test
    public void testOwnerAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Mismatched owner ID", ownerId, defaultHikeData.getOwnerId());
    }

    @Test
    public void testDateAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Mismatched date", date, defaultHikeData.getDate());
    }

    @Test
    public void testHikePointsAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);

        List<DefaultHikePoint> hikePoints = new ArrayList<>();
        hikePoints.add(new DefaultHikePoint(new LatLng(0,0), new Date(100, 01, 01), 1.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(10,10), new Date(100, 01, 01), 2.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(15, 15), new Date(100, 01, 01), 3.0));
        for (int i = 0; i < rawHikePoints.size(); i++) {
            DefaultHikePoint expectedHikePoint = hikePoints.get(i);
            DefaultHikePoint calcHikePoint = (DefaultHikePoint)defaultHikeData.getHikePoints().get(i);
            assertEquals("Mismatched latitude of a hike point", expectedHikePoint.getPosition().latitude, calcHikePoint.getPosition().latitude, 0);
            assertEquals("Mismatched longitude of a hike point", expectedHikePoint.getPosition().longitude, calcHikePoint.getPosition().longitude, 0);
        }
    }

    @Test
    public void testDistanceAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Incorrect distance", 2345000, defaultHikeData.getDistance(), 5000);
    }

    @Test
    public void testBoundingBoxAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        LatLngBounds.Builder boundingBoxBuilder =  new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0,0)).include(new LatLng(15, 15));
        assertEquals("Incorrect bounding box", boundingBoxBuilder.build(), defaultHikeData.getBoundingBox());
    }

    @Test
    public void testHikeLocationAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        LatLngBounds.Builder boundingBoxBuilder =  new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0, 0)).include(new LatLng(15, 15));
        LatLng center = boundingBoxBuilder.build().getCenter();
        assertEquals("Incorrect representative hike location", center, defaultHikeData.getHikeLocation());
    }

    @Test
    public void testStartLocationAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Incorrect representative start location", startLocation, defaultHikeData.getStartLocation());
    }

    @Test
    public void testFinishLocationAccess() {
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);
        assertEquals("Incorrect representative finish location", finishLocation, defaultHikeData.getFinishLocation());
    }

}