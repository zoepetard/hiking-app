package ch.epfl.sweng.team7.authentication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates a singleton representation of the user that is signed in
 */
public class SignedInUser {

    private long mId;
    private String mName;
    private String mMailAddress;
    private boolean mLoggedIn;
    private String mAuthToken;
    private long mProfilePicId;

    private SignedInUser() {
        mLoggedIn = false;
    }

    private static class SignedInUserHolder {
        private static final SignedInUser INSTANCE = new SignedInUser();
    }

    public static SignedInUser getInstance() {
        return SignedInUserHolder.INSTANCE;
    }

    public void init(long id, String mailAddress) {
        mId = id;
        mMailAddress = mailAddress;
    }

    public long getId() {
        return mId;
    }

    public String getUserName() {
        return mName;
    }

    public String getMailAddress() {
        return mMailAddress;
    }

    public boolean getLoggedIn() {
        return mLoggedIn;
    }

    public long getProfilePicId() {
        return mProfilePicId;
    }

    public void logout() {
        mLoggedIn = false;
        mId = 0;
        mMailAddress = "";
        mAuthToken = "";
        mProfilePicId = 0;
    }

    /**
     * Build the authentication header that is sent with every database request
     */
    public JSONObject buildAuthHeader() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logged_in", mLoggedIn);
        if(mLoggedIn) {
            jsonObject.put("mail_address", mMailAddress);
            jsonObject.put("user_id", mId);
            jsonObject.put("user_name", mName);
            jsonObject.put("token", mAuthToken);
            jsonObject.put("profile_image_id", mProfilePicId);
        }
        return jsonObject;
    }

    /**
     * Process the JSON data that the server has sent. Currently that data matches
     * the format of RawUserData, but this might still change.
     * @param jsonObject the return JSON of the server login_user request
     */
    public void loginFromJSON(JSONObject jsonObject) throws JSONException {
        mMailAddress = jsonObject.getString("mail_address");
        mId = jsonObject.getLong("user_id");
        mName = jsonObject.getString("user_name");
        mAuthToken = jsonObject.getString("token");
        mProfilePicId = jsonObject.getLong("profile_image_id");
        mLoggedIn = true;
    }
}
