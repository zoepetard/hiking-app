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

    private long mUserId;
    private String mUserName;
    private String mMailAddress;
    private List<Long> mHikeList; // id's for user's hike
    private long mSelectedHikeId = -1;

    public DefaultUserData(RawUserData rawUserData) {

        mUserId = rawUserData.getUserId();
        mUserName = rawUserData.getUserName();
        mMailAddress = rawUserData.getMailAddress();

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
     * @return id for currently selected hike for user
     */
    @Override
    public long getSelectedHikeId() {
        return mSelectedHikeId;
    }

    /**
     * Changes the user name
     *
     * @param newName the new user name
     */
    @Override
    public void changeUserName(String newName) {
        mUserName = newName;
    }

    /**
     * @param hikeList - list containing id of user's hikes
     */
    @Override
    public void setHikeList(List<Long> hikeList) {
        mHikeList = hikeList;
    }

    /**
     * Updates user's selected hike
     *
     * @param selectedHikeId - id of selected hike
     */
    @Override
    public void setSelectedHikeId(long selectedHikeId) {
        mSelectedHikeId = selectedHikeId;
    }
}

