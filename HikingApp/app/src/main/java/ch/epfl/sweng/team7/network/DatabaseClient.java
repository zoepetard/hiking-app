/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on SwEngHomework3 QuizClient class
 */

package ch.epfl.sweng.team7.network;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;


import ch.epfl.sweng.team7.authentication.LoginRequest;


/**
 * A client object to a hikingapp(footpath) server that abstracts the underlying
 * communication protocol and data formats.
 */
public interface DatabaseClient {
    /**
     * Fetch a single hike from the server
     *
     * @param hikeId The numeric ID of one hike in the database
     * @return A {@link RawHikeData} object encapsulating one hike
     * @throws DatabaseClientException in case the hike could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     *                                 or the hikeId did not match a valid hike.
     */

    RawHikeData fetchSingleHike(long hikeId) throws DatabaseClientException;

    /**
     * Fetch multiple hikes from the server
     *
     * @param hikeIds The numeric IDs of multiple hikes in the database
     * @return A list of {@link RawHikeData} objects encapsulating multiple hikes
     * @throws DatabaseClientException in case the hike could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     *                                 or the hikeId did not match a valid hike.
     */
    List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException;

    /**
     * Get all hikes in a rectangular window on the map
     *
     * @param bounds Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException;

    /**
     * Get all hikes of a user
     *
     * @param userId A valid user ID
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    List<Long> getHikeIdsOfUser(long userId) throws DatabaseClientException;

    /**
     * Get all hikes with given keywords
     *
     * @param keywords A string of keywords, separated by spaces. Special characters will be ignored.
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    List<Long> getHikeIdsWithKeywords(String keywords) throws DatabaseClientException;

    /**
     * Post a hike to the database. Returns the database ID
     * that this hike was assigned from the database.
     *
     * @param hike Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    long postHike(RawHikeData hike) throws DatabaseClientException;

    /**
     * Delete a hike from the server. A hike can only be deleted by its owner.
     *
     * @param hikeId - ID of the hike
     * @throws DatabaseClientException if unable to delete user
     */
    void deleteHike(long hikeId) throws DatabaseClientException;

    /**
     * Post user data to the data base
     *
     * @param rawUserData object conatining id,user name and mail address
     * @return user id
     * @throws DatabaseClientException if post is unsuccessful
     */
    long postUserData(RawUserData rawUserData) throws DatabaseClientException;

    /**
     * Fetch data for a user from the server
     *
     * @param userId - id of the user
     * @return RawUserData
     * @throws DatabaseClientException if unable to fetch user data
     */
    RawUserData fetchUserData(long userId) throws DatabaseClientException;

    /**
     * Log user into the server, i.e. get user profile information
     * @param loginRequest
     * @throws DatabaseClientException
     */
    void loginUser(LoginRequest loginRequest) throws DatabaseClientException;

    /**
     * Delete a user from the server. A user can only delete himself.
     *
     * @param userId - ID of the user
     * @throws DatabaseClientException if unable to delete user
     */
    void deleteUser(long userId) throws DatabaseClientException;

<<<<<<< HEAD
    /**
     * TODO DEPRECATED DELETE
     * @param mailAddress - used to query server
     * @return RawUserData - corresponding to user's mail address
     */

    RawUserData fetchUserData(String mailAddress) throws DatabaseClientException;
=======
>>>>>>> Changed methods from type PictureAnnotation to Drawable

    /**
>>>>>>> Rebase to master
     * Get an image from the database
     * @param imageId the database key of the image
     * @return the image
    */

    Drawable getImage(long imageId) throws DatabaseClientException;


    /**
     * Post an image to the database
     * @param drawable an image, here as drawable
     * @return the database key of that image
     * @throws DatabaseClientException
     */
<<<<<<< HEAD
    long postImage(Drawable drawable) throws DatabaseClientException;

=======
    long postPicture (Drawable picture) throws DatabaseClientException;

    Drawable getPicture (long pictureId) throws  DatabaseClientException;
>>>>>>> Changed methods from type PictureAnnotation to Drawable

    /**
     * Delete an image from the database
     * @param imageId the database key of the image
     * @throws DatabaseClientException
     */
    void deleteImage(long imageId) throws DatabaseClientException;

    /**
     * Post a comment to the database
     * @param
     * //TODO(runjie) iss107 add class Comment and pass comment as a parameter
     * @return the database key of that comment
     * @throws DatabaseClientException
     */
    long postComment(RawHikeComment comment) throws DatabaseClientException;

    /**
     * Delete a comment from the database
     * @param commentId the database key of the comment
     * @throws DatabaseClientException
     */
    void deleteComment(long commentId) throws DatabaseClientException;
    

    /**
     * Post a vote about a hike.
     */
    void postVote(RatingVote vote) throws DatabaseClientException;


}
