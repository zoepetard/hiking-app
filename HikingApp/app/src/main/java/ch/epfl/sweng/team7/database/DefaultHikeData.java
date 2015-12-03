/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 03 Nov 2015
 */
package ch.epfl.sweng.team7.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.network.Rating;
import ch.epfl.sweng.team7.network.RawHikeComment;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static android.location.Location.distanceBetween;

public class DefaultHikeData implements HikeData {

    private final static String LOG_FLAG = "DB_DefaultHikeData";

    private final long mHikeId;    // Database hike ID of this hike
    private final long mOwnerId;   // Database user ID of owner
    private final Date mDate;      // A UTC time stamp
    private final List<HikePoint> mHikePoints;
    private final List<HikeComment> mComments;
    private final double mDistance;
    private final LatLngBounds mBoundingBox;
    private final LatLng mHikeLocation;
    private final LatLng mStartLocation;
    private final LatLng mFinishLocation;
    private final ElevationBounds mElevationBounds;
    private final Rating mRating;
    private String mTitle;

    /**
     * A HikeData object is created from a RawHikeData, but calculates much more information
     * that can later be queried by the user interface.
     */
    public DefaultHikeData(RawHikeData rawHikeData) {
        mHikeId = rawHikeData.getHikeId();
        mOwnerId = rawHikeData.getOwnerId();
        mDate = rawHikeData.getDate();

        List<RawHikePoint> rawHikePoints = rawHikeData.getHikePoints();
        mHikePoints = new ArrayList<>();
        for (RawHikePoint rawHikePoint : rawHikePoints) {
            mHikePoints.add(new DefaultHikePoint(rawHikePoint));
        }

        List<RawHikeComment> rawHikeComments = rawHikeData.getAllComments();
        mComments = new ArrayList<>();
        for (RawHikeComment rawHikeComment : rawHikeComments) {
            mComments.add(new DefaultHikeComment(rawHikeComment));
        }

        mDistance = calculateDistance(rawHikePoints);
        mBoundingBox = calculateBoundingBox(rawHikePoints);
        mHikeLocation = getBoundingBox().getCenter();
        mStartLocation = rawHikePoints.get(0).getPosition();
        mFinishLocation = rawHikePoints.get(rawHikePoints.size() - 1).getPosition();
        mElevationBounds = calculateElevationBounds(rawHikePoints);

        // Hike rating is optional in RawHikeData, but this class is guaranteed to have it.
        if (rawHikeData.getRating() != null) {
            mRating = rawHikeData.getRating();
        } else {
            mRating = new Rating();
        }

        mTitle = rawHikeData.getTitle();
    }

    /**
     * @param newTitle
     */
    @Override
    public void setTitle(String newTitle) {
        mTitle = newTitle;
    }

    /**
     * @return the hike ID.
     */
    public long getHikeId() {
        return mHikeId;
    }

    /**
     * @return the owner ID.
     */
    public long getOwnerId() {
        return mOwnerId;
    }

    /**
     * @return the date of the hike
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * @return the rating of the hike
     * This feature is not yet implemented in the backend
     */
    public Rating getRating() {
        return mRating;
    }

    /**
     * @return an ordered list of the waypoints on this hike
     */
    public List<HikePoint> getHikePoints() {
        return mHikePoints;
    }

    public List<HikeComment> getAllComments() {
        return mComments;
    }

    /**
     * @return the total distance covered by the hike in kilometers
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * @return the total elevation gain in meters,
     * which is the sum of all position elevation differences, ignoring all downhill
     */
    public double getElevationGain() {
        return mElevationBounds.mElevationGain;
    }

    /**
     * @return the total elevation loss in meters,
     * which is the sum of all negative elevation differences, ignoring all uphill
     */
    public double getElevationLoss() {
        return mElevationBounds.mElevationLoss;
    }

    /**
     * @return the elevation of the hike's highest point, in meters
     */
    public double getMaxElevation() {
        return mElevationBounds.mMaxElevation;
    }

    /**
     * @return the elevation of the hike's lowest point, in meters
     */
    public double getMinElevation() {
        return mElevationBounds.mMinElevation;
    }

    /**
     * @return a box around the hike.
     * Queried by the backend/interface to see whether hike should be displayed.
     */
    public LatLngBounds getBoundingBox() {
        return mBoundingBox;
    }

    /**
     * @return a representative point for this hike, where the pin will be placed on the map
     */
    public LatLng getHikeLocation() {
        return mHikeLocation;
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public LatLng getFinishLocation() {
        return mFinishLocation;
    }

    public String getTitle() {
        return mTitle;
    }

    private double calculateDistance(List<RawHikePoint> rawHikePoints) {
        double distance = 0;
        for (int i = 0; i < rawHikePoints.size() - 1; i++) {
            LatLng currentLoc = rawHikePoints.get(i).getPosition();
            LatLng nextLoc = rawHikePoints.get(i + 1).getPosition();
            float[] distanceBetween = new float[1];
            //Computes the approximate distance (in meters) between polyLinePoint and point.
            //Returns the result as the first element of the float array distanceBetween
            distanceBetween(currentLoc.latitude, currentLoc.longitude,
                    nextLoc.latitude, nextLoc.longitude, distanceBetween);
            distance += (double) distanceBetween[0];
        }
        return distance;
    }

    private LatLngBounds calculateBoundingBox(List<RawHikePoint> rawHikePoints) {
        LatLngBounds.Builder boundingBoxBuilder = new LatLngBounds.Builder();
        for (RawHikePoint rawHikePoint : rawHikePoints) {
            LatLng pos = rawHikePoint.getPosition();
            boundingBoxBuilder.include(pos);
        }
        return boundingBoxBuilder.build();
    }

    private ElevationBounds calculateElevationBounds(List<RawHikePoint> rawHikePoints) {

        ElevationBounds elevationBounds = new ElevationBounds();

        // Initialize elevation
        double lastElevation = rawHikePoints.get(0).getElevation();
        elevationBounds.mMaxElevation = lastElevation;
        elevationBounds.mMinElevation = lastElevation;

        // Traverse hike points in order
        for (int i = 1; i < rawHikePoints.size(); ++i) {
            final double thisElevation = rawHikePoints.get(i).getElevation();
            final double deltaElevation = thisElevation - lastElevation;

            if (deltaElevation > 0) {
                elevationBounds.mElevationGain += deltaElevation;
            } else {
                // ElevationLoss is always positive, but deltaElevation is negative here
                elevationBounds.mElevationLoss += (-deltaElevation);
            }

            if (thisElevation > elevationBounds.mMaxElevation) {
                elevationBounds.mMaxElevation = thisElevation;
            }

            if (thisElevation < elevationBounds.mMinElevation) {
                elevationBounds.mMinElevation = thisElevation;
            }

            lastElevation = thisElevation;
        }
        return elevationBounds;
    }

    /**
     * Method to export the hike as a gpx file to the phone's internal storage
     */
    private void saveGPX() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            Element rootElement = doc.createElement("gpx");
            doc.appendChild(rootElement);

            // adds attributes to gpx element
            rootElement.setAttribute("version","1.0");
            rootElement.setAttribute("creator", SignedInUser.getInstance().getMailAddress());
            rootElement.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("xmlns","http://www.topografix.com/GPX/1/0");
            rootElement.setAttribute("xsi:schemaLocation","http://www.topografix.com/GPX/1/0/gpx.xsd");

            // set time
            Element timeElement = doc.createElement("time");
            timeElement.appendChild(doc.createTextNode("2005-11-07T14:00:09Z")); // TODO use real data
            rootElement.appendChild(timeElement);

            // adding track
            Element trackElement = doc.createElement("trk");
            Element trackName = doc.createElement("name");
            trackName.appendChild(doc.createTextNode(mTitle));
            trackElement.appendChild(trackName);
            rootElement.appendChild(trackElement);

            // track segment
            Element trackSegment = doc.createElement("trkseg");
            trackElement.appendChild(trackSegment);

            // for every hike point add a track point
            Element trackPoint = doc.createElement("trkpt");
            trackPoint.setAttribute("lat","52.56");
            trackPoint.setAttribute("lon","-1.82");
            Element elevation = doc.createElement("ele"); // TODO use real data
            elevation.appendChild(doc.createTextNode("115.5")); // TODO use real data
            trackPoint.appendChild(elevation);
            Element pointTime = doc.createElement("time");
            pointTime.appendChild(doc.createTextNode("2005-11-07T14:00:09Z")); // TODO use real data
            trackPoint.appendChild(pointTime);

            trackSegment.appendChild(trackPoint);

            // transform content into xml and print it out
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(System.out);

            // Indent and output to console for testing
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(source, result);


        }catch(ParserConfigurationException e){


        }catch(TransformerException te) {


        }

    }


    // A simple storage container for elevation-related data.
    // ElevationGain and ElevationLoss are both positive numbers.
    private class ElevationBounds {
        public double mMinElevation;
        public double mMaxElevation;
        public double mElevationGain;
        public double mElevationLoss;
    }

}
