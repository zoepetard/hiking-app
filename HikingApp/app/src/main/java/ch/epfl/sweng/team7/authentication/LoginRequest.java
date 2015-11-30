package ch.epfl.sweng.team7.authentication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by simon on 11/28/15.
 * A LoginRequest is sent from the frontend to the backend
 * to request the login of a user. It contains the necessary
 * authentication information.
 * If a user account does not exist yet, the backend will
 * create a new account based on the information
 * provided in this request.
 */


public class LoginRequest {
    private final String mMailAddress;
    private final String mUserNameHint;
    private final String mIdToken;

    public LoginRequest(String mailAddress, String userNameHint, String idToken) {
        mMailAddress = mailAddress;
        mUserNameHint = userNameHint;
        mIdToken = idToken;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_name_hint", mUserNameHint);
        jsonObject.put("mail_address", mMailAddress);
        jsonObject.put("id_token", mIdToken);
        return jsonObject;
    }
}
