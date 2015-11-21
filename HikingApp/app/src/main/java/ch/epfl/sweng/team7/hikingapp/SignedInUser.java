package ch.epfl.sweng.team7.hikingapp;

/**
 * Creates a singleton representation of the user that is signed in
 */
public class SignedInUser {

    private long mId;
    private String mUserName;
    private String mMailAddress;

    private SignedInUser() {
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

    public void setUserName(String userName) {
        mUserName = userName;
    }
}
