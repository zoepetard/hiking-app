package ch.epfl.sweng.team7.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Object containing data about a user
 */
public class DefaultUserData implements UserData {

    private long mUserId;
    private String mUserName;
    private String mMailAddress;
    private List<Long> mHikeList; // id's for user's hike

    public DefaultUserData(RawUserData rawUserData) {

        mUserId = rawUserData.getUserId();
        mUserName = rawUserData.getUserName();
        mMailAddress = rawUserData.getMailAddress();
        mHikeList = new ArrayList<Long>();

    }

    /**
     * @return user id
     */
    @Override
    public long getUserId() {
        return mUserId;
    }

    /**
     * @return user name
     */
    @Override
    public String getUserName() {
        return mUserName;
    }

    /**
     * @return user mail address
     */
    @Override
    public String getMailAddress() {
        return mMailAddress;
    }

    /**
     * @return list of hikes
     */
    @Override
    public List<Long> getHikeList() {
        return mHikeList;
    }

    /**
     * @return number of hikes
     */
    @Override
    public int getNumberOfHikes() {
        return mHikeList.size();
    }

    /**
     * @param hikeList - list containing id of user's hikes
     */
    @Override
    public void setHikeList(List<Long> hikeList) {
        mHikeList = hikeList;
    }


}

