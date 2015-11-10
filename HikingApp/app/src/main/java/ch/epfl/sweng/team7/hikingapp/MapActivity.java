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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import ch.epfl.sweng.team7.gpsService.GPSManager;

public class MapActivity extends FragmentActivity {

    private final static String LOG_FLAG = "Activity_Map";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GPSManager gps = GPSManager.getInstance();
    private GoogleMap.OnMyLocationChangeListener locationChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        // nav drawer setup
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View mapView = getLayoutInflater().inflate(R.layout.activity_map, null);
        mainContentFrame.addView(mapView);

        setUpMapIfNeeded();

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        loadNavDrawerItems(navDrawerList);
        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = (String) parent.getItemAtPosition(position);
                Intent intent;

                switch (itemText) {
                    case "Account":
                        intent = new Intent(view.getContext(), ChangeNicknameActivity.class);
                        startActivity(intent);
                        break;
                    case "Map":
                        intent = new Intent(view.getContext(), MapActivity.class);
                        startActivity(intent);
                        break;
                    case "Hikes":
                        intent = new Intent(view.getContext(), HikeListActivity.class);
                        startActivity(intent);
                        break;
                    case "Logout":
                        intent = new Intent(view.getContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpLocationListener() {
        locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                gps.updateCurrentLocation(location);
                Log.d(LOG_FLAG, "GPS State: " + gps.toString());
            }
        };
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(locationChangeListener);
        displayTestPoints();
    }

    private void displayTestPoints() {
        LatLng origin = new LatLng(0,0);
        LatLng accra = new LatLng(5.615986, -0.171533);
        LatLng saoTome = new LatLng(0.362365, 6.558835);

        PolylineOptions testTriangle = new PolylineOptions()
                .add(origin)
                .add(accra)
                .add(saoTome)
                .add(origin);

        Polyline polyline = mMap.addPolyline(testTriangle);
    }

    private void loadNavDrawerItems(ListView navDrawerList) {

        String[] listViewItems = {"Account", "Map", "Hikes", "Logout"};
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);

    }

}
