package ch.epfl.sweng.team7.hikingapp;

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

    private static final int HIKE_LINE_COLOR = 0xff000066;

    public static List<Polyline> displayHikes(List<HikeData> hikesToDisplay, GoogleMap map) {
        List<Polyline> displayedHikes = new ArrayList<>();
        for (HikeData hike : hikesToDisplay) {
            PolylineOptions polylineOptions = new PolylineOptions();
            List<HikePoint> databaseHikePoints = hike.getHikePoints();
            for (HikePoint hikePoint : databaseHikePoints) {
                polylineOptions.add(hikePoint.getPosition())
                        .width(5)
                        .color(HIKE_LINE_COLOR);
            }
            Polyline polyline = map.addPolyline(polylineOptions);
            displayedHikes.add(polyline);
        }
        return displayedHikes;
    }

    public static Map<Marker, Long> displayMarkers(List<HikeData> hikesToDisplay, GoogleMap map) {
        Map<Marker, Long> markerByHike = new HashMap<>();
        for (HikeData hike : hikesToDisplay) {
            MarkerOptions startMarkerOptions = new MarkerOptions()
                    .position(hike.getStartLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start_hike));
            MarkerOptions finishMarkerOptions = new MarkerOptions()
                    .position(hike.getFinishLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_finish_hike));

            Marker startMarker = map.addMarker(startMarkerOptions);
            Marker finishMarker = map.addMarker(finishMarkerOptions);

            markerByHike.put(startMarker, hike.getHikeId());
            markerByHike.put(finishMarker, hike.getHikeId());

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
        }
        return markerByHike;
    }

    public static void setOnMapClick(Boolean mapClickable, List<Polyline> displayedHikes, GoogleMap map) {
        map.getUiSettings().setAllGesturesEnabled(mapClickable);
    }

    public static void setCamera(List<HikeData> hikesToDisplay, GoogleMap map, int mapWidth, int mapHeight) {
        LatLngBounds boundingBox = hikesToDisplay.get(0).getBoundingBox();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundingBox, mapWidth, mapHeight, 60));
    }

    public static void displayAnnotations(List<HikeData> hikesToDisplay, GoogleMap map) {
        //Display de Annotations
        for (HikeData hike : hikesToDisplay) {
            List<MarkerOptions> annotations = new ArrayList<>();
            if (hike.getAnnotations() != null || hike.getAnnotations().size() != 0) {
                for (int i = 0; i < hike.getAnnotations().size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(hike.getAnnotations().get(i).getRawHikePoint().getPosition())
                            .title("Annotation")
                            .snippet(hike.getAnnotations().get(i).getAnnotation())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    annotations.add(markerOptions);
                    final Marker textAnnotation = map.addMarker(markerOptions);
                    textAnnotation.showInfoWindow();
                }
            }
        }
    }
}