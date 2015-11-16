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
    public void setUp(){
        RawUserData rawUserData = new RawUserData(userId,userName,mailAddress);
        defaultUserData = new DefaultUserData(rawUserData);
    }

    @Test
    public void testUserIdMatch(){
        assertEquals("Id values don't match", userId, defaultUserData.getUserId());
    }

    @Test
    public void testUserNameMatch(){
        assertEquals("User names don't match",userName,defaultUserData.getUserName());

    }

    @Test
    public void testMailAddressMatch(){
        assertEquals("Mail addresses don't match", mailAddress, defaultUserData.getMailAddress());
                
    }



}
