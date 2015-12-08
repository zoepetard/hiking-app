package ch.epfl.sweng.team7.hikingapp;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import org.junit.Before;

import java.math.BigInteger;

import ch.epfl.sweng.team7.authentication.LoginRequest;
import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.UserData;
import ch.epfl.sweng.team7.mockServer.MockServer;

public class UserDataTest extends ActivityInstrumentationTestCase2<UserDataActivity> {

    private UserData userData;

    public UserDataTest() {
        super(UserDataActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        DataManager.setDatabaseClient(new MockServer());
        DataManager dataManager = DataManager.getInstance();
        userData = dataManager.getUserData(12345);
    }

    public void testCorrectData() throws Exception {
        final TextView name = (TextView) getActivity().findViewById(R.id.user_name);
        final TextView email = (TextView) getActivity().findViewById(R.id.user_email);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                name.setText(userData.getUserName());
                email.setText(userData.getMailAddress());
            }
        });
        assertEquals(name.getText(), "Bort");
        assertEquals(email.getText(), "bort@googlemail.com");
    }
}
