package ch.epfl.sweng.team7.database;

import org.junit.Before;
import org.junit.Test;


import ch.epfl.sweng.team7.network.RawUserData;

import static org.junit.Assert.assertEquals;

/**
 * Tests the DefaultUserData class
 */
public class DefaultUserDataTest {

    private final int mUserId = 1;
    private final String mUserName = "Bert";
    private final String mMailAddress = "bert@gmail.com";
    private DefaultUserData mDefaultUserData;

    @Before
    public void setUp() {
        RawUserData rawUserData = new RawUserData(mUserId, mUserName, mMailAddress, -1);
        mDefaultUserData = new DefaultUserData(rawUserData);
    }

    @Test
    public void testUserIdMatch() {
        assertEquals("Id values don't match", mUserId, mDefaultUserData.getUserId());
    }

    @Test
    public void testUserNameMatch() {
        assertEquals("User names don't match", mUserName, mDefaultUserData.getUserName());
    }

    @Test
    public void testMailAddressMatch() {
        assertEquals("Mail addresses don't match", mMailAddress, mDefaultUserData.getMailAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUserNameLength() {
        RawUserData rawUserData = new RawUserData(2, "A", "a@gmail.com", -1);
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidMailAddress() {
        RawUserData rawUserData = new RawUserData(3, "Bert", "gmail.com", -1);
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositiveId() {
        RawUserData rawUserData = new RawUserData(-2, "Bert", "bert@gmail.com", -1);
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test
    public void testIdUnknown() {
        RawUserData rawUserData = new RawUserData(-1, "Bert", "bert@gmail.com", -1);
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
        assertEquals("Unknown User Id", -1, defaultUserData.getUserId());
    }
}
