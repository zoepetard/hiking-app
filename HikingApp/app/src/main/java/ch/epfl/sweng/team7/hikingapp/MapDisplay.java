package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;

/**
 * Created by zoepetard on 26/11/15.
 */
public class MapDisplay {

    public static List<Polyline> displayHikes(List<HikeData> hikesToDisplay, GoogleMap map) {
        List<Polyline> displayedHikes = new ArrayList<>();
        for (HikeData hike: hikesToDisplay) {
            PolylineOptions polylineOptions = new PolylineOptions();
            List<HikePoint> databaseHikePoints = hike.getHikePoints();
            for (HikePoint hikePoint : databaseHikePoints) {
                polylineOptions.add(hikePoint.getPosition());
            }
            Polyline polyline = map.addPolyline(polylineOptions);
            displayedHikes.add(polyline);
        }
        return displayedHikes;
    }

    public static Map<Marker, Long> displayMarkers(List<HikeData> hikesToDisplay, GoogleMap map) {
        Map<Marker, Long> markerByHike = new HashMap<>();
        for (HikeData hike: hikesToDisplay) {
            MarkerOptions startMarkerOptions = new MarkerOptions()
                    .position(hike.getStartLocation())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            MarkerOptions finishMarkerOptions = new MarkerOptions()
                    .position(hike.getFinishLocation())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            Marker startMarker = map.addMarker(startMarkerOptions);
            Marker finishMarker = map.addMarker(finishMarkerOptions);

            markerByHike.put(startMarker, hike.getHikeId());
            markerByHike.put(finishMarker, hike.getHikeId());
        }
        return markerByHike;
    }

    public static void setOnMapClick(Boolean mapClickable, List<Polyline> displayedHikes, GoogleMap map) {
        map.getUiSettings().setAllGesturesEnabled(mapClickable);
    }

    public static void setCamera(List<HikeData> hikesToDisplay, GoogleMap map, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        LatLngBounds boundingBox = hikesToDisplay.get(0).getBoundingBox();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundingBox, screenWidth, screenHeight / 3, 60));
    }
}