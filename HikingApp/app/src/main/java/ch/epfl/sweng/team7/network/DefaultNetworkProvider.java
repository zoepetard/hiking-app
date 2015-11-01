package ch.epfl.sweng.team7.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A default implementation of the {@link NetworkProvider} interface that uses
 * the mechanism available in the {@link URL} object to create
 * {@link HttpURLConnection} objects.
 *
 */
public class DefaultNetworkProvider implements NetworkProvider {

    /**
     * The default constructor.
     */
    public DefaultNetworkProvider() {
    }

    /**
     * Returns a connection to a specified URL.
     * @param url a valid HTTP or HTTPS URL.
     * @return a valid {@link HttpURLConnection}
     * @throws IOException
     */
    @Override
    public HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}