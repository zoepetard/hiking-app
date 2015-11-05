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


/**
 * Class to get and post hikes in the server
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
     * Fetch a single hike from the server
     * @param hikeId The numeric ID of one hike in the database
     * @return A {@link RawHikeData} object encapsulating one hike
     * @throws DatabaseClientException in case the hike could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the hikeId did not match a valid hike.
     */
    public RawHikeData fetchSingleHike(long hikeId) throws DatabaseClientException {
        try {
            URL url = new URL(mServerUrl + "/get_hike/");
            HttpURLConnection conn = getConnection(url, "GET");
            conn.setRequestProperty("hike_id", Long.toString(hikeId));
            conn.connect();
            String stringHikeData = fetchResponse(conn, HttpURLConnection.HTTP_OK);
            JSONObject jsonHikeData = new JSONObject(stringHikeData);
            return RawHikeData.parseFromJSON(jsonHikeData);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Fetch multiple hikes from the server
     * @param hikeIds The numeric IDs of multiple hikes in the database
     * @return A list of {@link RawHikeData} objects encapsulating multiple hikes
     * @throws DatabaseClientException in case the hike could not be
     * retrieved for any reason external to the application (network failure, etc.)
     * or the hikeId did not match a valid hike.
     */
    public List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }

    /**
     * Get all hikes in a rectangular window on the map
     * @param bounds Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    public List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException {
        throw new DatabaseClientException("Not implemented."); // TODO implement
    }

    /**
     * Post a hike to the database. Returns the database ID
     * that this hike was assigned from the database.
     * @param hike Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     * retrieved for any reason external to the application (network failure, etc.)
     */
    public long postHike(RawHikeData hike) throws DatabaseClientException {
        try {
            URL url = new URL(mServerUrl + "/post_hike/");
            HttpURLConnection conn = getConnection(url, "POST");
            byte[] outputInBytes = hike.toJSON().toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            String stringHikeData = fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
            JSONObject jsonHikeId = new JSONObject(stringHikeData);
            return jsonHikeId.getLong("hike_id");
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }
    
    /**
     * Method to set the properties of the connection to the server
     * @param url the server url
     * @param method "GET" or "POST"
     * @return a valid HttpConnection
     * @throws IOException
     */
    private HttpURLConnection getConnection(URL url, String method) throws IOException {
        HttpURLConnection conn = mNetworkProvider.getConnection(url);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestProperty("Content-Type", JSON_CONTENT + ";" + ENCODING);
        conn.setDoInput(true);
        conn.setDoOutput(method.compareTo("POST") == 0);
        conn.setRequestMethod(method);
        return conn;
    }
    
    /**
     * Method to check the response code of the server and return the message that the server sends
     * @param conn an open HttpURLConnection
     * @return the string that was read from the connection
     * @throws IOException
     */
    private String fetchResponse(HttpURLConnection conn, int expectedResponseCode) throws IOException {
        int responseCode = conn.getResponseCode();
        StringBuilder result = new StringBuilder();
        if(responseCode != expectedResponseCode) {
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