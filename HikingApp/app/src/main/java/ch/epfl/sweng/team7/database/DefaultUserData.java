package ch.epfl.sweng.team7.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Object containing data about a user
 */
public class DefaultUserData implements UserData{

    private long userId;
    private String userName;
    private String mailAddress;
    private List<Long> hikeList; // id's for user's hike
    private long selectedHikeId;


    public DefaultUserData(RawUserData rawUserData){

        this.userId = rawUserData.getUserId();
        this.userName = rawUserData.getUserName();
        this.mailAddress = rawUserData.getMailAddress();

    }

    /**
     * @return user id
     */
    @Override
    public long getUserId() {
        return 0;
    }

    /**
     * @return user name
     */
    @Override
    public String getUserName() {
        return null;
    }

    /**
     * @return user mail address
     */
    @Override
    public String getMailAddress() {
        return null;
    }

    /**
     * @return list of hikes
     */
    @Override
    public List<HikeData> getHikeList() {
        return null;
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
     * @param newName
     * @set new user name
     */
    @Override
    public void changeUserName(String newName) {
        this.userName = newName;
    }
    
}

