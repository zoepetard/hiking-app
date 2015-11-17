package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class UserDataTest {
    @Rule
    public ActivityTestRule<UserDataActivity> mActivityRule = new ActivityTestRule<>(
            UserDataActivity.class);

    @Test
    public void testDisplayUserData() {
        // test if it's real data stored in the local cache after
        // issue #56 is in master for user with this user_id
        onView(withId(R.id.user_name)).check(matches(withText("Team 7")));
        onView(withId(R.id.user_email)).check(matches(withText("team7@epfl.ch")));
        onView(withId(R.id.num_hikes)).check(matches(withText("Number of hikes: 100")));
    }
}
