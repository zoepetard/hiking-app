package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;

public class HikeListActivity extends Activity {
    private DataManager dataManager = DataManager.getInstance();
    private LatLngBounds bounds;

    private final static String LOG_FLAG = "Activity_HikeList";
    public final static String EXTRA_HIKE_ID =
            "ch.epfl.sweng.team7.hikingapp.HIKE_ID";

    //Displays a list of nearby hikes, with a map, the distance and the rating.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_list);
        setContentView(R.layout.navigation_drawer);

        // Inflate Navigation Drawer with main content
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer,null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View hikeListView = getLayoutInflater().inflate(R.layout.activity_hike_list, null);
        mainContentFrame.addView(hikeListView);

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(
                navDrawerList, navDrawerView.getContext(), this);

        Bundle bound = getIntent().getParcelableExtra(MapActivity.EXTRA_BOUNDS);
        if (bound != null) {
            LatLng sw = bound.getParcelable("sw");
            LatLng ne = bound.getParcelable("ne");
            bounds = new LatLngBounds(sw, ne);
        } else {
            // display all hikes if no bounds specified
            bounds = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 179));
        }
        new GetMultHikeAsync().execute(bounds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hike_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void backToMap(View view) {
        finish();
    }

    private class GetMultHikeAsync extends AsyncTask<LatLngBounds, Void, List<HikeData> > {

        @Override
        protected List<HikeData> doInBackground(LatLngBounds... bounds) {
            try {
                return dataManager.getHikesInWindow(bounds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<HikeData> results) {
            if (results != null) {
                displayHikes(results);
            }
        }
    }

    private void displayHikes(final List<HikeData> results) {
        CustomListAdapter adapter = new CustomListAdapter(this, results);
        ListView listView = (ListView) findViewById(R.id.hike_list_view);
        listView.setAdapter(adapter);
    }
}