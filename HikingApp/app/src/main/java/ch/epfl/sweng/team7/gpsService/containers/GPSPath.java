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
    private long mTotalTime;

    public GPSPath() {
        this.path = new ArrayList<>();
        this.mTotalTime = 0;
    }

    /**
     * Method called to add a new FootPrint to this GPSPath, with
     * default pause flag set to false.
     * @param newFootPrint new FootPrint to be added.
     */
    public void addFootPrint(GPSFootPrint newFootPrint) {
        addFootPrint(newFootPrint, false);
    }

    /**
     * Method calles to add a new FootPrint to this GPSPath, with
     * a custom pause flag.
     * @param newFootPrint new FootPrint to be added.
     * @param resumeFootPrint boolean value representing the pause flag.
     */
    public void addFootPrint(GPSFootPrint newFootPrint, boolean resumeFootPrint) {
        if (path.size() >= 1 && !resumeFootPrint) mTotalTime += newFootPrint.getTimeStamp() - path.get(path.size() - 1).getTimeStamp();
        if (newFootPrint != null) path.add(newFootPrint);
    }

    /**
     * Method called to get the number of existing FootPrints.
     */
    public long getFootPrintCount() {
        return this.path.size();
    }

    /**
     * Method called to remove all FootPrints before a certain index.
     * @param index index until which all FootPrints will be removed.
     */
    public void removeFootPrintsBefore(int index) {
        if (path != null && index < path.size() && index >= 0) {
            path = path.subList(0, index);
        }
    }

    /**
     * Method called to remove all FootPrints after a certain index.
     * @param index index after which all FootPrints will be removed.
     */
    public void removeFootPrintsAfter(int index) {
        if (path != null && index < path.size() && index >= 0) {
            path = path.subList(index, path.size());
        }
    }

    /**
     * Method called to get access to the path stored so far.
     */
    public List<GPSFootPrint> getPath() {
        return path;
    }

    /**
     * Method called to get the time elapsed, in seconds,
     * since the beginning.
     */
    public long timeElapsedInSeconds() {
        return mTotalTime/1000;
    }

    /**
     * Method called to get the distance between the first
     * and the last FootPrints stored.
     */
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
