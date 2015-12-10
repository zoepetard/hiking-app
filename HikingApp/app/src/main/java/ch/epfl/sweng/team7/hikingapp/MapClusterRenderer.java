package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by zoepetard on 09/12/15.
 */
public class  MapClusterRenderer extends DefaultClusterRenderer<MapClusterItem> {

    private static final int[] BUCKETS = {10, 20, 50, 100, 200, 500, 1000};

    MapClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MapClusterItem> cluster) {
        return cluster.getSize() > 2;
    }

    @Override
    protected int getBucket(Cluster<MapClusterItem> cluster) {
        int size = cluster.getSize() / 2;
        if (size <= BUCKETS[0]) {
            return size;
        }
        for (int i = 0; i < BUCKETS.length - 1; i++) {
            if (size < BUCKETS[i + 1]) {
                return BUCKETS[i];
            }
        }
        return BUCKETS[BUCKETS.length - 1];
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item,
                                               MarkerOptions markerOptions) {
        if (item.getStartOrFinish().equals("start"))
        {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start_hike));
        }
        else if (item.getStartOrFinish().equals("finish")) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_finish_hike));
        }
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(MapClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }
}


