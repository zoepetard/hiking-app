package ch.epfl.sweng.team7.hikingapp;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

/**
 * Data container for a hike displayed on a map.
 *
 * Created by zoepetard on 09/12/15.
 */
public class DisplayedHike {
    private Long mId;
    private Polyline mPolyline;
    private Marker mStartMarker;
    private Marker mFinishMarker;

    public DisplayedHike(Long id, Polyline polyline, Marker startMarker, Marker finishMarker) {
        mId = id;
        mPolyline = polyline;
        mStartMarker = startMarker;
        mFinishMarker = finishMarker;
    }

    public void setId(Long newId) {
        mId = newId;
    }

    public void setPolyline(Polyline newPolyline) {
        mPolyline = newPolyline;
    }

    public void setStartMarker(Marker newStartMarker) {
        mStartMarker = newStartMarker;
    }

    public void setFinishMarker(Marker newFinishMarker) {
        mFinishMarker = newFinishMarker;
    }

    public long getId() {
        return this.mId;
    }

    public Polyline getPolyline() {
        return this.mPolyline;
    }

    public Marker getStartMarker() {
        return this.mStartMarker;
    }

    public Marker getFinishMarker() {
        return this.mFinishMarker;
    }
}
