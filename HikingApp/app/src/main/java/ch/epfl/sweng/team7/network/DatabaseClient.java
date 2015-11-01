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
 *
 */
public interface DatabaseClient {
    /**
     * Fetch a single track from the server
     * @param trackId The numeric ID of one track in the database
     * @return A {@link ch.epfl.sweng.team7.database.TrackData} object encapsulating one track
     * @throws DatabaseClientException in case the track could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the trackId did not match a valid track.
     */
    ch.epfl.sweng.team7.database.TrackData fetchSingleTrack(int trackId) throws DatabaseClientException;

    /**
     * Fetch multiple tracks from the server
     * @param trackIds The numeric IDs of multiple tracks in the database
     * @return A list of {@link ch.epfl.sweng.team7.database.TrackData} objects encapsulating multiple tracks
     * @throws DatabaseClientException in case the track could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the trackId did not match a valid track.
     */
    List<ch.epfl.sweng.team7.database.TrackData> fetchMultipleTracks(List<Integer> trackIds) throws DatabaseClientException;

    /**
     * Get all tracks in a rectangular window on the map
     * @param bounds Boundaries (window) of the
     * @return A list of track IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    List<Integer> getAllTracksInBounds(LatLngBounds bounds) throws DatabaseClientException;

    /**
     * Post a track to the database. Returns the database ID
     * that this track was assigned from the database.
     * @param track Boundaries (window) of the
     * @return A list of track IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    int postTrack(ch.epfl.sweng.team7.database.TrackData track) throws DatabaseClientException;
}
