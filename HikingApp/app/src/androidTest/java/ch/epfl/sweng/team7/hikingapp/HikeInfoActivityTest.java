package ch.epfl.sweng.team7.hikingapp;

/**
 * Created by fredrik-eliasson on 08/11/15.
 */

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.espresso.contrib.*;
import android.view.View;
import android.widget.ListView;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class HikeInfoActivityTest
        extends ActivityInstrumentationTestCase2<HikeInfoActivity> {

    private HikeInfoActivity hikeInfoActivity;

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

    public void testClickItemInNavDrawer(){

        DrawerActions.openDrawer(R.id.nav_drawer_layout);
        onView(withId(R.id.nav_drawer)).perform(click());

    }

    public void testNavDrawerLoadItems(){

        ListView navDrawerListView = (ListView) getActivity().findViewById(R.id.nav_drawer);
        if(navDrawerListView.getAdapter().isEmpty()){
            fail("ListView Empty");
        }
        else
        {
            if(!navDrawerListView.getAdapter().getItem(0).equals("Account")){
                fail("First item should be Account");
            }

            if(!navDrawerListView.getAdapter().getItem(1).equals("Logout")){
                fail("Fourth item should be Logout");
            }
        }
    }


    /** Test if it's possible to display an image in fullscreen
     * and then return from fullscreen
     */
    public void testToggleFullScreen(){

        hikeInfoActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hikeInfoActivity.toggleFullScreen();
                View infoView = getActivity().findViewById(R.id.info_overview_layout);
                View fullScreenView = getActivity().findViewById(R.id.image_fullscreen_layout);

                if(infoView.getVisibility() == View.VISIBLE ){
                    fail("infoView should be GONE");
                }

                if(fullScreenView.getVisibility() != View.VISIBLE ){
                    fail("fullScreenView should be VISIBLE");
                }

                hikeInfoActivity.toggleFullScreen();

                if(infoView.getVisibility() != View.VISIBLE ){
                    fail("infoView should be VISIBLE");
                }

                if(fullScreenView.getVisibility() == View.VISIBLE ){
                    fail("fullScreenView should be GONE");
                }
            }
        });
    }


    public void testBackFromFullScreenButton(){

        hikeInfoActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hikeInfoActivity.toggleFullScreen();
            }
        });

        View infoView = getActivity().findViewById(R.id.info_overview_layout);
        View fullScreenView = getActivity().findViewById(R.id.image_fullscreen_layout);

        onView(withId(R.id.back_button_fullscreen_image)).perform(click());
        if(infoView.getVisibility() != View.VISIBLE ){
            fail("infoView should be VISIBLE");
        }

        if(fullScreenView.getVisibility() == View.VISIBLE ){
            fail("fullScreenView should be GONE");
        }
    }


}