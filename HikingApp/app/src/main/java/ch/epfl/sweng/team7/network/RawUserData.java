package ch.epfl.sweng.team7.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Stores the user information as it's stored on the server
 */

public class RawUserData {

    public static final long USER_ID_UNKNOWN = -1;
    public static final long USER_NAME_MIN_LENGTH = 2;

    private long mUserId;
    private String mUserName;
    private String mMailAddress;


    public RawUserData(long userId, String userName, String mailAddress) {

        // Check that are arguments are valid, otherwise throw exception
        if (userId < 0 && userId != USER_ID_UNKNOWN) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (userName.length() < USER_NAME_MIN_LENGTH) {
            throw new IllegalArgumentException("User name must be at least two characters");
        }
        if (!mailAddress.contains("@")) {
            throw new IllegalArgumentException("Invalid e-mail address");
        }

        mUserId = userId;
        mUserName = userName;
        mMailAddress = mailAddress;

    }

    public long getUserId() {
        return mUserId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getMailAddress() {
        return mMailAddress;
    }

    public void setUserId(long id) {

        if (mUserId < 0 && mUserId != USER_ID_UNKNOWN) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        mUserId = id;
    }

    public void setUserName(String userName) {

        if (userName.length() < 1) {
            throw new IllegalArgumentException("User name must be at least two characters");
        }
        mUserName = userName;
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", mUserId);
        jsonObject.put("user_name", mUserName);
        jsonObject.put("mail_address", mMailAddress);

        return new JSONObject();
    }

    public static RawUserData parseFromJSON(JSONObject jsonObject) throws JSONException {


        try {
            return new RawUserData(
                    jsonObject.getLong("user_id"),
                    jsonObject.getString("user_name"),
                    jsonObject.getString("mail_address"));

        } catch (NullPointerException e) {
            throw new JSONException("Invalid user data " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException("Invalid user data" + e.getMessage());
        }
    }

    private JSONArray parseHikeList(List<Long> hikeList) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < hikeList.size(); i++) {
            jsonArray.put(hikeList.get(i));
        }
        return jsonArray;
    }

}
