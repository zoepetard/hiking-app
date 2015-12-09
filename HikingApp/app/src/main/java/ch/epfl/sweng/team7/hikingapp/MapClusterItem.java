package ch.epfl.sweng.team7.hikingapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by zoepetard on 09/12/15.
 */
public class MapClusterItem implements ClusterItem {
    private final LatLng mPosition;

    public MapClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
