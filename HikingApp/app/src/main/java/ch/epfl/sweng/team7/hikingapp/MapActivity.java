package ch.epfl.sweng.team7.hikingapp;

import android.content.Intent;
import android.graphics.Point;
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

import com.google.android.gms.maps.CameraUpdateFactory;
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

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.DefaultHikeData;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.network.RawHikeData;
import ch.epfl.sweng.team7.network.RawHikePoint;

import static android.location.Location.distanceBetween;

public class MapActivity extends FragmentActivity {

    private final static String LOG_FLAG = "Activity_Map";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPSManager gps = GPSManager.getInstance();
    private List<HikeData> hikesInWindow;

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
    private void setUpMap()  {

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;




        hikesInWindow = testHikeList();
        for (int i = 0; i < hikesInWindow.size(); i++) {
            HikeData hike = hikesInWindow.get(i);
            displayMarkers(hike);
            PolylineOptions polylineOptions = displayOneHike(hike);


        }



        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(hikesInWindow.get(0).getBoundingBox(), screenWidth, screenHeight, screenWidth / 10));


        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                TableLayout mapTableLayout = (TableLayout) findViewById(R.id.mapTextTable);
                double shortestDistance = 1;
                List<LatLng> polylinePoints = polylineOptions.getPoints();
                for (LatLng polylinePoint : polylinePoints) {
                    float[] distanceBetween = new float[1];

                    //Computes the approximate distance (in meters) between polyLinePoint and point.
                    //Returns the result as the first element of the float array distanceBetween
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
        });*/
    }



    private PolylineOptions displayOneHike(final HikeData hike) {
        PolylineOptions testPolyline = new PolylineOptions();

        List<HikePoint> databaseHikePoints = hike.getHikePoints();
        for (HikePoint hikePoint: databaseHikePoints) {
            testPolyline.add(hikePoint.getPosition());
        }
        Polyline polyline = mMap.addPolyline(testPolyline);
        return testPolyline;

    }

    private void displayMarkers(final HikeData hike) {
        MarkerOptions startMarker = new MarkerOptions()
                .position(hike.getStartLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        MarkerOptions finishMarker = new MarkerOptions()
                .position(hike.getFinishLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return onMarkerClickHelper(marker);
            }
        });
        mMap.addMarker(startMarker);
        mMap.addMarker(finishMarker);
    }

    private boolean onMarkerClickHelper(Marker marker) {
        for (int i = 0; i < hikesInWindow.size(); i++) {
            HikeData hike = hikesInWindow.get(i);
            if (marker.getPosition().equals(hike.getStartLocation()) ||
                    marker.getPosition().equals(hike.getFinishLocation())) {
                displayHikeInfo(hike);
                return true;
            }
        }
        return true;
    }

    private void displayHikeInfo(HikeData hike) {
        TableLayout mapTableLayout = (TableLayout)findViewById(R.id.mapTextTable);
        mapTableLayout.removeAllViews();

        TextView hikeTitle = new TextView(this);
        hikeTitle.setText(getResources().getString(R.string.hikeNumberText,hike.getHikeId()));
        hikeTitle.setTextSize(20);

        TextView hikeOwner = new TextView(this);
        hikeOwner.setText(getResources().getString(R.string.hikeOwnerText, hike.getOwnerId()));

        TextView hikeDistance = new TextView(this);
        hikeDistance.setText(getResources().getString(R.string.hikeDistanceText, (long) hike.getDistance()));

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

    private static List<HikeData> testHikeList() {
        //List<HikeData>
        HikeData hike1 = testHike1();
        HikeData hike2 = testHike2();
        List<HikeData> hikeList = new ArrayList<>();
        hikeList.add(hike1);
        hikeList.add(hike2);
        return hikeList;
    }

    //Hike object for testing purposes
    private static DefaultHikeData testHike1() {
        long hikeId = 1;
        long ownerId = 1;
        Date date = new Date(1000101);
        List<RawHikePoint> rawHikePoints;
        LatLng startLocation = new LatLng(47.445172, -80.570527);
        LatLng finishLocation = new LatLng(47.377527, -80.615061);
        RawHikeData mRawHikeData;

        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(1000101), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.435175, -80.575680), new Date(1000102), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.428269, -80.565859), new Date(1000103), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.420308, -80.579278), new Date(1000104), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.414782, -80.573249), new Date(1000105), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.396026, -80.583556), new Date(1000106), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.387139, -80.602226), new Date(1000107), 3.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(1000108), 2.0));
        mRawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        return new DefaultHikeData(mRawHikeData);
    }

    //Hike object for testing purposes
    private static DefaultHikeData testHike2() {
        long hikeId = 2;
        long ownerId = 2;
        Date date = new Date(1000201);
        List<RawHikePoint> rawHikePoints;
        LatLng startLocation = new LatLng(47.395043, -80.664059);
        LatLng finishLocation = new LatLng(47.400156, -80.552308);
        RawHikeData mRawHikeData;

        rawHikePoints = new ArrayList<>();
        rawHikePoints.add(new RawHikePoint(startLocation, new Date(1000201), 1.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.397019, -80.653931), new Date(1000202), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.400737, -80.632989), new Date(1000203), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.400621, -80.604493), new Date(1000204), 3.0));
        rawHikePoints.add(new RawHikePoint(new LatLng(47.399807, -80.577199), new Date(1000205), 3.0));
        rawHikePoints.add(new RawHikePoint(finishLocation, new Date(1000206), 2.0));
        mRawHikeData = new RawHikeData(hikeId, ownerId, date, rawHikePoints);
        return new DefaultHikeData(mRawHikeData);
    }




}
