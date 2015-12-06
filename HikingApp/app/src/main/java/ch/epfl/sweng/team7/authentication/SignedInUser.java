package ch.epfl.sweng.team7.authentication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates a singleton representation of the user that is signed in
 */
public class SignedInUser {

    private long mId;
    private String mMailAddress;
    private boolean mLoggedIn;
    private String mAuthToken;

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

    public String getMailAddress() {
        return mMailAddress;
    }

    public boolean getLoggedIn() {
        return mLoggedIn;
    }

    public void logout() {
        mLoggedIn = false;
        mId = 0;
        mMailAddress = "";
        mAuthToken = "";
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
            jsonObject.put("token", mAuthToken);
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
        mAuthToken = jsonObject.getString("token");
        mLoggedIn = true;
    }
}
