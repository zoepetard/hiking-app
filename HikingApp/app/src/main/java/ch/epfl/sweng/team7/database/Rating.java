package ch.epfl.sweng.team7.database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by simon on 11/29/15.
 */
public class Rating {
    private final double mAverage;
    private final int mCount;
    private final Integer mUserRating;

    public Rating(double average, int count, int userRating) {
        mAverage = average;
        mCount = count;
        // A negative rating denotes "not voted yet"
        if(userRating >= 0) {
            mUserRating = new Integer(userRating);
        } else {
            mUserRating = null;
        }
    }

    public boolean userHasVoted() {
        return (mUserRating != null);
    }

    /**
     * Get the rating value that should be displayed to the GUI
     */
    public double getDisplayRating() {
        if(userHasVoted()) {
            return mUserRating;
        } else {
            return mAverage;
        }
    }

    public int getVoteCount() {
        return mCount;
    }

    public static Rating parseFromJSON(JSONObject jsonObject) throws JSONException {
        return new Rating(
                jsonObject.getDouble("rating"),
                jsonObject.getInt("count"),
                jsonObject.getInt("visitor_rating"));
    }
}
