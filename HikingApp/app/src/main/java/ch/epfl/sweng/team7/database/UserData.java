package ch.epfl.sweng.team7.database;


import java.util.List;

/**
 * Interface for representation of a user
 */

public interface UserData {

    /**
     * @return user id
     */
    long getUserId();

    /**
     * @return user name
     */
    String getUserName();

    void setUserName(String userName);

    /**
     * @return user mail address
     */
    String getMailAddress();

    long getUserProfilePic();

    void setUserProfilePic(long picId);

    /**
     * @return list of hikes
     */
    List<Long> getHikeList();

    /**
     * @return number of hikes
     */
    int getNumberOfHikes();

    /**
     * @param hikeList - list containing id of user's hikes
     */
    void setHikeList(List<Long> hikeList);

}