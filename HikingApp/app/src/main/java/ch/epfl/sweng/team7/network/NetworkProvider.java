package ch.epfl.sweng.team7.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Constructs {@link HttpURLConnection} objects that can be used to
 * retrieve data from a given {@link URL}.
 *
 */
public interface NetworkProvider {
    /**
     * Returns a new {@link HttpURLConnection} object for the given {@link URL}.
     *
     * @param url a valid HTTP or HTTPS URL.
     * @return a new {@link HttpURLConnection} object for successful
     * connections.
     * @throws IOException if the connection object could not be created or
     * if the URL is not HTTP/HTTPS.
     */
    HttpURLConnection getConnection(URL url) throws IOException;
}