package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class LoginTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void testBeforeLogin() {
        onView(withId(R.id.status)).check(matches(withText("Signed out")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.change_display_name_and_goto_map)).check(matches(not(isDisplayed())));
        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_and_disconnect)).check(matches(not(isDisplayed())));
    }
}
