package ch.epfl.sweng.team7.database;

import ch.epfl.sweng.team7.network.RawHikePoint;

/**
 * Created by pablo on 23/11/15.
 */
public class Annotation {
    private RawHikePoint mRawHikePoint;

    public Annotation (RawHikePoint rawHikePoint){
        mRawHikePoint = rawHikePoint;
    }

    public RawHikePoint getRawHikePoint(){ return mRawHikePoint; }
}
