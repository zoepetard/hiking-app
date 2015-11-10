package ch.epfl.sweng.team7.gpsService.containers;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to be used as a container of GPSFootPrints.
 * When we finish populating it, it should have all
 * the information needed to reproduce an entire hike.
 */
public final class GPSPath {

    private final static String LOG_FLAG = "GPS_Path";

    private List<GPSFootPrint> path;

    public GPSPath() {
        this.path = new ArrayList<GPSFootPrint>();
    }

    public void addFootPrint(GPSFootPrint newFootPrint) {
        if (newFootPrint != null) path.add(newFootPrint);
    }

    public float getAverageSpeed() {
        //TODO implement this method
        return 0.0f;
    }

    public long getFootPrintCount() {
        return this.path.size();
    }

    @Override
    public String toString() {
        return String.format("[FootPrints: %d]", path.size());
    }
}
