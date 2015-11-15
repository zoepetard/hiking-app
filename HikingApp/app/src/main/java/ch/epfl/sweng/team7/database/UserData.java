package ch.epfl.sweng.team7.database;


import java.util.List;

/** Interface for representation of a user*/

public interface UserData {

    /** @return user id */
    long getUserId();

    /** @return user name */
    String getUserName();

    /** @return user mail address */
    String getMailAddress();

    /** @return list of hikes */
    List<HikeData> getHikeList();

    /** @return number of hikes */
    int getNumberOfHikes();

    /** @return id for currently selected hike for user */
    long getSelectedHikeId();

    /** @set new user name */
    void changeUserName(String newName);


}