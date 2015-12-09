/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on HikingApp ClientSide and
 * SwEngHomework3 NetworkQuizClient class
 */

package ch.epfl.sweng.team7.network;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.authentication.LoginRequest;
import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.HikeData;

/**
 * Class to get and post hikes in the server
 */
public class NetworkDatabaseClient implements DatabaseClient {

    private final static String LOG_FLAG = "Network_DatabaseClient";
    private final static int CONNECT_TIMEOUT = 1000;
    private final static String JSON_CONTENT = "application/json";
    private final static String JPEG_CONTENT = "image/jpeg";
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
            HttpURLConnection conn = getConnection("get_hike", "GET");
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
        Map<String, String> requestProperties = new HashMap<>();

        try {
            JSONObject boundingBoxJSON = new JSONObject();
            boundingBoxJSON.put("lat_min", bounds.southwest.latitude);
            boundingBoxJSON.put("lng_min", bounds.southwest.longitude);
            boundingBoxJSON.put("lat_max", bounds.northeast.latitude);
            boundingBoxJSON.put("lng_max", bounds.northeast.longitude);
            requestProperties.put("bounding_box", boundingBoxJSON.toString());
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }

        return getHikeIds("get_hikes_in_window", requestProperties);
    }


    /**
     * Get all hikes of a user
     *
     * @param userId A valid user ID
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    public List<Long> getHikeIdsOfUser(long userId) throws DatabaseClientException {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("user_id", Long.toString(userId));
        return getHikeIds("get_hikes_of_user", requestProperties);
    }


    /**
     * Get all hikes with given keywords
     *
     * @param keywords A string of keywords, separated by spaces. Special characters will be ignored.
     * @return A list of hike IDs
     * @throws DatabaseClientException in case the data could not be
     *                                 retrieved for any reason external to the application (network failure, etc.)
     */
    public List<Long> getHikeIdsWithKeywords(String keywords) throws DatabaseClientException {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("keywords", keywords);
        return getHikeIds("get_hikes_with_keywords", requestProperties);
    }


    /**
     * Get a list of hike IDs from the server, for any server function "get_hikes_..."
     * @param functionName the name of the server function
     * @param requestProperties the request properties, as name-value-pairs
     * @return a list of hike IDs
     * @throws DatabaseClientException
     */
    private List<Long> getHikeIds(String functionName, Map<String, String> requestProperties) throws DatabaseClientException {

        try {
            HttpURLConnection conn = getConnection(functionName, "GET");
            for(Map.Entry<String, String> property : requestProperties.entrySet()) {
                conn.setRequestProperty(property.getKey(), property.getValue());
            }
            conn.connect();
            String stringHikeIds = fetchResponse(conn, HttpURLConnection.HTTP_OK);

            // Parse response
            JSONObject jsonHikeIds = new JSONObject(stringHikeIds);
            JSONArray jsonHikeIdArray = jsonHikeIds.getJSONArray("hike_ids");
            List<Long> hikeList = new ArrayList<>();
            for (int i = 0; i < jsonHikeIdArray.length(); ++i) {
                hikeList.add(jsonHikeIdArray.getLong(i));
            }
            return hikeList;
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
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
            HttpURLConnection conn = getConnection("post_hike", "POST");
            byte[] outputInBytes = hike.toJSON().toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            String stringHikeData = fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
            JSONObject jsonHikeId = new JSONObject(stringHikeData);
            return jsonHikeId.getLong("hike_id");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_FLAG, "DatabaseClientException in post hike in Network database");
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            Log.d(LOG_FLAG, "JSONEXCEPTion in post hike in Network database");
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

            HttpURLConnection conn = getConnection("delete_hike", "POST");
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
            HttpURLConnection conn = getConnection("post_user", "POST");
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
            HttpURLConnection conn = getConnection("get_user", "GET");
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
     * Log user into the server, i.e. get user profile information
     *
     * @param loginRequest
     * @throws DatabaseClientException
     */

    public void loginUser(LoginRequest loginRequest) throws DatabaseClientException {
        try {
            HttpURLConnection conn = getConnection("login_user", "GET");
            conn.setRequestProperty("login_request", loginRequest.toJSON().toString());
            conn.connect();
            String stringResponse = fetchResponse(conn, HttpURLConnection.HTTP_OK);
            SignedInUser.getInstance().loginFromJSON(new JSONObject(stringResponse));
        } catch (IOException | JSONException e) {
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

            HttpURLConnection conn = getConnection("delete_user", "POST");
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
     * Get an image from the database
     * @param imageId the database key of the image
     * @return the image
     * @throws DatabaseClientException
     */
    public Drawable getImage(long imageId) throws DatabaseClientException {
        try {
            HttpURLConnection conn = getConnection("get_image", "GET");
            conn.setRequestProperty("image_id", Long.toString(imageId));
            conn.connect();

            checkResponseType(conn, HttpURLConnection.HTTP_OK, JPEG_CONTENT);

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            return Drawable.createFromStream(bis, "");
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException("ImageManager Error: " + e);
        }
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

        try {
            HttpURLConnection conn = getConnection("post_image", "POST");
            conn.setRequestProperty("image_id", Long.toString(imageId));
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] outputInBytes = stream.toByteArray();
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            String serverResponse = fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
            return new JSONObject(serverResponse).getLong("image_id");
        } catch (IOException e) {
            throw new DatabaseClientException(e);
        } catch (JSONException e) {
            throw new DatabaseClientException(e);
        }
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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image_id", imageId);

            HttpURLConnection conn = getConnection("delete_image", "POST");
            byte[] outputInBytes = jsonObject.toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            fetchResponse(conn, HttpURLConnection.HTTP_OK);
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Post a comment to the database
     * @param comment the comment to be posted
     * @return the database key of that comment
     * @throws DatabaseClientException
     */
    public long postComment(RawHikeComment comment) throws DatabaseClientException {
        try {
            HttpURLConnection conn = getConnection("post_comment", "POST");
            byte[] outputInBytes = comment.toJSON().toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            String stringResponse = fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
            return new JSONObject(stringResponse).getLong("comment_id");
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Delete a comment from the database
     *
     * @param commentId the database key of the comment
     * @throws DatabaseClientException
     */
    public void deleteComment(long commentId) throws DatabaseClientException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comment_id", commentId);

            HttpURLConnection conn = getConnection("delete_comment", "POST");
            byte[] outputInBytes = jsonObject.toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            fetchResponse(conn, HttpURLConnection.HTTP_OK);
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
    }

    /**
     * Post a vote about a hike.
     */
    public void postVote(RatingVote vote) throws DatabaseClientException {
        try {
            HttpURLConnection conn = getConnection("post_vote", "POST");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("owner_id", SignedInUser.getInstance().getId());
            jsonObject.put("hike_id", vote.getHikeId());
            jsonObject.put("value", vote.getRating());
            byte[] outputInBytes = jsonObject.toString().getBytes("UTF-8");
            conn.connect();
            conn.getOutputStream().write(outputInBytes);
            fetchResponse(conn, HttpURLConnection.HTTP_CREATED);
        } catch (IOException | JSONException e) {
            throw new DatabaseClientException(e);
        }
        return;
    }

    /**
     * Method to set the properties of the connection to the server
     *
     * @param function the server function, without /
     * @param method   "GET" or "POST"
     * @return a valid HttpConnection
     * @throws IOException
     */
    private HttpURLConnection getConnection(String function, String method) throws IOException, JSONException {
        URL url = new URL(mServerUrl + "/" + function + "/");
        HttpURLConnection conn = mNetworkProvider.getConnection(url);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestProperty("Content-Type", JSON_CONTENT + ";" + ENCODING);
        conn.setDoInput(true);
        conn.setDoOutput(method.compareTo("POST") == 0);
        conn.setRequestMethod(method);

        // Authentication
        SignedInUser signedInUser = SignedInUser.getInstance();
        //conn.setRequestProperty("auth_user_id", Long.toString(signedInUser.getId()));
        conn.setRequestProperty("auth_header", signedInUser.buildAuthHeader().toString());
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

        checkResponseType(conn, expectedResponseCode, JSON_CONTENT);

        InputStream input = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line + "\n");
        }
        conn.disconnect();
        return result.toString();
    }

    /**
     * Method to check the response code and content type of the server
     */
    private void checkResponseType(HttpURLConnection conn, int expectedResponseCode, String expectedContentType) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode != expectedResponseCode) {
            throw new IOException("Unexpected HTTP Response Code: " + responseCode + " (Expected: " + expectedResponseCode + ")");
        }

        String contentType = conn.getContentType();
        if (contentType == null) {
            throw new IOException("HTTP content type unset");
        } else if (!contentType.equals(expectedContentType)) {
            throw new IOException("Invalid HTTP content type: " + contentType + " (Expected: " + expectedContentType + ")");
        }

    }


}