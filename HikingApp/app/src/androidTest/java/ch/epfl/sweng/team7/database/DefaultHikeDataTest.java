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

import ch.epfl.sweng.team7.network.RawHikeComment;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static org.junit.Assert.*;

public class DefaultHikeDataTest {

    private final long hikeId = 1;
    private final long ownerId = 1;
    private final Date date = new Date(1000101);
    private List<RawHikePoint> rawHikePoints;
    private final LatLng startLocation = new LatLng(0, 0);
    private final LatLng finishLocation = new LatLng(15, 15);
    private DefaultHikeData mDefaultHikeData;
    private static final double EPS_DOUBLE = 1e-10;

    @Before
    public void setUp() throws Exception {
        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(1000101), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(10, 10), new Date(1000102), 3.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(1000103), 2.0));
        List<RawHikeComment> newHikeComments = new ArrayList<>();
        RawHikeData rawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints, newHikeComments);
        mDefaultHikeData = new DefaultHikeData(rawHikeData);
    }

    @Test
    public void testIdAccess() {
        assertEquals("Mismatched hike ID", hikeId, mDefaultHikeData.getHikeId());
    }

    @Test
    public void testOwnerAccess() {
        assertEquals("Mismatched owner ID", ownerId, mDefaultHikeData.getOwnerId());
    }

    @Test
    public void testDateAccess() {
        assertEquals("Mismatched date", date, mDefaultHikeData.getDate());
    }

    @Test
    public void testHikePointsAccess() {
        List<DefaultHikePoint> hikePoints = new ArrayList<>();
        hikePoints.add(new DefaultHikePoint(new LatLng(0, 0), new Date(1000101), 1.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(10, 10), new Date(1000102), 2.0));
        hikePoints.add(new DefaultHikePoint(new LatLng(15, 15), new Date(1000103), 3.0));
        for (int i = 0; i < rawHikePoints.size(); i++) {
            HikePoint expectedHikePoint = hikePoints.get(i);
            HikePoint calcHikePoint = mDefaultHikeData.getHikePoints().get(i);
            assertEquals("Mismatched latitude of a hike point", expectedHikePoint.getPosition().latitude, calcHikePoint.getPosition().latitude, 0);
            assertEquals("Mismatched longitude of a hike point", expectedHikePoint.getPosition().longitude, calcHikePoint.getPosition().longitude, 0);
        }
    }

    @Test
    public void testDistanceAccess() {
        assertEquals("Incorrect distance", 2340425, mDefaultHikeData.getDistance(), 5);
    }

    @Test
    public void testBoundingBoxAccess() {
        LatLngBounds.Builder boundingBoxBuilder = new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0, 0)).include(new LatLng(15, 15));
        assertEquals("Incorrect bounding box", boundingBoxBuilder.build(), mDefaultHikeData.getBoundingBox());
    }

    @Test
    public void testHikeLocationAccess() {
        LatLngBounds.Builder boundingBoxBuilder = new LatLngBounds.Builder();
        boundingBoxBuilder.include(new LatLng(0, 0)).include(new LatLng(15, 15));
        LatLng center = boundingBoxBuilder.build().getCenter();
        assertEquals("Incorrect representative hike location", center, mDefaultHikeData.getHikeLocation());
    }

    @Test
    public void testStartLocationAccess() {
        assertEquals("Incorrect representative start location", startLocation, mDefaultHikeData.getStartLocation());
    }

    @Test
    public void testFinishLocationAccess() {
        assertEquals("Incorrect representative finish location", finishLocation, mDefaultHikeData.getFinishLocation());
    }

    @Test
    public void testGetMinElevation() {
        assertEquals("Incorrect min elevation", 1.0, mDefaultHikeData.getMinElevation(), EPS_DOUBLE);
    }

    @Test
    public void testGetMaxElevation() {
        assertEquals("Incorrect max elevation", 3.0, mDefaultHikeData.getMaxElevation(), EPS_DOUBLE);
    }

    @Test
    public void testGetElevationGain() {
        assertEquals("Incorrect elevation gain", 2.0, mDefaultHikeData.getElevationGain(), EPS_DOUBLE);
    }

    @Test
    public void testGetElevationLoss() {
        assertEquals("Incorrect elevation loss", 1.0, mDefaultHikeData.getElevationLoss(), EPS_DOUBLE);
    }

}