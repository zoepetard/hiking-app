package ch.epfl.sweng.team7.database;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Class to represent a PictureAnnotation
 * Created by pablo on 18/11/15.
 */

public class PictureAnnotation extends Annotation {

    private final static String LOG_FLAG = "DB_Picture";
    private RawHikePoint mRawHikePoint;
    private long mHikeId;
    private long mPictureId;
    private Image mPicture;
    public PictureAnnotation(RawHikePoint rawHikePoint, Image picture) {
        super(rawHikePoint);
        mPicture = picture;
    }

    public RawHikePoint getRawHikePoint() {
        return mRawHikePoint;
    }

    public long getHikeId () {
        return mHikeId;
    }

    public long getPictureId () { return mPictureId; }

    public Image getImage() { return mPicture; }
}
