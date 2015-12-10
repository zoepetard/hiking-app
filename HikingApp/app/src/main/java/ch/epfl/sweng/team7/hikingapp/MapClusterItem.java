package ch.epfl.sweng.team7.hikingapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by zoepetard on 09/12/15.
 */
public class MapClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mStartOrFinish;

    public MapClusterItem(double lat, double lng, String startOrFinish) {
        mPosition = new LatLng(lat, lng);
        mStartOrFinish = startOrFinish;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getStartOrFinish() {
        return mStartOrFinish;
    }

}
