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

    public List<GPSFootPrint> getPath() {
        return path;
    }

    public long timeElapsedInSeconds() {
        try {
            return (path.get(path.size()-1).getTimeStamp() - path.get(0).getTimeStamp())/1000;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public float distanceToStart() {
        try {
            Location startLoc = new Location("");
            startLoc.setLatitude(path.get(0).getGeoCoords().getLatitude());
            startLoc.setLongitude(path.get(0).getGeoCoords().getLongitude());
            startLoc.setAltitude(path.get(0).getGeoCoords().getAltitude());

            Location lastLoc = new Location("");
            lastLoc.setLatitude(path.get(path.size() - 1).getGeoCoords().getLatitude());
            lastLoc.setLongitude(path.get(path.size() - 1).getGeoCoords().getLatitude());
            lastLoc.setAltitude(path.get(path.size() - 1).getGeoCoords().getAltitude());

            return (startLoc.distanceTo(lastLoc));
        } catch (IndexOutOfBoundsException e) {
            return 0f;
        }
    }

    @Override
    public String toString() {
        return String.format("[FootPrints: %d]", path.size());
    }
}
