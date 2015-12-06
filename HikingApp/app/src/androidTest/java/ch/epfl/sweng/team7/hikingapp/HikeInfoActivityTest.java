package ch.epfl.sweng.team7.hikingapp;

/**
 * Created by fredrik-eliasson on 08/11/15.
 */

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.espresso.contrib.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DefaultHikeData;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawUserData;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class HikeInfoActivityTest
        extends ActivityInstrumentationTestCase2<HikeInfoActivity> {

    private HikeInfoActivity hikeInfoActivity;
    private static final String PROPER_GPX_ONEHIKE = ""
            + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<gpx creator=\"Wikiloc - http://www.wikiloc.com\" version=\"1.1\"><trk>"
            + "<name>Rochers de Naye</name><cmt></cmt><desc></desc><trkseg>"
            + "<trkpt lat=\"46.451290\" lon=\"6.976647\"><ele>1509.0</ele><time>2015-11-27T15:49:15Z</time></trkpt>"
            + "<trkpt lat=\"46.451195\" lon=\"6.976807\"><ele>1512.0</ele><time>2015-11-27T15:49:55Z</time></trkpt>"
            + "</trkseg></trk></gpx>";


    public HikeInfoActivityTest() {
        super(HikeInfoActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        hikeInfoActivity = getActivity();
    }

    public void testOpenNavDrawer() {
        DrawerActions.openDrawer(R.id.nav_drawer_layout);

    }

    public void testCloseNavDrawer() {

        DrawerActions.openDrawer(R.id.nav_drawer_layout);
        DrawerActions.closeDrawer(R.id.nav_drawer_layout);

    }

    public void testClickItemInNavDrawer() {

        DrawerActions.openDrawer(R.id.nav_drawer_layout);
        onView(withId(R.id.nav_drawer)).perform(click());

    }

    public void testNavDrawerLoadItems() {

        ListView navDrawerListView = (ListView) getActivity().findViewById(R.id.nav_drawer);
        if (navDrawerListView.getAdapter().isEmpty()) {
            fail("ListView Empty");
        } else {
            if (!navDrawerListView.getAdapter().getItem(0).equals("Account")) {
                fail("First item should be Account");
            }

            if (!navDrawerListView.getAdapter().getItem(1).equals("Logout")) {
                fail("Second item should be Logout");
            }
        }
    }


    /**
     * Test if it's possible to display an image in fullscreen
     * and then return from fullscreen
     */
    public void testToggleFullScreen() {

        hikeInfoActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                hikeInfoActivity.getHikeInfoView().toggleFullScreen();

                View infoView = getActivity().findViewById(R.id.info_overview_layout);
                View fullScreenView = getActivity().findViewById(R.id.image_fullscreen_layout);

                if (infoView.getVisibility() == View.VISIBLE) {
                    fail("infoView should be GONE");
                }

                if (fullScreenView.getVisibility() != View.VISIBLE) {
                    fail("fullScreenView should be VISIBLE");
                }

                Button backButton = hikeInfoActivity.getHikeInfoView().getBackButton();
                backButton.callOnClick();

                if (infoView.getVisibility() != View.VISIBLE) {
                    fail("infoView should be VISIBLE");
                }

                if (fullScreenView.getVisibility() == View.VISIBLE) {
                    fail("fullScreenView should be GONE");
                }
            }
        });
    }


    public void testBackFromFullScreenButton() {

        hikeInfoActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HikeInfoView hikeInfoView = hikeInfoActivity.getHikeInfoView();
                hikeInfoView.toggleFullScreen();
            }
        });

        View infoView = getActivity().findViewById(R.id.info_overview_layout);
        View fullScreenView = getActivity().findViewById(R.id.image_fullscreen_layout);

        onView(withId(R.id.back_button_fullscreen_image)).perform(click());
        if (infoView.getVisibility() != View.VISIBLE) {
            fail("infoView should be VISIBLE");
        }

        if (fullScreenView.getVisibility() == View.VISIBLE) {
            fail("fullScreenView should be GONE");
        }
    }


    public void testPostAndShowComments() {
        final EditText commentEditText = (EditText) getActivity().findViewById(R.id.comment_text);
        final Button commentButton = (Button) getActivity().findViewById(R.id.done_edit_comment);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                commentEditText.setText(R.string.test_comment);
                commentButton.performClick();
            }
        });
        TextView idText = (TextView) getActivity().findViewById(R.id.comment_userid);
        assertEquals(idText.getText(), String.valueOf(SignedInUser.getInstance().getId()));
        TextView commentText = (TextView) getActivity().findViewById(R.id.comment_display_text);
        assertEquals(commentText.getText(), getActivity().getResources().getString(R.string.test_comment));
    }

    public void testSaveGPX() throws Exception {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(PROPER_GPX_ONEHIKE)));
        RawHikeData rawHikeData = RawHikeData.parseFromGPXDocument(doc);

        DefaultHikeData displayedHike = new DefaultHikeData(rawHikeData);
        DataManager dataManager = DataManager.getInstance();
        String filePath = dataManager.saveGPX(displayedHike, hikeInfoActivity.getApplicationContext());

        if (filePath == null) {
            fail("File not created");
        }

        File file = new File(filePath);
        Document document = dBuilder.parse(file);

        rawHikeData = RawHikeData.parseFromGPXDocument(document);
        DefaultHikeData defaultHikeData = new DefaultHikeData(rawHikeData);

        assertEquals("Hike title doesn't match", defaultHikeData.getTitle(), displayedHike.getTitle());
        assertEquals("Distances don't match", defaultHikeData.getDistance(), displayedHike.getDistance());
        assertEquals("Date don't match", defaultHikeData.getDate(), defaultHikeData.getDate());

        for (int i = 0; i < defaultHikeData.getHikePoints().size(); i++) {
            HikePoint hikePointOne = defaultHikeData.getHikePoints().get(i);
            HikePoint hikePointTwo = displayedHike.getHikePoints().get(i);

            assertEquals("Latitude doesn't match", hikePointOne.getPosition().latitude, hikePointOne.getPosition().latitude);
            assertEquals("Longitude doesn't match", hikePointOne.getPosition().longitude, hikePointOne.getPosition().longitude);
            assertEquals("Elevation doesn't match", hikePointOne.getElevation(), hikePointTwo.getElevation());
            assertEquals("Timestamp doesn't match", hikePointOne.getTime(), hikePointTwo.getTime());
        }
    }


}