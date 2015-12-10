package ch.epfl.sweng.team7.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The rating for a hike, storing both the average rating
 * and the rating chosen by the signed in user.
 *
 * Created by simon on 11/29/15.
 */
public class Rating {
    private final static double DEFAULT_AVERAGE = 3;
    private final double mAverage;
    private final int mCount;
    private Float mUserRating;

    public Rating(double average, int count, float userRating) {
        mAverage = average;
        mCount = count;
        // A negative rating denotes "not voted yet"
        if(userRating >= 0) {
            mUserRating = userRating;
        } else {
            mUserRating = null;
        }
    }

    public Rating() {
        mAverage = DEFAULT_AVERAGE;
        mCount = 0;
        mUserRating = null;
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

    /**
     * Update the rating with a vote. This is a shortcut for updating the information
     * while data is sent to the server at the same time.
     */
    public void update(RatingVote vote) {
        mUserRating = vote.getRating();
    }

    public static Rating parseFromJSON(JSONObject jsonObject) throws JSONException {
        return new Rating(
                jsonObject.getDouble("rating"),
                jsonObject.getInt("count"),
                jsonObject.getInt("visitor_rating"));
    }
}
