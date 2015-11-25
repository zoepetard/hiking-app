package ch.epfl.sweng.team7.hikingapp;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Creates a singleton representation of the user that is signed in
 */
public class SignedInUser {

    private long mId;
    private String mUserName;
    private String mMailAddress;
    private boolean mLoggedIn;

    private SignedInUser() {
        mLoggedIn = false;
    }

    private static class SignedInUserHolder {
        private static final SignedInUser INSTANCE = new SignedInUser();
    }

    public static SignedInUser getInstance() {
        return SignedInUserHolder.INSTANCE;
    }

    public void init(long id, String userName, String mailAddress) {
        mId = id;
        mUserName = userName;
        mMailAddress = mailAddress;
    }

    public long getId() {
        return mId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getMailAddress() {
        return mMailAddress;
    }

    public boolean getLoggedIn() {
        return mLoggedIn;
    }

    /**
     * Process the JSON data that the server has sent. Currently that data matches
     * the format of RawUserData, but this might still change.
     * @param jsonObject the return JSON of the server login_user request
     */
    public void loginFromJSON(JSONObject jsonObject) throws JSONException {
        RawUserData rawUserData = RawUserData.parseFromJSON(jsonObject);
        if(!mMailAddress.equals(rawUserData.getMailAddress())) {
            throw new JSONException("User email address does not match previous.");
        }
        mUserName = rawUserData.getUserName();
        mId = rawUserData.getUserId();
        mLoggedIn = true;
    }
}
