package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Created by pablo on 17/11/15.
 */
public class TextAnnotation {

    private final static String LOG_FLAG = "Text annotation";
    private String mComment;
    private RawHikePoint mRawHikePoint;


    public TextAnnotation(String comment, RawHikePoint rawHikePoint) {
        mRawHikePoint = rawHikePoint;
        mComment = comment;
    }

    /**
     * Parse a RawHikePoint from an appropriate JSON object
     *
     * @param jsonArray [String comment, double lat, double lng, long date, double elevation]
     * @return a valid RawHikePoint object
     * @throws JSONException
     */
    public static TextAnnotation parseFromJSON(JSONArray jsonArray) throws JSONException {
        LatLng latLng = new LatLng(jsonArray.getDouble(0), jsonArray.getDouble(1));
        Date date = new Date(jsonArray.getLong(2));
        double elevation = jsonArray.getDouble(3);
        String comment = new String(jsonArray.getString(4));
        return new TextAnnotation(comment, new RawHikePoint(latLng, date, elevation));
    }


    /**
     * @return JSONArray [String comment, RawHikePoint rawHikePoint].
     * Storing a JSON array instead of a full JSON object reduces the communication/storage
     * data size of a hike.
     * @throws JSONException
     */
    public JSONArray toJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(mComment)
                .put(mRawHikePoint.getPosition().latitude)
                .put(mRawHikePoint.getPosition().longitude)
                .put(mRawHikePoint.getTime())
                .put(mRawHikePoint.getElevation());
        return jsonArray;
    }


    public String getmComment() {
        return mComment;
    }

    public RawHikePoint getRawHikePoint() {
        return mRawHikePoint;
    }
}
