package ch.epfl.sweng.team7.database;

public interface HikeComment {

    /**
     * @return the comment ID.
     */
    long getCommentId();

    /**
     * @return the hike ID.
     */
    long getCommentHikeId();

    /**
     * @return the owner ID.
     */
    long getCommentOwnerId();

    String getCommentText();
}
