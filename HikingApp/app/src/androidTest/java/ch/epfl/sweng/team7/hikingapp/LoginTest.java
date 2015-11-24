package ch.epfl.sweng.team7.hikingapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class LoginTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void testSignedInUser() {
        SignedInUser user = SignedInUser.getInstance();
        user.init(1, "team7", "team7@gmail.com");

        assertEquals("ID mismatch", user.getId(), 1);
        assertEquals("User name mismatch", user.getUserName(), "team7");
        assertEquals("Email address mismatch", user.getMailAddress(), "team7@gmail.com");

        // test that it's only possible to have one object but multiple references
        SignedInUser newUser = SignedInUser.getInstance();
        newUser.init(2, "7team", "7team@gmail.com");

        assertEquals("Wrong ID", newUser.getId(), 2);
        assertEquals("Wrong user name", newUser.getId(), 2);
        assertEquals("Wrong email address", newUser.getId(), 2);

        // check that old reference is updated
        assertEquals("Wrong ID for old pointer", user.getId(), 2);
        assertEquals("Wrong user name for old pointer", user.getUserName(), "7team");
        assertEquals("Wrong email address for old pointer", user.getMailAddress(), "7team@gmail.com");
    }
}