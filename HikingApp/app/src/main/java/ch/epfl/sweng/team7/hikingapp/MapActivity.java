package ch.epfl.sweng.team7.hikingapp;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.hikingapp.mapActivityElements.BottomInfoView;

import static android.location.Location.distanceBetween;

public class MapActivity extends FragmentActivity {

    private final static String LOG_FLAG = "Activity_Map";
    private final static int BOTTOM_TABLE_ACCESS_ID = 1;
    private final static String EXTRA_HIKE_ID =
            "ch.epfl.sweng.team7.hikingapp.HIKE_ID";

    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPSManager gps = GPSManager.getInstance();
    private BottomInfoView bottomTable = BottomInfoView.getInstance();
    DataManager mDataManager = DataManager.getInstance();
    private List<HikeData> mHikesInWindow;
    private static LatLngBounds bounds;
    private Map<Marker, Long> mMarkerByHike = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        gps.startService(this);

        // nav drawer setup
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer, null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View mapView = getLayoutInflater().inflate(R.layout.activity_map, null);
        mainContentFrame.addView(mapView);

        setUpMapIfNeeded();

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList, navDrawerView.getContext());

        //creates a start/stop tracking button
        createTrackingToggleButton();

        //Initializes the BottomInfoView
        createBottomInfoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        gps.bindService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gps.unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public static LatLngBounds getBounds() {
        bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        return bounds;
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //TODO These are the bounds that should be changed to center on user's location.
        LatLngBounds bounds = new LatLngBounds(new LatLng(-90, -179), new LatLng(90, 179));
        new DownloadHikeList().execute(bounds);
    }

    private class DownloadHikeList extends AsyncTask<LatLngBounds, Void, List<HikeData>> {
        @Override
        protected List<HikeData> doInBackground(LatLngBounds... bounds) {

            try {
                return mDataManager.getHikesInWindow(bounds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<HikeData> result) {
            if (result != null) {
                displayMap(result);
            }
        }
    }

    private void displayMap(List<HikeData> result) {
        mHikesInWindow = result;
        LatLngBounds.Builder boundingBoxBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < mHikesInWindow.size(); i++) {
            HikeData hike = mHikesInWindow.get(i);
            displayMarkers(hike);
            displayHike(hike);
            boundingBoxBuilder.include(hike.getStartLocation());
            boundingBoxBuilder.include(hike.getFinishLocation());
        }

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundingBoxBuilder.build(), screenWidth, screenHeight, 30));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                onMapClickHelper(point);
            }
        });
    }

    private void displayMarkers(final HikeData hike) {
        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(hike.getStartLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        MarkerOptions finishMarkerOptions = new MarkerOptions()
                .position(hike.getFinishLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return onMarkerClickHelper(marker);
            }
        });
        Marker startMarker = mMap.addMarker(startMarkerOptions);
        Marker finishMarker = mMap.addMarker(finishMarkerOptions);

        mMarkerByHike.put(startMarker, hike.getHikeId());
        mMarkerByHike.put(finishMarker, hike.getHikeId());
    }

    private boolean onMarkerClickHelper(Marker marker) {
        if (mMarkerByHike.containsKey(marker)) {
            long hikeId = mMarkerByHike.get(marker);
            try {
                displayHikeInfo(mDataManager.getHike(hikeId));
            } catch (DataManagerException e) {
                e.printStackTrace();
            }
            return true;
        }

        return true;
    }

    private void displayHike(final HikeData hike) {
        PolylineOptions polylineOptions = new PolylineOptions();
        List<HikePoint> databaseHikePoints = hike.getHikePoints();
        for (HikePoint hikePoint : databaseHikePoints) {
            polylineOptions.add(hikePoint.getPosition());
        }
        mMap.addPolyline(polylineOptions);
    }

    private void onMapClickHelper(LatLng point) {
        for (int i = 0; i < mHikesInWindow.size(); i++) {
            HikeData hike = mHikesInWindow.get(i);
            double shortestDistance = 100;
            List<HikePoint> hikePoints = hike.getHikePoints();


            for (HikePoint hikePoint : hikePoints) {

                float[] distanceBetween = new float[1];
                //Computes the approximate distance (in meters) between polyLinePoint and point.
                //Returns the result as the first element of the float array distanceBetween
                distanceBetween(hikePoint.getPosition().latitude, hikePoint.getPosition().longitude,
                        point.latitude, point.longitude, distanceBetween);
                double distance = distanceBetween[0];

                if (distance < shortestDistance) {
                    displayHikeInfo(hike);
                    return;
                }
            }
            BottomInfoView.getInstance().hide(BOTTOM_TABLE_ACCESS_ID);
        }
    }

    private void displayHikeInfo(final HikeData hike) {
        bottomTable.setTitle(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeNumberText, hike.getHikeId()));
        bottomTable.clearInfoLines(BOTTOM_TABLE_ACCESS_ID);
        bottomTable.addInfoLine(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeOwnerText, hike.getOwnerId()));
        bottomTable.addInfoLine(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeDistanceText, (long) hike.getDistance() / 1000));
        bottomTable.setOnClickListener(BOTTOM_TABLE_ACCESS_ID, new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HikeInfoActivity.class);
                intent.putExtra(EXTRA_HIKE_ID, Long.toString(hike.getHikeId()));
                startActivity(intent);
            }
        });

        bottomTable.show(BOTTOM_TABLE_ACCESS_ID);
    }

    private void createTrackingToggleButton() {
        Button toggleButton = new Button(this);
        toggleButton.setText("Start");
        toggleButton.setId(R.id.button_tracking_toggle);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        toggleButton.setLayoutParams(lp);
        layout.addView(toggleButton, lp);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gps.toggleTracking();
                Button toggleButton = (Button) findViewById(R.id.button_tracking_toggle);
                toggleButton.setText((gps.tracking()) ? R.string.button_stop_tracking : R.string.button_start_tracking);
            }
        });
    }

    private void createBottomInfoView() {
        bottomTable.initialize(this);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(bottomTable.getView(), lp);
    }
}
