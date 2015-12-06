package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    //Creates and returns a TableRow with information about a hike.
    public TableRow getHikeRow(int i, HikeData hikeData) {

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        TableRow hikeRow = new TableRow(this);
        hikeRow.setTag("hikeRow" + Integer.toString(i));
        hikeRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        GridLayout.Spec row1 = GridLayout.spec(0);
        GridLayout.Spec row2 = GridLayout.spec(1);
        GridLayout.Spec row3 = GridLayout.spec(2);
        GridLayout.Spec rowSpan = GridLayout.spec(0, 3);

        GridLayout.Spec col1 = GridLayout.spec(0);
        GridLayout.Spec col2 = GridLayout.spec(1);

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setRowCount(3);
        gridLayout.setColumnCount(2);

        //map column
        // still placeholder since map is not stored in server yet
        GridLayout.LayoutParams mapColumn = new GridLayout.LayoutParams(rowSpan, col1);
        mapColumn.width = screenWidth / 3;
        mapColumn.height = screenHeight / 5;
        TextView mapText = new TextView(this);
        mapText.setText("Map for hike " + Integer.toString(i + 1) + " goes here.");
        mapText.setLayoutParams(mapColumn);
        gridLayout.addView(mapText, mapColumn);

        //name row
        GridLayout.LayoutParams nameRow = new GridLayout.LayoutParams(row1, col2);
        TextView nameText = new TextView(this);
        String name = getResources().getString(R.string.hikeNumberText, hikeData.getHikeId());
        nameText.setText(name);
        nameText.setLayoutParams(nameRow);
        nameText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HikeInfoActivity.class);
                startActivity(intent);
            }
        });
        gridLayout.addView(nameText, nameRow);

        //distance row
        GridLayout.LayoutParams distanceRow = new GridLayout.LayoutParams(row2, col2);
        TextView distanceText = new TextView(this);
        distanceText.setText(getResources().getString(R.string.hikeDistanceText, (long) hikeData.getDistance() / 1000));
        distanceText.setLayoutParams(distanceRow);
        gridLayout.addView(distanceText, distanceRow);

        //rating row
        GridLayout.LayoutParams ratingRow = new GridLayout.LayoutParams(row3, col2);
        TextView ratingText = new TextView(this);
        ratingText.setText(getResources().getString(R.string.hikeRatingText, (long) hikeData.getRating().getDisplayRating()));

        ratingText.setLayoutParams(ratingRow);
        gridLayout.addView(ratingText, ratingRow);

        hikeRow.addView(gridLayout);
        return hikeRow;
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

        private void displayHikes(final List<HikeData> results) {
            TableLayout hikeListTable = (TableLayout)findViewById((R.id.hikeListTable));
            for (int i = 0; i < results.size(); i++) {
                final HikeData hikeData = results.get(i);
                final TableRow hikeRow = getHikeRow(i, hikeData);
                hikeRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), HikeInfoActivity.class);
                        i.putExtra(EXTRA_HIKE_ID, Long.toString(hikeData.getHikeId()));
                        startActivity(i);
                    }
                });
                hikeListTable.addView(hikeRow, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            }
        }
    }
}
