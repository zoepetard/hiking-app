package ch.epfl.sweng.team7.network;

/**
 * Created by simon on 11/29/15.
 * One vote for the hike rating
 */
public class RatingVote {
    private final long mHikeId;
    private final float mRating;

    public RatingVote(long hikeId, float rating) {
        mHikeId = hikeId;
        mRating = rating;
    }

    public long getHikeId() {
        return mHikeId;
    }

    public float getRating() {
        return mRating;
    }
}
