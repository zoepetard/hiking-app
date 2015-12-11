package ch.epfl.sweng.team7.database;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * The annotation class stores an annotation to a hike,
 * which can be text or an image (by image id)
 *
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
     * @throws JSONException
     */
    public static Annotation parseFromJSON(JSONArray jsonObject) throws JSONException {
        Log.d(LOG_FLAG, jsonObject.toString());
        LatLng latLng = new LatLng(jsonObject.getDouble(0), jsonObject.getDouble(1));
        Date date = new Date();
        double elevation = jsonObject.getDouble(3);
        String text = jsonObject.getString(4);
        long pictureId = jsonObject.getLong(5);
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

}
