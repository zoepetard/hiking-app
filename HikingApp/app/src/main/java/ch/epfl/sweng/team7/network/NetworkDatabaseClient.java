/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on HikingApp ClientSide and
 * SwEngHomework3 NetworkQuizClient class
 */

package ch.epfl.sweng.team7.network;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.hikingapp.SignedInUser;


/**
 * Class to get and post hikes in the server
 */
public class NetworkDatabaseClient implements DatabaseClient {

    private final static String LOG_FLAG = "Network_DatabaseClient";
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
     *
     * @param hikeId The numeric ID of one hike in the database
     * @return A {@link RawHikeData} object encapsulating one hike
     * @throws DatabaseClientException in case the hike could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     *                                 or the hikeId did not match a valid hike.
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
        } catch (IOException | JSONException | HikeParseException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Fetch multiple hikes from the server
     *
     * @param hikeIds The numeric IDs of multiple hikes in the database
     * @return A list of {@link RawHikeData} objects encapsulating multiple hikes
     * @throws DatabaseClientException in case the hike could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     *                                 or the hikeId did not match a valid hike.
     */
    public List<RawHikeData> fetchMultipleHikes(List<Long> hikeIds) throws DatabaseClientException {
        // TODO implement properly
        List<RawHikeData> rawHikeDatas = new ArrayList<>();
        for (Long hikeId : hikeIds) {
            rawHikeDatas.add(fetchSingleHike(hikeId));
        }
        return rawHikeDatas;
    }

    /**
     * Get all hikes in a rectangular window on the map
     *
     * @param bounds Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    public List<Long> getHikeIdsInWindow(LatLngBounds bounds) throws DatabaseClientException {

        String boundingBoxJSON = String.format(
                "{\"lat_min\":%f,\"lng_min\":%f,\"lat_max\":%f,\"lng_max\":%f}",
                bounds.southwest.latitude, bounds.southwest.longitude,
                bounds.northeast.latitude, bounds.northeast.longitude);
        List<Long> hikeList = new ArrayList<>();

        try {
            URL url = new URL(mServerUrl + "/get_hikes_in_window/");
            HttpURLConnection conn = getConnection(url, "GET");
            conn.setRequestProperty("bounding_box", boundingBoxJSON);
            conn.connect();
            String stringHikeIds = fetchResponse(conn, HttpURLConnection.HTTP_OK);

            // Parse response
            JSONObject jsonHikeIds = new JSONObject(stringHikeIds);
            JSONArray jsonHikeIdArray = jsonHikeIds.getJSONArray("hike_ids");
            for (int i = 0; i < jsonHikeIdArray.length(); ++i) {
                hikeList.add(jsonHikeIdArray.getLong(i));
            }
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
        return hikeList;
    }


    public List<Long> getHikeIdsOfUser(long userId) throws DatabaseClientException {

        List<Long> hikeList = new ArrayList<>();

        try {
            URL url = new URL(mServerUrl + "/get_hikes_of_user/");
            HttpURLConnection conn = getConnection(url, "GET");
            conn.setRequestProperty("user_id", Long.toString(userId));
            conn.connect();
            String stringHikeIds = fetchResponse(conn, HttpURLConnection.HTTP_OK);

            // Parse response
            JSONObject jsonHikeIds = new JSONObject(stringHikeIds);
            JSONArray jsonHikeIdArray = jsonHikeIds.getJSONArray("hike_ids");
            for (int i = 0; i < jsonHikeIdArray.length(); ++i) {
                hikeList.add(jsonHikeIdArray.getLong(i));
            }
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
        return hikeList;
    }

    /**
     * Post a hike to the database. Returns the database ID
     * that this hike was assigned from the database.
     *
     * @param hike Boundaries (window) of the
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
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
     * Delete a hike from the server. A hike can only be deleted by its owner.
     *
     * @param hikeId - ID of the hike
     * @throws DatabaseClientException if unable to delete user
     */
    public void deleteHike(long hikeId) throws DatabaseClientException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hike_id", hikeId);

            URL url = new URL(mServerUrl + "/delete_hike/");
            HttpURLConnection conn = getConnection(url, "POST");
            byte[] outputInBytes = jsonObject.toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            fetchResponse(conn, HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Send user data to the server.  Returns the database ID
     * that this user was assigned from the database.
     *
     * @param rawUserData - RawUserData object
     * @return user id
     * @throws DatabaseClientException
     */
    public long postUserData(RawUserData rawUserData) throws DatabaseClientException {
        try {
            URL url = new URL(mServerUrl + "/post_user/");
            HttpURLConnection conn = getConnection(url, "POST");
            byte[] outputInBytes = rawUserData.toJSON().toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            String serverResponse = fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
            return new JSONObject(serverResponse).getLong("user_id");
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Fetch data for a user from the server
     *
     * @param userId - mail address of the user
     * @return RawUserData
     * @throws DatabaseClientException if unable to fetch user data
     */
    public RawUserData fetchUserData(long userId) throws DatabaseClientException {
        try {
            URL url = new URL(mServerUrl + "/get_user/");
            HttpURLConnection conn = getConnection(url, "GET");
            conn.setRequestProperty("user_id", Long.toString(userId));
            conn.connect();
            String stringUserData = fetchResponse(conn, HttpURLConnection.HTTP_OK);
            JSONObject jsonUserData = new JSONObject(stringUserData);
            return RawUserData.parseFromJSON(jsonUserData);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException("Couldn't retrieve user data: " + e.getMessage());
        }
    }

    /**
     * TODO DEPRECATED - remove from code
     *
     * @param mailAddress - used to query server
     * @return RawUserData - corresponding to user's mail address
     */
    public RawUserData fetchUserData(String mailAddress) throws DatabaseClientException {

        try {
            URL url = new URL(mServerUrl + "/get_user/");
            HttpURLConnection conn = getConnection(url, "GET");
            // TODO change 2nd parameter to mailAddress when servers accepts new users
            conn.setRequestProperty("user_mail_address", mailAddress);
            conn.connect();
            String stringUserId = fetchResponse(conn, HttpURLConnection.HTTP_OK);
            JSONObject jsonObject = new JSONObject(stringUserId);
            return RawUserData.parseFromJSON(jsonObject);
        } catch (IOException e) {
            throw new DatabaseClientException(e.getMessage());
        } catch (JSONException e) {
            throw new DatabaseClientException("JSONException: " + e.getMessage());
        }
    }

    /**
     * Logs in the SignedInUser
     */
    public void loginUser() throws DatabaseClientException {
        SignedInUser signedInUser = SignedInUser.getInstance();

        try {
            URL url = new URL(mServerUrl + "/login_user/");
            HttpURLConnection conn = getConnection(url, "GET");
            conn.setRequestProperty("user_mail_address", signedInUser.getMailAddress());
            conn.connect();
            String stringUserId = fetchResponse(conn, HttpURLConnection.HTTP_OK);
            JSONObject jsonObject = new JSONObject(stringUserId);
            signedInUser.loginFromJSON(jsonObject);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Delete a user from the server. A user can only delete himself.
     *
     * @param userId - ID of the user
     * @throws DatabaseClientException if unable to delete user
     */
    public void deleteUser(long userId) throws DatabaseClientException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);

            URL url = new URL(mServerUrl + "/delete_user/");
            HttpURLConnection conn = getConnection(url, "POST");
            byte[] outputInBytes = jsonObject.toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            fetchResponse(conn, HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Method to set the properties of the connection to the server
     *
     * @param url    the server url
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
     *
     * @param conn an open HttpURLConnection
     * @return the string that was read from the connection
     * @throws IOException
     */
    private String fetchResponse(HttpURLConnection conn, int expectedResponseCode) throws IOException {
        int responseCode = conn.getResponseCode();
        StringBuilder result = new StringBuilder();
        if (responseCode != expectedResponseCode) {
            throw new IOException("Unexpected HTTP Response Code: " + responseCode);
        }

        String contentType = conn.getContentType();
        if (contentType == null) {
            throw new IOException("HTTP content type unset");
        } else if (!contentType.equals(JSON_CONTENT)) {
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