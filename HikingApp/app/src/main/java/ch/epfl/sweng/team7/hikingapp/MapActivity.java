package ch.epfl.sweng.team7.hikingapp;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.team7.database.DefaultHikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static android.location.Location.distanceBetween;

public class MapActivity extends FragmentActivity {

    private final static String LOG_FLAG = "Activity_Map";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPSManager gps = GPSManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        gps.startService(this);

        // nav drawer setup
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer,null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View mapView = getLayoutInflater().inflate(R.layout.activity_map, null);
        mainContentFrame.addView(mapView);

        setUpMapIfNeeded();

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList,navDrawerView.getContext());


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        displayMarkers();
        final PolylineOptions testPolyline = displayTestPoints();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                TableLayout mapTableLayout = (TableLayout) findViewById(R.id.mapTextTable);
                double shortestDistance = 200000;
                List<LatLng> polylinePoints = testPolyline.getPoints();
                for (LatLng polylinePoint : polylinePoints) {
                    float[] distanceBetween = new float[1];
                    distanceBetween(polylinePoint.latitude, polylinePoint.longitude,
                            point.latitude, point.longitude, distanceBetween);
                    double distance = distanceBetween[0];
                    if (distance < shortestDistance) {
                        displayHikeInfo();
                        return;
                    }
                }
                mapTableLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private PolylineOptions displayTestPoints() {
        PolylineOptions testPolyline = new PolylineOptions();
        List<HikePoint> testHikePoints = testHike().getHikePoints();
        for (HikePoint hikePoint: testHikePoints) {
            testPolyline.add(hikePoint.getPosition());
        }
        Polyline polyline = mMap.addPolyline(testPolyline);
        return testPolyline;

    }

    private void displayMarkers() {
        MarkerOptions startMarker = new MarkerOptions()
                .position(testHike().getStartLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        MarkerOptions finishMarker = new MarkerOptions()
                .position(testHike().getFinishLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                displayHikeInfo();
                return true;
            }
        });
        mMap.addMarker(startMarker);
        mMap.addMarker(finishMarker);
    }

    private void displayHikeInfo() {
        TableLayout mapTableLayout = (TableLayout)findViewById(R.id.mapTextTable);
        mapTableLayout.removeAllViews();

        TextView hikeTitle = new TextView(this);
        hikeTitle.setText(getResources().getString(R.string.hikeNumberText) + Integer.toString((int) testHike().getHikeId()));
        hikeTitle.setTextSize(20);

        TextView hikeOwner = new TextView(this);
        hikeOwner.setText(getResources().getString(R.string.hikeOwnerText) + Integer.toString((int)testHike().getOwnerId()));

        TextView hikeDistance = new TextView(this);
        hikeDistance.setText(getResources().getString(R.string.hikeDistanceText) + Integer.toString((int)testHike().getDistance()) + getResources().getString(R.string.km));

        mapTableLayout.addView(hikeTitle);
        mapTableLayout.addView(hikeOwner);
        mapTableLayout.addView(hikeDistance);
        mapTableLayout.setVisibility(View.VISIBLE);
        mapTableLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HikeInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    //Hike object for testing purposes
    private DefaultHikeData testHike() {
        long hikeId = 1;
        long ownerId = 1;
        Date date = new Date(1000101);
        List<RawHikePoint> rawHikePoints;
        LatLng startLocation = new LatLng(0,0);
        LatLng finishLocation = new LatLng(15, 15);
        RawHikeData mRawHikeData;

        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(1000101), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(2,2), new Date(1000102), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(5,5), new Date(1000103), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(10,10), new Date(1000104), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(12,12), new Date(1000105), 3.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(1000106), 2.0));
        mRawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        return new DefaultHikeData(mRawHikeData);
    }

}
