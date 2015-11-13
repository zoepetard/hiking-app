package ch.epfl.sweng.team7.network;

/**
 * Created by simon on 11/13/15.
 *
 * Thrown to indicate a parsing problem
 */
public class ParsingException extends Exception {

    private final static String LOG_FLAG = "Network_ParsingException";
    private static final long serialVersionUID = 1L;

    public ParsingException() {
        super();
    }

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(Throwable throwable) {
            super(throwable);
        }
}
