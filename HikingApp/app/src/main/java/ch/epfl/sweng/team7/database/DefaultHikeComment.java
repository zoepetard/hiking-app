package ch.epfl.sweng.team7.database;

import ch.epfl.sweng.team7.network.RawHikeComment;

public class DefaultHikeComment implements HikeComment {

    private final static String LOG_FLAG = "DB_DefaultHikeComment";

    private final long mCommentId;
    private final long mCommentHikeId;
    private final long mCommentOwnerId;
    private final String mCommentOwnerName;
    private final String mCommentText;
    private final String mCommentDate;

    public DefaultHikeComment(RawHikeComment rawHikeComment) {
        mCommentId = rawHikeComment.getCommentId();
        mCommentHikeId = rawHikeComment.getCommentHikeId();
        mCommentOwnerId = rawHikeComment.getCommentOwnerId();
        mCommentOwnerName = rawHikeComment.getCommentOwnerName();
        mCommentText = rawHikeComment.getCommentText();
        mCommentDate = rawHikeComment.getCommentDate();
    }


    @Override
    public long getCommentId() {
        return mCommentId;
    }

    @Override
    public long getCommentHikeId() {
        return mCommentHikeId;
    }

    @Override
    public long getCommentOwnerId() {
        return mCommentOwnerId;
    }

    @Override
    public String getCommentOwnerName() {
        return mCommentOwnerName;
    }

    @Override
    public String getCommentText() {
        return mCommentText;
    }

    @Override
    public String getCommentDate() {
        return mCommentDate;
    }
}

