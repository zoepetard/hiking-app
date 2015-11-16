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

    private long userId;
    private String userName;
    private String mailAddress;


    public RawUserData(long userId, String userName, String mailAddress) {

        // Arguments check
        if (userId < 0 && userId != USER_ID_UNKNOWN) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (userName.length() < USER_NAME_MIN_LENGTH) {
            throw new IllegalArgumentException("User name must be at least two characters");
        }
        if (!mailAddress.contains("@")) {
            throw new IllegalArgumentException("Invalid e-mail address");
        }

        this.userId = userId;
        this.userName = userName;
        this.mailAddress = mailAddress;

    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setUserId(long id) {

        if (userId < 0 && userId != USER_ID_UNKNOWN) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        this.userId = id;
    }

    public void setUserName(String userName) {

        if (userName.length() < 1) {
            throw new IllegalArgumentException("User name must be at least two characters");
        }
        this.userName = userName;
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("user_name", userName);
        jsonObject.put("mail_address", mailAddress);

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
