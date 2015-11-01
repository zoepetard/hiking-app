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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


/**
 * Class to get and post tracks in the server
 * TODO communicate: renamed from ClientSide
 */
public class NetworkDatabaseClient implements DatabaseClient {
    
    int CONNECT_TIMEOUT = 1000;
    String JSON_CONTENT = "application/json";
    String WEB = "http://localhost:8080";
    String ENCODING = ";charset=utf-8";
    String json = "";
    JSONObject jObjTrack = null;
    HttpURLConnection conn;

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
    public ch.epfl.sweng.team7.database.TrackData fetchSingleTrack(int trackId) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
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
     * Method to post a track in json format to the server
     * @param track
     * @return the response from the server
     * @throws IOException
     */
    private String postTrack(JSONObject track) throws IOException {
        
        URL url = new URL(WEB);
        String message = track.toString();
        //Try to connect to the server
        HttpURLConnection conn = null;
        conn = connProperties(url, conn, "POST");
        
        
        
        OutputStreamWriter writer = null;
        
        //send the track trhu the urlconnection
        try {
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        
        return checkResponseCode(conn);
    }
    
    
    /**
     * Method to request some track from the server
     * @param trackId
     * @return
     * @throws IOException
     */
    private JSONObject getTrack(int trackId) throws IOException {
        String trackAtServer = "?" + Integer.toString(trackId);
        
        URL url = new URL(WEB+trackAtServer);
        //Try to connect to the server
        HttpURLConnection conn = null;
        conn = connProperties(url, conn, "GET");
        
        //Get the response code and the response from the server
        json = checkResponseCode(conn);
        
        //create a Track from the response of the server
        try {
            jObjTrack  = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObjTrack;
        
    }
    
    /**
     * Method to set the properties of the connection to the server
     * @param url
     * @param conn
     * @return
     */
    private HttpURLConnection connProperties(URL url, HttpURLConnection conn, String method){
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestProperty("Content-Type", JSON_CONTENT + ENCODING);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    /**
     * Method to check the response code of the server and return the message that the server sends
     * @param conn
     * @return
     * @throws IOException
     */
    private String checkResponseCode(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        StringBuilder result = new StringBuilder();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            InputStream input = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line + "\n");
            }
        }else System.out.println("Error en response code GET");
        
        conn.disconnect();
        return result.toString();
    }
    
}