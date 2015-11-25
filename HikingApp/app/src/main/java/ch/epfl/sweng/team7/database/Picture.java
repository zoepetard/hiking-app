package ch.epfl.sweng.team7.database;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Class to represent a Picture
 * Created by pablo on 18/11/15.
 */

public class Picture {
    private RawHikePoint mRawHikePoint;
    private long mHikeId;
    private Image mPicture;
    public Picture(RawHikePoint rawHikePoint, Image picture ) {
        mRawHikePoint = rawHikePoint;
        mPicture = picture;
    }

    public RawHikePoint getRawHikePoint() {
        return mRawHikePoint;
    }

    public long getHikeId () {
        return mHikeId;
    }
}
