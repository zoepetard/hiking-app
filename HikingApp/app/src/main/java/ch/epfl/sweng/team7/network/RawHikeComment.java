package ch.epfl.sweng.team7.network;

import org.json.JSONException;
import org.json.JSONObject;

public class RawHikeComment {
    private final static String LOG_FLAG = "Network_RawHikeComment";
    public static final long COMMENT_ID_UNKNOWN = -1;

    private long mCommentId;
    private long mCommentHikeId;
    private long mCommentOwnerId;
    private String mCommentText;

    public RawHikeComment(long commentId, long commentHikeId, long commentOwnerId,
                          String commentText) {
        mCommentId = commentId;
        mCommentHikeId = commentHikeId;
        mCommentOwnerId = commentOwnerId;
        mCommentText = commentText;
    }

    public long getCommentId() {
        return mCommentId;
    }

    public long getCommentHikeId() {
        return mCommentHikeId;
    }

    public long getCommentOwnerId() {
        return mCommentOwnerId;
    }

    public String getCommentText() {
        return mCommentText;
    }

    public void setCommentId(long id) {
        if (mCommentId < 0 && mCommentId != COMMENT_ID_UNKNOWN) {
            throw new IllegalArgumentException("Comment ID must be positive");
        }
        mCommentId = id;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comment_id", mCommentId);
        jsonObject.put("hike_id", mCommentHikeId);
        jsonObject.put("user_id", mCommentOwnerId);
        jsonObject.put("comment_text", mCommentText);
        return jsonObject;
    }

    public static RawHikeComment parseFromJSON(JSONObject jsonObject) throws JSONException {
        return new RawHikeComment(
                jsonObject.getLong("comment_id"),
                jsonObject.getLong("hike_id"),
                jsonObject.getLong("user_id"),
                jsonObject.getString("comment_text"));
    }
}

