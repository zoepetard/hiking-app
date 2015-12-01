package ch.epfl.sweng.team7.mockServer;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.authentication.LoginRequest;
import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DefaultHikeData;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.HikeParseException;
import ch.epfl.sweng.team7.network.RatingVote;
import ch.epfl.sweng.team7.network.RawHikeComment;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;
import ch.epfl.sweng.team7.network.RawUserData;

/**
 * Created by pablo on 6/11/15.
 */
public class MockServer implements DatabaseClient {

    private static final String PROPER_JSON_ONEHIKE = "{\n"
            + "  \"hike_id\": 1,\n"
            + "  \"owner_id\": 48,\n"
            + "  \"date\": 123201,\n"
            + "  \"hike_data\": [\n"
            + "    [0.0, 0.0, 123201, 0.5],\n"
            + "    [0.1, 0.1, 123202, 0.6],\n"
            + "    [0.2, 0.0, 123203, 0.7],\n"
            + "    [0.3,89.9, 123204, 0.8],\n"
            + "    [0.4, 0.0, 123205, 0.9]\n"
            + "  ],\n"
            + "  \"comments\": [\n"
            + "  ],\n"
            + "  \"title\": \"test\"\n"
            + "}\n";
    //Same as DefaultLocalCache
    private final int HIKES_CACHE_MAX_SIZE = 100;
    private final HashMap<Long, RawHikeData> mHikeDataBase = new FixedSizeHashMap<>(HIKES_CACHE_MAX_SIZE);
    private int mAssignedHikeID = 10;
    private final HashMap<Long, RawHikeComment> mHikeCommentDataBase = new FixedSizeHashMap<>(HIKES_CACHE_MAX_SIZE);
    private int mAssignedCommentID = 10;

    private List<RawUserData> mUsers;


    public MockServer() throws DatabaseClientException {
        createMockHikeOne();
        mUsers = new ArrayList<>();
    }

    /**
     * Method to fetch a single RawHikeData with the given hikeID
     *
     * @param hikeId The numeric ID of one hike in the database
     * @throws DatabaseClientException
     */
    @Override
    public RawHikeData fetchSingleHike(long hikeId) throws DatabaseClientException {
        //Hike 1 should always exists
        if (hasHike(hikeId)) {
            return getHike(hikeId);
        } else {
            throw new DatabaseClientException("No hike on the server with ID " + hikeId);
        }
    }

    /**
     * Return a list of of RawHikeData with the given hikeIds
     *
     * @param hikeIds The numeric IDs of multiple hikes in the database
     * @throws DatabaseClientException
     */
    @Override
    public List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException {
        List<RawHikeData> listRawHikeData = new ArrayList<>();
        for (Long hikeId : hikeIds) {
            listRawHikeData.add(fetchSingleHike(hikeId));
        }
        return listRawHikeData;
    }

    /**
     * Return the hikeIds of hikes that are in the given window
     *
     * @param bounds Boundaries (window) of the
     * @return
     * @throws DatabaseClientException
     */
    @Override
    public List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException {
        List<Long> hikeIdsInWindow = new ArrayList<>();
        for (RawHikeData rawHikeData : mHikeDataBase.values()) {
            for (RawHikePoint rawHikePoint : rawHikeData.getHikePoints()) {
                if (bounds.contains(rawHikePoint.getPosition())) {
                    hikeIdsInWindow.add(rawHikeData.getHikeId());
                    break;
                }
            }
        }
        return hikeIdsInWindow;
    }

    /**
     * Method to post a hike in the database. The database assigns a hike ID and returns that.
     *
     * @param hike to post. ID is ignored, because hike will be assigned a new ID.
     * @throws DatabaseClientException
     */
    @Override
    public long postHike(RawHikeData hike) throws DatabaseClientException {
        long hikeId = hike.getHikeId();
        if (hikeId > 0) {
            if (!hasHike(hikeId)) {
                throw new DatabaseClientException("Setting Hike that's not there.");
            }
        } else {
            hikeId = mAssignedHikeID;
            mAssignedHikeID++;
            hike.setHikeId(hikeId);
        }
        putHike(hike);
        return hikeId;
    }

    /**
     * Delete a hike from the server. A hike can only be deleted by its owner.
     *
     * @param hikeId - ID of the hike
     * @throws DatabaseClientException if unable to delete user
     */
    public void deleteHike(long hikeId) throws DatabaseClientException {
        mHikeDataBase.remove(hikeId);
    }

    /**
     * Post user data to the data base
     *
     * @param rawUserData object conatining id,user name and mail address
     * @return user id
     * @throws DatabaseClientException if post is unsuccessful
     */
    @Override
    public long postUserData(RawUserData rawUserData) throws DatabaseClientException {
        // Positive user ID means the user is in the database
        if (rawUserData.getUserId() >= 0) {
            for (int i = 0; i < mUsers.size(); ++i) {
                if (mUsers.get(i).getUserId() == rawUserData.getUserId()) {
                    mUsers.set(i, rawUserData);
                    return i;
                }
            }
            throw new DatabaseClientException("User to update not found in MockServer.");
        } else {
            long newUserId = mUsers.size();
            rawUserData.setUserId(newUserId);
            mUsers.add(rawUserData);
            return newUserId;
        }
    }

    /**
     * Fetch data for a user from the server
     *
     * @param userId - id of the user
     * @return RawUserData
     * @throws DatabaseClientException if unable to fetch user data
     */
    @Override
    public RawUserData fetchUserData(long userId) throws DatabaseClientException {
        for (RawUserData rawUserData : mUsers) {
            if (rawUserData.getUserId() == userId) {
                return rawUserData;
            }
        }
        throw new DatabaseClientException("User to fetch not found in MockServer.");
    }

    /**
     * Delete a user from the server. A user can only delete himself.
     *
     * @param userId - ID of the user
     * @throws DatabaseClientException if unable to delete user
     */
    public void deleteUser(long userId) throws DatabaseClientException {
        for (int i = 0; i < mUsers.size(); ++i) {
            if (mUsers.get(i).getUserId() == userId) {
                mUsers.remove(i);
                return;
            }
        }
        throw new DatabaseClientException("User to delete not found in MockServer.");
    }

    /**
     * Log user into the server, i.e. get user profile information
     *
     * @param loginRequest
     * @throws DatabaseClientException
     */
    public void loginUser(LoginRequest loginRequest) throws DatabaseClientException {
        SignedInUser signedInUser = SignedInUser.getInstance();
        for (RawUserData rawUserData : mUsers) {
            try {
                if (rawUserData.getMailAddress().equals(loginRequest.toJSON().getString("mail_address"))) {
                    signedInUser.loginFromJSON(rawUserData.toJSON());
                    return;
                }
            } catch (JSONException e) {
                throw new DatabaseClientException(e);
            }
        }
        throw new DatabaseClientException("User to fetch not found in MockServer.");
    }

    /**
     * Get an image from the database
     *
     * @param imageId the database key of the image
     * @return the image
     * @throws DatabaseClientException
     */
    public Drawable getImage(long imageId) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented.");
    }

    /**
     * Post an image to the database
     *
     * @param drawable an image, here as drawable
     * @param imageId  the ID of the image if it should be changed
     * @return the database key of that image
     * @throws DatabaseClientException
     */
    public long postImage(Drawable drawable, long imageId) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented.");
    }

    /**
     * Post an image to the database
     *
     * @param drawable an image, here as drawable
     * @return the database key of that image
     * @throws DatabaseClientException
     */
    public long postImage(Drawable drawable) throws DatabaseClientException {
        return postImage(drawable, -1);
    }

    /**
     * Delete an image from the database
     *
     * @param imageId the database key of the image
     * @throws DatabaseClientException
     */
    public void deleteImage(long imageId) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented.");
    }

    /**
     * Post a comment to the database
     * @param comment the comment to be posted
     * @return the database key of that comment
     * @throws DatabaseClientException
     */
    public long postComment(RawHikeComment comment) throws DatabaseClientException {
        long commentId = comment.getCommentId();
        if (commentId > 0) {
            if (!hasComment(commentId)) {
                throw new DatabaseClientException("Setting Comment that's not there.");
            }
        } else {
            commentId = mAssignedCommentID;
            mAssignedCommentID++;
            comment.setCommentId(commentId);
        }
        putComment(comment);
        return commentId;
    }

    /**
     * Delete a comment from the database
     *
     * @param commentId the database key of the comment
     * @throws DatabaseClientException
     */
    public void deleteComment(long commentId) throws DatabaseClientException {
        mHikeCommentDataBase.remove(commentId);
    }

    private void putComment(RawHikeComment rawHikeComment) throws DatabaseClientException {
        if (rawHikeComment != null) {
            mHikeCommentDataBase.put(rawHikeComment.getCommentId(), rawHikeComment);
        }
    }

    /**
     * Post a vote about a hike.
     */
    public void postVote(RatingVote vote) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented.");
    }

    /**
     * Search for hikes
     *
     * @param query , search string
     * @return list of hikedata
     */
    @Override
    public List<HikeData> searchHike(String query) throws DatabaseClientException {

        List<HikeData> hikeDataList = new ArrayList<>();

        for (Map.Entry<Long, RawHikeData> entry : mHikeDataBase.entrySet()) {
            RawHikeData tempHike = entry.getValue();
            if (tempHike.getTitle().contains(query)) {
                hikeDataList.add(new DefaultHikeData(tempHike));
            }
        }

        return hikeDataList;
    }


    // Internal database management functions
    public boolean hasHike(long hikeId) {
        return mHikeDataBase.containsKey(hikeId);
    }

    // Internal database management functions
    public boolean hasComment(long commentId) {
        return mHikeCommentDataBase.containsKey(commentId);
    }

    public RawHikeData getHike(long hikeId) {
        return mHikeDataBase.get(hikeId);
    }

    private void putHike(RawHikeData rawHikeData) throws DatabaseClientException {
        if (rawHikeData != null) {
            if (rawHikeData.getHikeId() == 3) {
                throw new DatabaseClientException("The Mock server cannot have a hike 3.");
            }
            mHikeDataBase.put(rawHikeData.getHikeId(), rawHikeData);
        }
    }

    private static class FixedSizeHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int MAX_ENTRIES;

        FixedSizeHashMap(int maxEntries) {
            super(16 /*initial size*/, 0.75f /*initial load factor*/, true /*update on access*/);
            MAX_ENTRIES = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }

    /**
     * Create mock hike number 1 (should always exist).
     */
    private void createMockHikeOne() throws DatabaseClientException {
        try {
            RawHikeData rawHikeData = createHikeData();
            mHikeDataBase.put(rawHikeData.getHikeId(), rawHikeData);
        } catch (HikeParseException e) {
            throw new DatabaseClientException(e);
        }
    }

    private static RawHikeData createHikeData() throws HikeParseException {
        try {
            return RawHikeData.parseFromJSON(new JSONObject(PROPER_JSON_ONEHIKE));
        } catch (JSONException e) {
            throw new HikeParseException(e);
        }
    }


}
