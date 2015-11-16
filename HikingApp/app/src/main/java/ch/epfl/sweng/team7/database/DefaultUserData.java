package ch.epfl.sweng.team7.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Object containing data about a user
 */
public class DefaultUserData implements UserData {

    private long userId;
    private String userName;
    private String mailAddress;
    private List<Long> hikeList; // id's for user's hike
    private long selectedHikeId = -1;

    public DefaultUserData(RawUserData rawUserData) {

        this.userId = rawUserData.getUserId();
        this.userName = rawUserData.getUserName();
        this.mailAddress = rawUserData.getMailAddress();

    }

    /**
     * @return user id
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * @return user name
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * @return user mail address
     */
    @Override
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @return list of hikes
     */
    @Override
    public List<Long> getHikeList() {
        return hikeList;
    }

    /**
     * @return number of hikes
     */
    @Override
    public int getNumberOfHikes() {
        return hikeList.size();
    }

    /**
     * @return id for currently selected hike for user
     */
    @Override
    public long getSelectedHikeId() {
        return selectedHikeId;
    }

    /**
     * Changes the user name
     *
     * @param newName the new user name
     */
    @Override
    public void changeUserName(String newName) {
        this.userName = newName;
    }

    /**
     * @param hikeList - list containing id of user's hikes
     */
    @Override
    public void setHikeList(List<Long> hikeList) {
        this.hikeList = hikeList;
    }

    /**
     * Updates user's selected hike
     *
     * @param selectedHikeId - id of selected hike
     */
    @Override
    public void setSelectedHikeId(long selectedHikeId) {
        this.selectedHikeId = selectedHikeId;
    }
}

