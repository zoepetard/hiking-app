package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ChangeNicknameTest {
    @Rule
    public ActivityTestRule<ChangeNicknameActivity> mActivityRule = new ActivityTestRule<>(
            ChangeNicknameActivity.class);

    @Test
    public void testRegularChangeNickname() {
        onView(withId(R.id.edit_nickname)).perform(typeText("NewNickname"), closeSoftKeyboard());
        onView(withId(R.id.edit_nickname)).check(matches(withText("NewNickname")));
        // check launching MapActivity (segfault with perform(click())
    }
}
