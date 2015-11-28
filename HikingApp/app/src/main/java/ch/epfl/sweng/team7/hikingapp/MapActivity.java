package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.location.Geocoder;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private final static int BOTTOM_TABLE_ACCESS_ID = 1;
    private final static String EXTRA_HIKE_ID =
            "ch.epfl.sweng.team7.hikingapp.HIKE_ID";

    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static LatLngBounds bounds;
    private GPSManager mGps = GPSManager.getInstance();
    private BottomInfoView mBottomTable = BottomInfoView.getInstance();
    private DataManager mDataManager = DataManager.getInstance();
    private List<HikeData> mHikesInWindow;
    private Map<Marker, Long> mMarkerByHike = new HashMap<>();

    private SearchView searchView;
    private ListView suggestionListView;
    private List<Address> suggestionList = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;
    private Geocoder mGeocoder;
    private List<Address> locationAddressList = new ArrayList<>();
    public final static String EXTRA_BOUNDS =
            "ch.epfl.sweng.team7.hikingapp.BOUNDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        mGps.startService(this);

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

        setGoToHikesButtonListener();

        setUpSearchView();

        mGeocoder = new Geocoder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGps.bindService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGps.unbindService(this);
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

        LatLng userLatLng = getUserPosition();
        LatLngBounds initialBounds = guessNewLatLng(userLatLng, userLatLng, 0.5);

        List<HikeData> hikesFound = new ArrayList<>();
        boolean firstHike = true;
        new DownloadHikeList().execute(new DownloadHikeParams(hikesFound, initialBounds, firstHike));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                searchView.onActionViewCollapsed(); // remove focus from searchview
                onMapClickHelper(point);
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                onCameraChangeHelper();
            }
        });
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
            if (postExecuteParams == null) {
                return;
            } // TODO remove this when done

            List<HikeData> hikesFound = postExecuteParams.mHikesFound;
            LatLngBounds oldBounds = postExecuteParams.mOldBounds;
            boolean firstHike = postExecuteParams.mFirstHike;

            if (hikesFound != null) {
                if (hikesFound.size() > 0) {
                    displayMap(hikesFound, oldBounds, firstHike);
                } else {
                    LatLngBounds newBounds = guessNewLatLng(oldBounds.southwest, oldBounds.northeast, 0.5);
                    new DownloadHikeList().execute(new DownloadHikeParams(hikesFound, newBounds, firstHike));
                }
            }
        }
    }

    private void displayMap(List<HikeData> hikesFound, LatLngBounds bounds, boolean firstHike) {

        mHikesInWindow = hikesFound;
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

        if (firstHike) {
            LatLng userLatLng = getUserPosition();
            boundingBoxBuilder.include(userLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundingBoxBuilder.build(), screenWidth, screenHeight, 30));
        }
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
                mGps.toggleTracking();
                Button toggleButton = (Button) findViewById(R.id.button_tracking_toggle);
                toggleButton.setText((mGps.tracking()) ? R.string.button_stop_tracking : R.string.button_start_tracking);
            }
        });
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

        searchView = (SearchView) findViewById(R.id.search_map_view);
        suggestionListView = (ListView) findViewById(R.id.search_suggestions_list);
        suggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchView.onActionViewCollapsed();
                // move the camera to the location corresponding to clicked item
                suggestionListView.setVisibility(View.GONE);
                if (locationAddressList.size() != 0) {
                    Address clickedLocation = locationAddressList.get(position);

                    clickedLocation.getLatitude();
                    clickedLocation.getLongitude();
                    LatLng latLng = new LatLng(clickedLocation.getLatitude(), clickedLocation.getLongitude());

                    CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                }
                // TODO display hikes from bounds
            }
        });

        suggestionAdapter = new SuggestionAdapter(this, suggestionList);
        suggestionListView.setAdapter(suggestionAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadSearchSuggestions(true, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSearchSuggestions(false, newText);
                return false;
            }

            public void loadSearchSuggestions(boolean isDoneTyping, String searchString) {

                if (searchString.length() <= 3 && !isDoneTyping) {

                    suggestionListView.setVisibility(View.GONE);
                    return;
                }

                List<Address> suggestions = new ArrayList<>();
                try {
                    locationAddressList = mGeocoder.getFromLocationName(searchString, 5);
                    for (int i = 0; i < locationAddressList.size(); i++) {
                        suggestions.add(locationAddressList.get(i));
                    }
                    if (isDoneTyping && suggestions.size() == 0) {
                        Address address = new Address(Locale.ENGLISH);
                        address.setFeatureName("No results");
                        suggestions.add(address);
                    }
                } catch (IOException e) {
                    Address address = new Address(Locale.ENGLISH);
                    address.setFeatureName("An error occurred");
                    suggestions.add(address);
                }
                suggestionList.clear();
                suggestionList.addAll(suggestions);
                suggestionAdapter.notifyDataSetChanged();
                suggestionListView.setVisibility(View.VISIBLE);
            }

        });
    }

    private LatLng getUserPosition() {
        double switzerlandLatitude = 46.4;
        double switzerlandLongitude = 6.4;
        if (mGps.enabled()) {
            GeoCoords userGeoCoords = mGps.getCurrentCoords();
            return userGeoCoords.toLatLng();
        } else {
            return new LatLng(switzerlandLatitude, switzerlandLongitude);
        }
    }

}


class SuggestionAdapter extends ArrayAdapter<Address> {

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
