/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on HikingApp ClientSide and
 * SwEngHomework3 NetworkQuizClient class
 */

package ch.epfl.sweng.team7.network;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ch.epfl.sweng.team7.database.TrackData;


/**
 * Class to get and post tracks in the server
 */
public class NetworkDatabaseClient implements DatabaseClient {

    private final static int CONNECT_TIMEOUT = 1000;
    private final static String JSON_CONTENT = "application/json";
    private final static String ENCODING = "charset=utf-8";

    private final String mServerUrl;
    private final NetworkProvider mNetworkProvider;


    public NetworkDatabaseClient(String serverUrl, NetworkProvider networkProvider) {
        mServerUrl = serverUrl;
        mNetworkProvider = networkProvider;
    }


    /**
     * Fetch a single track from the server
     * @param trackId The numeric ID of one track in the database
     * @return A {@link ch.epfl.sweng.team7.database.TrackData} object encapsulating one track
     * @throws DatabaseClientException in case the track could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the trackId did not match a valid track.
     */
    public ch.epfl.sweng.team7.database.TrackData fetchSingleTrack(long trackId) throws DatabaseClientException {
        try {
            URL url = new URL(mServerUrl + "/get_track/");
            HttpURLConnection conn = openConnection(url, "GET");
            String stringTrackData = fetchResponse(conn);
            JSONObject jsonTrackData = new JSONObject(stringTrackData);
            return TrackData.parseFromJSON(jsonTrackData);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Fetch multiple tracks from the server
     * @param trackIds The numeric IDs of multiple tracks in the database
     * @return A list of {@link ch.epfl.sweng.team7.database.TrackData} objects encapsulating multiple tracks
     * @throws DatabaseClientException in case the track could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the trackId did not match a valid track.
     */
    public List<ch.epfl.sweng.team7.database.TrackData> fetchMultipleTracks(List<Integer> trackIds) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }

    /**
     * Get all tracks in a rectangular window on the map
     * @param bounds Boundaries (window) of the
     * @return A list of track IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    public List<Integer> getAllTracksInBounds(LatLngBounds bounds) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }

    /**
     * Post a track to the database. Returns the database ID
     * that this track was assigned from the database.
     * @param track Boundaries (window) of the
     * @return A list of track IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    public int postTrack(ch.epfl.sweng.team7.database.TrackData track) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }
    
    /**
     * Method to set the properties of the connection to the server
     * @param url the server url
     * @param method "GET" or "POST"
     * @return a valid HttpConnection
     * @throws IOException
     */
    private HttpURLConnection openConnection(URL url, String method) throws IOException {
        HttpURLConnection conn = mNetworkProvider.getConnection(url);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestProperty("Content-Type", JSON_CONTENT + ";" + ENCODING);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        conn.connect();
        return conn;
    }
    
    /**
     * Method to check the response code of the server and return the message that the server sends
     * @param conn an open HttpURLConnection
     * @return the string that was read from the connection
     * @throws IOException
     */
    private String fetchResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        StringBuilder result = new StringBuilder();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Unexpected HTTP Response Code: "+responseCode);
        }

        String contentType = conn.getContentType();
        if (contentType == null) {
            throw new IOException("HTTP content type unset");
        } else if(contentType.compareTo(JSON_CONTENT) != 0) {
            throw new IOException("Invalid HTTP content type: " + contentType);
        }

        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line + "\n");
        }

        conn.disconnect();
        return result.toString();
    }
    
}