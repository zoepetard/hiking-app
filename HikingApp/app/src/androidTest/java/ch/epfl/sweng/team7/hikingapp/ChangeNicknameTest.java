package ch.epfl.sweng.team7.hikingapp;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ChangeNicknameTest {
    @Rule
    public ActivityTestRule<ChangeNicknameActivity> mActivityRule = new ActivityTestRule<>(
            ChangeNicknameActivity.class);

    @Test
    public void testRegularChangeNickname() {
        Instrumentation.ActivityMonitor mapActivityMonitor =
                getInstrumentation().addMonitor(MapActivity.class.getName(),
                        null, false);
        onView(withId(R.id.edit_nickname)).perform(typeText("newNickname"));
        onView(withId(R.id.done_edit_nickname)).perform(click());
        MapActivity mapActivity = (MapActivity)
                mapActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("MapActivity is null", mapActivity);
        assertEquals("Monitor for MapActivity has not been called",
                1, mapActivityMonitor.getHits());
        assertEquals("Activity is of wrong type",
                MapActivity.class, mapActivity.getClass());
        getInstrumentation().removeMonitor(mapActivityMonitor);
    }
}
