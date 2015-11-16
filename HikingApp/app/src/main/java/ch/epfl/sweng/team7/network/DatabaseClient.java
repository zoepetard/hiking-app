/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on SwEngHomework3 QuizClient class
 */

package ch.epfl.sweng.team7.network;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

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
     * retrieved for any reason external to the application (network failure, etc.)
     * or the hikeId did not match a valid hike.
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
     * @param mailAddress - mail address of the user
     * @return RawUserData
     * @throws DatabaseClientException if unable to fetch user data
     */
    RawUserData fetchUserData(String mailAddress) throws DatabaseClientException;


}
