package ch.epfl.sweng.team7.gpsService.containers;

import android.location.Location;

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
        this.path = new ArrayList<>();
    }

    public void addFootPrint(GPSFootPrint newFootPrint) {
        if (newFootPrint != null) path.add(newFootPrint);
    }

    public long getFootPrintCount() {
        return this.path.size();
    }

    public void removeFootPrintsBefore(int index) {
        if (path != null && index < path.size() && index >= 0) {
            path = path.subList(0, index);
        }
    }

    public void removeFootPrintsAfter(int index) {
        if (path != null && index < path.size() && index >= 0) {
            path = path.subList(index, path.size()-1);
        }
    }

    public List<GPSFootPrint> getPath() {
        return path;
    }

    public long timeElapsedInSeconds() {
        if (path.size() >= 2) {
            return (path.get(path.size()-1).getTimeStamp() - path.get(0).getTimeStamp())/1000;
        } else {
            return 0;
        }
    }

    public float distanceToStart() {
        if (path.size() >= 2) {
            Location startLoc = path.get(0).toLocation();
            Location lastLoc = path.get(path.size() - 1).toLocation();
            return (startLoc.distanceTo(lastLoc));
        } else {
            return 0f;
        }
    }

    @Override
    public String toString() {
        return String.format("[FootPrints: %d]", path.size());
    }
}
