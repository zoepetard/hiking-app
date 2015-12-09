package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.gpsService.containers.coordinates.GeoCoords;
import ch.epfl.sweng.team7.hikingapp.mapActivityElements.BottomInfoView;

import static android.location.Location.distanceBetween;

public class MapActivity extends FragmentActivity {

    private final static String LOG_FLAG = "Activity_Map";
    private final static int DEFAULT_ZOOM = 15;
    private final static int BOTTOM_TABLE_ACCESS_ID = 1;
    private final static String EXTRA_HIKE_ID =
            "ch.epfl.sweng.team7.hikingapp.HIKE_ID";
    private final static String EXTRA_EXIT = "exit";
    private static final int HIKE_LINE_COLOR = 0xff000066;
    private static LatLngBounds bounds;
    private static LatLng mUserLocation;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPSManager mGps = GPSManager.getInstance();
    private BottomInfoView mBottomTable = BottomInfoView.getInstance();
    private DataManager mDataManager = DataManager.getInstance();
    private List<HikeData> mHikesInWindow;
    //private Map<Marker, Long> mMarkerByHike = new HashMap<>();
    //private List<Pair<Polyline, Long>> mDisplayedHikes = new ArrayList<>();
    private List<DisplayedHike> mDisplayedHikes = new ArrayList<>();

    private boolean mFollowingUser = false;

    private Polyline mPolyRef;
    private PolylineOptions mCurHike;

    private SearchView mSearchView;
    private ListView mSuggestionListView;
    private List<Address> mSuggestionList = new ArrayList<>();
    private SuggestionAdapter mSuggestionAdapter;
    private Geocoder mGeocoder;
    public final static String EXTRA_BOUNDS =
            "ch.epfl.sweng.team7.hikingapp.BOUNDS";
    private static int MAX_SEARCH_SUGGESTIONS = 10;
    private static int MIN_QUERY_LENGTH_FOR_SUGGESTIONS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        mGps.startService(this);

        if (getIntent().getBooleanExtra(EXTRA_EXIT, false)) {
            finish();
        }

        // nav drawer setup
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer, null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View mapView = getLayoutInflater().inflate(R.layout.activity_map, null);
        mainContentFrame.addView(mapView);

        setUpMapIfNeeded();

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(
                navDrawerList, navDrawerView.getContext(), this);

        //creates a start/stop tracking button
        createTrackingToggleButton();

        //creates a pause/resume tracking button
        createPauseTrackingButton();

        //Initializes the BottomInfoView
        createBottomInfoView();

        setGoToHikesButtonListener();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        setUpSearchView();

        mGeocoder = new Geocoder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGps.bindService(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGps.unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGps.stopService();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processNewIntent();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(EXTRA_EXIT, true);
        startActivity(intent);
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

    public LatLngBounds getBounds() {
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
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mUserLocation = getUserPosition();
        LatLngBounds initialBounds = guessNewLatLng(mUserLocation, mUserLocation, 0.5);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initialBounds, mScreenWidth, mScreenHeight, 30));

        List<HikeData> hikesFound = new ArrayList<>();
        boolean firstHike = true;
        new DownloadHikeList().execute(new DownloadHikeParams(hikesFound, initialBounds, firstHike));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mSearchView.onActionViewCollapsed(); // remove focus from searchview
                onMapClickHelper(point);
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                onCameraChangeHelper();
                mFollowingUser = false;
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (mGps.tracking()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mFollowingUser) focusOnLatLng(latLng);
                    if (!mGps.paused()) {
                        List<LatLng> points = mPolyRef.getPoints();
                        points.add(latLng);
                        mPolyRef.setPoints(points);
                    }
                }
            }
        });
    }

    private void processNewIntent() {
        Intent intent = getIntent();
        boolean displaySingleHike = false;

        if (intent != null && intent.hasExtra(HikeInfoActivity.HIKE_ID)) {
            String hikeIdStr = intent.getStringExtra(HikeInfoActivity.HIKE_ID);

            if (hikeIdStr != null) {
                long intentHikeId = Long.valueOf(hikeIdStr);
                for (DisplayedHike displayedHike : mDisplayedHikes) {
                    if (intentHikeId == displayedHike.getId()) {
                        displaySingleHike = true;
                    }
                }
                if (displaySingleHike) {
                    for (DisplayedHike displayedHike : mDisplayedHikes) {
                        if (displayedHike.getId() != intentHikeId) {
                            displayedHike.getPolyline().remove();
                            displayedHike.getStartMarker().remove();
                            displayedHike.getFinishMarker().remove();
                        }
                    }
                    for (HikeData hikeData : mHikesInWindow) {
                        if (hikeData.getHikeId() == intentHikeId) {
                            LatLngBounds newBounds = hikeData.getBoundingBox();
                            int displayHeight = (int) (mScreenHeight * 0.7);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(newBounds, mScreenWidth, displayHeight, 30));
                        }
                    }

                }
            }
        }
    }

    private static class DownloadHikeParams {
        List<HikeData> mHikesFound;
        LatLngBounds mOldBounds;
        boolean mFirstHike;

        DownloadHikeParams(List<HikeData> hikesFound, LatLngBounds oldBounds, boolean firstHike) {
            mHikesFound = hikesFound;
            mOldBounds = oldBounds;
            mFirstHike = firstHike;
        }
    }

    private class DownloadHikeList extends AsyncTask<DownloadHikeParams, Void, DownloadHikeParams> {
        @Override
        protected DownloadHikeParams doInBackground(DownloadHikeParams... params) {
            try {
                LatLngBounds oldBounds = params[0].mOldBounds;
                boolean firstHike = params[0].mFirstHike;
                List<HikeData> hikesFound = mDataManager.getHikesInWindow(oldBounds);
                return new DownloadHikeParams(hikesFound, oldBounds, firstHike);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(DownloadHikeParams postExecuteParams) {

            // Fixes bug #114: On error, doInBackground will abort with null
            if (postExecuteParams == null) {
                return;
            }

            List<HikeData> hikesFound = postExecuteParams.mHikesFound;
            LatLngBounds oldBounds = postExecuteParams.mOldBounds;
            boolean firstHike = postExecuteParams.mFirstHike;

            if (hikesFound != null) {
                if (hikesFound.size() > 0) {
                    displayMap(hikesFound, oldBounds, firstHike);
                } else {
                    LatLngBounds newBounds = guessNewLatLng(oldBounds.southwest, oldBounds.northeast, 0.5);
                    //new DownloadHikeList().execute(new DownloadHikeParams(hikesFound, newBounds, firstHike));
                    // TODO(zoe) implement alternative to infinte recursion
                }
            }
        }
    }

    private void displayMap(List<HikeData> hikesFound, LatLngBounds bounds, boolean firstHike) {

        mHikesInWindow = hikesFound;
        LatLngBounds.Builder boundingBoxBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < mHikesInWindow.size(); i++) {
            HikeData hike = mHikesInWindow.get(i);
            Polyline polyline = displayHike(hike);
            Pair<Marker, Marker> markers = displayMarkers(hike);
            mDisplayedHikes.add(new DisplayedHike(hike.getHikeId(), polyline, markers.first, markers.second));
            boundingBoxBuilder.include(hike.getStartLocation());
            boundingBoxBuilder.include(hike.getFinishLocation());
        }

        if (firstHike) {
            boundingBoxBuilder.include(mUserLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundingBoxBuilder.build(), mScreenWidth, mScreenHeight, 30));
        }
    }

    private Pair<Marker, Marker> displayMarkers(final HikeData hike) {
        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(hike.getStartLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start_hike));
        MarkerOptions finishMarkerOptions = new MarkerOptions()
                .position(hike.getFinishLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_finish_hike));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return onMarkerClickHelper(marker);
            }
        });
        Marker startMarker = mMap.addMarker(startMarkerOptions);
        Marker finishMarker = mMap.addMarker(finishMarkerOptions);

        return Pair.create(startMarker, finishMarker);
    }

    private boolean onMarkerClickHelper(Marker marker) {
        for (DisplayedHike displayedHike : mDisplayedHikes) {
            if (marker.equals(displayedHike.getStartMarker())
                    || marker.equals(displayedHike.getFinishMarker())) {
                long hikeId = displayedHike.getId();
                try {
                    displayHikeInfo(mDataManager.getHike(hikeId));
                } catch (DataManagerException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return true;
    }

    private Polyline displayHike(final HikeData hike) {
        PolylineOptions polylineOptions = new PolylineOptions();
        List<HikePoint> databaseHikePoints = hike.getHikePoints();
        for (HikePoint hikePoint : databaseHikePoints) {
            polylineOptions.add(hikePoint.getPosition())
                    .width(5)
                    .color(HIKE_LINE_COLOR);
        }
        return mMap.addPolyline(polylineOptions);
    }

    private void onMapClickHelper(LatLng point) {
        if (mHikesInWindow != null) {
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
    }

    private void displayHikeInfo(final HikeData hike) {
        mBottomTable.setTitle(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeNumberText, hike.getHikeId()));
        mBottomTable.clearInfoLines(BOTTOM_TABLE_ACCESS_ID);
        mBottomTable.addInfoLine(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeOwnerText, hike.getOwnerId()));
        mBottomTable.addInfoLine(BOTTOM_TABLE_ACCESS_ID, getResources().getString(R.string.hikeDistanceText, (long) hike.getDistance() / 1000));
        mBottomTable.setOnClickListener(BOTTOM_TABLE_ACCESS_ID, new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HikeInfoActivity.class);
                intent.putExtra(EXTRA_HIKE_ID, Long.toString(hike.getHikeId()));
                startActivity(intent);
            }
        });

        mBottomTable.show(BOTTOM_TABLE_ACCESS_ID);
    }

    private void createTrackingToggleButton() {
        Button toggleButton = new Button(this);
        toggleButton.setText((mGps.tracking()) ? R.string.button_stop_tracking : R.string.button_start_tracking);
        toggleButton.setId(R.id.button_tracking_toggle);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        toggleButton.setLayoutParams(lp);
        layout.addView(toggleButton, lp);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mGps.enabled()) {
                    mGps.toggleTracking();
                    updateButtonDisplay();
                    if (mGps.tracking() && !mGps.paused()) {
                        startHikeDisplay();
                        Log.d(LOG_FLAG, "Starting hike display");
                    }
                } else {
                    displayToast(getResources().getString(R.string.gps_location_not_enabled));
                }
            }
        });
    }

    public void updateButtonDisplay() {

        //Start/stop button update
        Button toggleButton = (Button) findViewById(R.id.button_tracking_toggle);
        Button pauseButton = (Button) findViewById(R.id.button_tracking_pause);
        if (mGps.tracking()) {
            toggleButton.setText(R.string.button_stop_tracking);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setText((mGps.paused()) ? R.string.button_resume_tracking : R.string.button_pause_tracking);
        } else {
            toggleButton.setText(R.string.button_start_tracking);
            pauseButton.setVisibility(View.INVISIBLE);
        }
    }

    private void createPauseTrackingButton() {
        Button pauseButton = new Button(this);
        pauseButton.setText(R.string.button_pause_tracking);
        pauseButton.setId(R.id.button_tracking_pause);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.LEFT_OF, R.id.button_tracking_toggle);

        pauseButton.setLayoutParams(lp);
        layout.addView(pauseButton, lp);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGps.togglePause();
                updateButtonDisplay();
            }
        });
        pauseButton.setVisibility(View.INVISIBLE);
    }

    private void createBottomInfoView() {
        mBottomTable.initialize(this);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(mBottomTable.getView(), lp);
    }

    private void onCameraChangeHelper() {
        LatLngBounds currentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        List<HikeData> hikesFound = new ArrayList<>();
        boolean firstHike = false;
        new DownloadHikeList().execute(new DownloadHikeParams(hikesFound, currentBounds, firstHike));
    }

    private LatLngBounds guessNewLatLng(LatLng southWest, LatLng northEast, double delta) {
        LatLng guessSW = new LatLng(southWest.latitude - delta, southWest.longitude - delta);
        LatLng guessNE = new LatLng(northEast.latitude + delta, northEast.longitude + delta);
        return new LatLngBounds(guessSW, guessNE);
    }

    private void setGoToHikesButtonListener() {
        Button goHikeButton = (Button) findViewById(R.id.go_hikes_button);
        goHikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLngBounds bounds = getBounds();
                Bundle bound = new Bundle();
                bound.putParcelable("sw", bounds.southwest);
                bound.putParcelable("ne", bounds.northeast);
                Intent intent = new Intent(v.getContext(), HikeListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(EXTRA_BOUNDS, bound);
                startActivity(intent);
            }
        });
    }

    private void setUpSearchView() {

        mSearchView = (SearchView) findViewById(R.id.search_map_view);
        mSuggestionListView = (ListView) findViewById(R.id.search_suggestions_list);
        mSuggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSearchView.onActionViewCollapsed();
                mSuggestionListView.setVisibility(View.GONE);

                // move the camera to the location corresponding to clicked item
                if (mSuggestionList.size() != 0) {
                    Address clickedLocation = mSuggestionList.get(position);
                    LatLng latLng = new LatLng(clickedLocation.getLatitude(), clickedLocation.getLongitude());

                    // get bounding box
                    Bundle clickedLocationExtras = clickedLocation.getExtras();
                    Object bounds = null;
                    if (clickedLocationExtras != null) {
                        bounds = clickedLocationExtras.get(EXTRA_BOUNDS);
                    }
                    if (bounds != null && bounds instanceof LatLngBounds) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds((LatLngBounds) bounds, 60));
                    } else {
                        focusOnLatLng(latLng);
                    }

                    // load hikes at new location
                    onCameraChangeHelper();
                }
            }
        });

        mSuggestionAdapter = new SuggestionAdapter(this, mSuggestionList);
        mSuggestionListView.setAdapter(mSuggestionAdapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new HikeSearcher().execute(new SearchHikeParams(query, true));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new HikeSearcher().execute(new SearchHikeParams(newText, false));
                return false;
            }
        });
    }

    private void displayToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class HikeSearcher extends AsyncTask<SearchHikeParams, Void, Boolean> {

        /**
         * Searches for a locations from a query
         *
         * @param params - Query & boolean indicating whether the user is done typing
         * @return boolean informing postexecute to either hide or show the suggestions
         */
        @Override
        protected Boolean doInBackground(SearchHikeParams... params) {

            String query = params[0].mQuery;
            boolean isDoneTyping = params[0].mIsDoneTyping;

            if (query.length() <= MIN_QUERY_LENGTH_FOR_SUGGESTIONS && !isDoneTyping) {
                return false;
            }

            List<Address> suggestions = new ArrayList<>();

            List<HikeData> hikeDataList = new ArrayList<>();
            try {
                hikeDataList = mDataManager.searchHike(query);
            } catch (DataManagerException e) {
                Log.d(LOG_FLAG, e.getMessage());
            }
            // check if local results and add to suggestions
            for (HikeData hikeData : hikeDataList) {

                Address address = new Address(Locale.ROOT);

                address.setFeatureName(hikeData.getTitle());
                LatLng latLng = hikeData.getHikeLocation();
                address.setLatitude(latLng.latitude);
                address.setLongitude(latLng.longitude);

                Bundle boundingBox = new Bundle();
                boundingBox.putParcelable(EXTRA_BOUNDS, hikeData.getBoundingBox());
                address.setExtras(boundingBox);

                suggestions.add(address);
            }

            try {
                List<Address> locationAddressList = mGeocoder.getFromLocationName(query, MAX_SEARCH_SUGGESTIONS);
                for (Address address : locationAddressList) {
                    suggestions.add(address);
                }
                if (isDoneTyping && suggestions.size() == 0) {
                    Address address = new Address(Locale.ROOT);
                    address.setFeatureName(getResources().getString(R.string.search_no_results));
                    suggestions.add(address);
                }
            } catch (IOException e) {
                Address address = new Address(Locale.ROOT);
                address.setFeatureName(getResources().getString(R.string.search_error));
                suggestions.add(address);
            }

            mSuggestionList.clear();
            mSuggestionList.addAll(suggestions);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean listIsVisible) {
            if (listIsVisible) {
                mSuggestionListView.setVisibility(View.VISIBLE);
            } else {
                mSuggestionListView.setVisibility(View.GONE);
            }
            mSuggestionAdapter.notifyDataSetChanged();
        }
    }

    private LatLng getUserPosition() {
        if (mGps.enabled()) {
            GeoCoords userGeoCoords = mGps.getCurrentCoords();
            return userGeoCoords.toLatLng();
        } else {
            double switzerlandLatitude = 46.4;
            double switzerlandLongitude = 6.4;
            return new LatLng(switzerlandLatitude, switzerlandLongitude);
        }
    }

    public void focusOnLatLng(LatLng latLng) {
        if (latLng != null) {
            CameraUpdate target = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
            mMap.animateCamera(target);
        }
    }

    public void startFollowingUser() {
        mFollowingUser = true;
    }

    private void startHikeDisplay() {
        mCurHike = new PolylineOptions();
        mPolyRef = mMap.addPolyline(mCurHike);
    }

    public void stopHikeDisplay() {
        //TODO do something when we stop hiking..?
        mPolyRef.remove();
    }


    private class SuggestionAdapter extends ArrayAdapter<Address> {

        private List<Address> mAddressList;

        public SuggestionAdapter(Context context, List<Address> addressList) {
            super(context, android.R.layout.simple_list_item_2, android.R.id.text1, addressList);
            mAddressList = addressList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView upperText = (TextView) view.findViewById(android.R.id.text1);
            TextView lowerText = (TextView) view.findViewById(android.R.id.text2);

            upperText.setText(mAddressList.get(position).getFeatureName());
            if (mAddressList.get(position).getCountryName() != null) {
                lowerText.setText(mAddressList.get(position).getCountryName());
            } else {
                lowerText.setText("");
            }
            return view;
        }
    }

    private class SearchHikeParams {

        String mQuery;
        boolean mIsDoneTyping;

        SearchHikeParams(String query, boolean isDoneTyping) {
            mQuery = query;
            mIsDoneTyping = isDoneTyping;
        }
    }

}
