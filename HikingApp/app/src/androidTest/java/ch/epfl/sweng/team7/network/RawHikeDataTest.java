package ch.epfl.sweng.team7.network;

/**
 * Created by simon on 11/27/15.
 */

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Tests whether communication with the backend
 * server works. These tests may fail if the
 * backend server is not available.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RawHikeDataTest extends TestCase {

    private static final double EPS_DOUBLE = 1e-10;

    private static final String PROPER_GPX_ONEHIKE = ""
            + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<gpx creator=\"Wikiloc - http://www.wikiloc.com\" version=\"1.1\"><trk>"
            +"<name>Rochers de Naye</name><cmt></cmt><desc></desc><trkseg>"
            +"<trkpt lat=\"46.451290\" lon=\"6.976647\"><ele>1509.0</ele><time>2015-11-27T15:49:15Z</time></trkpt>"
            +"<trkpt lat=\"46.451195\" lon=\"6.976807\"><ele>1512.0</ele><time>2015-11-27T15:49:55Z</time></trkpt>"
            + "</trkseg></trk></gpx>";

    @Test
    public void testCreate() throws Exception {
        List<RawHikePoint> rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(new LatLng(1, 2), new Date(3), 4));
        rawHikePoints.add(new RawHikePoint(new LatLng(11, 12), new Date(13), 14));

        List<RawHikeComment> newHikeComments = new ArrayList<>();
        RawHikeData rawHikeData = new RawHikeData(21, 22, new Date(23), rawHikePoints, newHikeComments, "");

        assertEquals(21, rawHikeData.getHikeId());
        assertEquals(22, rawHikeData.getOwnerId());
        assertEquals(23, rawHikeData.getDate().getTime());

        assertEquals(2, rawHikeData.getHikePoints().size());
        for(int i = 0; i < 2; ++i) {
            assertEquals(10*i+1, rawHikeData.getHikePoints().get(i).getPosition().latitude, EPS_DOUBLE);
            assertEquals(10*i+2, rawHikeData.getHikePoints().get(i).getPosition().longitude, EPS_DOUBLE);
            assertEquals(10*i+3, rawHikeData.getHikePoints().get(i).getTime().getTime());
            assertEquals(10*i+4, rawHikeData.getHikePoints().get(i).getElevation(), EPS_DOUBLE);
        }
    }

    @Test
    public void testParseFromGPX() throws Exception {
        Calendar cal = Calendar.getInstance();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(PROPER_GPX_ONEHIKE)));
        RawHikeData rawHikeData = RawHikeData.parseFromGPXDocument(doc);

        assertEquals(RawHikeData.HIKE_ID_UNKNOWN, rawHikeData.getHikeId());
        assertEquals(0, rawHikeData.getOwnerId());
        //assertEquals("Rochers de Naye", rawHikeData.getTitle()); TODO uncomment when title is implemented
        assertEquals(2, rawHikeData.getHikePoints().size());
        cal.setTime(rawHikeData.getHikePoints().get(0).getTime());
        assertEquals(46.451290, rawHikeData.getHikePoints().get(0).getPosition().latitude, EPS_DOUBLE);
        assertEquals(6.976647, rawHikeData.getHikePoints().get(0).getPosition().longitude, EPS_DOUBLE);
        assertEquals(2015, cal.get(Calendar.YEAR));
        assertEquals(Calendar.NOVEMBER, cal.get(Calendar.MONTH));
        assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(15, cal.get(Calendar.SECOND));
        assertEquals(1509.0, rawHikeData.getHikePoints().get(0).getElevation(), EPS_DOUBLE);

        cal.setTime(rawHikeData.getHikePoints().get(1).getTime());
        assertEquals(46.451195, rawHikeData.getHikePoints().get(1).getPosition().latitude, EPS_DOUBLE);
        assertEquals(6.976807, rawHikeData.getHikePoints().get(1).getPosition().longitude, EPS_DOUBLE);
        assertEquals(2015, cal.get(Calendar.YEAR));
        assertEquals(55, cal.get(Calendar.SECOND));
        assertEquals(1512.0, rawHikeData.getHikePoints().get(1).getElevation(), EPS_DOUBLE);
    }

}
