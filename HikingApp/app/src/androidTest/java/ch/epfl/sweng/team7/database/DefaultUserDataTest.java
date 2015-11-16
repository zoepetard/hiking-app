package ch.epfl.sweng.team7.database;

import org.junit.Before;
import org.junit.Test;


import ch.epfl.sweng.team7.network.RawUserData;

import static org.junit.Assert.assertEquals;

/**
 * Tests the DefaultUserData class
 */
public class DefaultUserDataTest {

    private int userId = 1;
    private String userName = "Bert";
    private String mailAddress = "bert@gmail.com";
    private DefaultUserData defaultUserData;

    @Before
    public void setUp() {
        RawUserData rawUserData = new RawUserData(userId, userName, mailAddress);
        defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test
    public void testUserIdMatch() {
        assertEquals("Id values don't match", userId, defaultUserData.getUserId());
    }

    @Test
    public void testUserNameMatch() {
        assertEquals("User names don't match", userName, defaultUserData.getUserName());
    }

    @Test
    public void testMailAddressMatch() {
        assertEquals("Mail addresses don't match", mailAddress, defaultUserData.getMailAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUserNameLength() {
        RawUserData rawUserData = new RawUserData(2, "A", "a@gmail.com");
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidMailAddress() {
        RawUserData rawUserData = new RawUserData(3, "Bert", "gmail.com");
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositiveId() {
        RawUserData rawUserData = new RawUserData(-2, "Bert", "bert@gmail.com");
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test
    public void testIdUnknown() {
        RawUserData rawUserData = new RawUserData(-1, "Bert", "bert@gmail.com");
        DefaultUserData defaultUserData = new DefaultUserData(rawUserData);
        assertEquals("Unknown User Id", -1, defaultUserData.getUserId());
    }
}
