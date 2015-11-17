package ch.epfl.sweng.team7.database;


import android.graphics.drawable.Drawable;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Created by pablo on 23/11/15.
 */
public class Annotation {
    private final static String LOG_FLAG = "Annotation";
    private RawHikePoint mRawHikePoint;
    private String mText;
    private Drawable mPicture;
    private long mPictureId;


    public Annotation(RawHikePoint rawHikePoint, String text, Drawable picture) {
        mRawHikePoint = rawHikePoint;
        mText = text;
        mPicture = picture;

    }


    public RawHikePoint getRawHikePoint() {
        return mRawHikePoint;
    }

    public String getAnnotation() {
        return mText;
    }

    public Drawable getPicture() {
        return mPicture;
    }

    public long getPictureId() {
        return mPictureId;
    }

    public void setPicturedId(long pictureId) {
        mPictureId = pictureId;
    }

    public void setPicture(Drawable picture) {
        mPicture = picture;
    }

    public void setText(String text) {
        mText = text;
    }


    /**
     * Method to convert JsonObject into annotation
     *
     * @param jsonObject [Point[lat, long, date, elevation], text, pictureID]
     * @return
     * @throws JSONException
     */
    public static Annotation parseFromJSON(JSONObject jsonObject) throws JSONException {
        LatLng latLng = new LatLng(jsonObject.getJSONArray("point").getDouble(0), jsonObject.getJSONArray("point").getDouble(1));
        Date date = new Date(jsonObject.getJSONArray("point").getLong(2));
        double elevation = jsonObject.getJSONArray("point").getDouble(3);
        String text = new String(jsonObject.getString("text_annotation"));
        long pictureId = jsonObject.getLong("picture_id");
        Annotation annotation = new Annotation(new RawHikePoint(latLng, date, elevation), text, null);
        annotation.setPicturedId(pictureId);
        return annotation;
    }



    /**
     * @return JSONArray [String comment, RawHikePoint rawHikePoint].
     * Storing a JSON array instead of a full JSON object reduces the communication/storage
     * data size of a hike.
     * @throws JSONException
     */
    public JSONArray toJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(getRawHikePoint().getPosition().latitude)
                .put(getRawHikePoint().getPosition().longitude)
                .put(getRawHikePoint().getTime())
                .put(getRawHikePoint().getElevation())
                .put(mText)
                .put(mPictureId);
        return jsonArray;
    }



    /**
     * Parse a RawHikePoint from an appropriate JSON object
     * @param jsonArray [String comment, double lat, double lng, long date, double elevation]
     * @return a valid RawHikePoint object
     * @throws JSONException
     */
    public static Annotation parseFromJSON(JSONArray jsonArray) throws JSONException {
        String comment = new String(jsonArray.getString(0));
        LatLng latLng = new LatLng(jsonArray.getDouble(1), jsonArray.getDouble(2));
        Date date = new Date(jsonArray.getLong(3));
        double elevation = jsonArray.getDouble(4);
        return new Annotation(new RawHikePoint(latLng, date, elevation), comment, null);
    }

}
