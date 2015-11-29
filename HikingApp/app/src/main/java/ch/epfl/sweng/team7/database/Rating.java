package ch.epfl.sweng.team7.database;

/**
 * Created by simon on 11/29/15.
 */
public class Rating {
    private final double mAverage;
    private final int mCount;
    private final int mUserRating;

    public Rating(double average, int count, int userRating) {
        mAverage = average;
        mCount = count;
        mUserRating = userRating;
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

    /**
     * Return whether the user has already voted
     */
    public boolean userHasVoted() {
        return mUserRating >= 0;
    }
}
